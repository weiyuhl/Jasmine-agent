package com.lhzkml.jasmineagent.diagnostics

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

object CrashReporter {
  private const val TAG = "JasmineCrashReporter"
  private const val DIAGNOSTICS_DIR = "diagnostics"
  private const val DOWNLOADS_DIR = "JasmineAgent"
  private const val LATEST_CRASH_FILE = "jasmine-crash-latest.txt"
  private const val LATEST_EXIT_FILE = "jasmine-exit-latest.txt"
  private const val LATEST_STARTUP_FILE = "jasmine-startup-latest.txt"
  private const val MAX_BREADCRUMBS = 80
  private const val MAX_EXIT_TRACE_BYTES = 256 * 1024

  private val lock = Any()
  private val breadcrumbs = ArrayDeque<String>()

  @Volatile private var installed = false

  fun install(context: Context) {
    val appContext = context.applicationContext ?: context
    val previousHandler =
      synchronized(lock) {
        if (installed) return
        installed = true
        Thread.getDefaultUncaughtExceptionHandler()
      }

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
      try {
        recordBreadcrumb(appContext, "Uncaught exception on ${thread.name}", throwable)
        writeCrashReport(appContext, thread, throwable)
      } catch (reporterError: Throwable) {
        Log.e(TAG, "Failed to write crash report", reporterError)
      } finally {
        previousHandler?.uncaughtException(thread, throwable)
          ?: run {
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(10)
          }
      }
    }

    recordBreadcrumb(appContext, "CrashReporter installed")
    runCatching { writePreviousExitReportIfPresent(appContext) }
      .onFailure { Log.w(TAG, "Failed to inspect previous process exits", it) }
  }

  fun recordBreadcrumb(context: Context, message: String, throwable: Throwable? = null) {
    val appContext = context.applicationContext ?: context
    val entry = buildString {
      append(timestamp())
      append(" [")
      append(Thread.currentThread().name)
      append("] ")
      append(message)
      throwable?.let {
        append('\n')
        append(stackTraceToString(it))
      }
    }

    synchronized(lock) {
      breadcrumbs.addLast(entry)
      while (breadcrumbs.size > MAX_BREADCRUMBS) {
        breadcrumbs.removeFirst()
      }
    }

    Log.d(TAG, message, throwable)
    runCatching { writeStartupSnapshot(appContext) }
  }

  private fun writeCrashReport(context: Context, thread: Thread, throwable: Throwable) {
    val fileName = "JasmineAgent-crash-${fileTimestamp()}.txt"
    val report = buildString {
      appendHeader(context)
      appendLine("Crashed Thread: ${thread.name}")
      appendLine()
      appendLine("Breadcrumbs:")
      appendBreadcrumbs()
      appendLine()
      appendLine("Stacktrace:")
      appendLine(stackTraceToString(throwable))
    }

    writeAppFiles(context, fileName, report, LATEST_CRASH_FILE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      runCatching { writePublicDownloads(context, fileName, report) }
        .onFailure { Log.w(TAG, "Failed to write public crash report", it) }
    }
  }

  private fun writeStartupSnapshot(context: Context) {
    val report = buildString {
      appendHeader(context)
      appendLine("Breadcrumbs:")
      appendBreadcrumbs()
    }

    writeLatestOnly(context, LATEST_STARTUP_FILE, report)
  }

  @TargetApi(Build.VERSION_CODES.R)
  private fun writePreviousExitReportIfPresent(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

    val exitInfo =
      context
        .getSystemService(ActivityManager::class.java)
        ?.getHistoricalProcessExitReasons(context.packageName, 0, 5)
        ?.firstOrNull { it.isReportableExitReason() } ?: return
    val fileName = "JasmineAgent-exit-${fileTimestamp()}.txt"
    val report = buildString {
      appendHeader(context)
      appendLine("Previous Process Exit:")
      appendLine("Reason: ${exitInfo.reasonName()} (${exitInfo.reason})")
      appendLine("Timestamp: ${formatDate("yyyy-MM-dd HH:mm:ss.SSS", Date(exitInfo.timestamp))}")
      appendLine("Process: ${exitInfo.processName}")
      appendLine("Pid: ${exitInfo.pid}")
      appendLine("Status: ${exitInfo.status}")
      appendLine("Importance: ${exitInfo.importance}")
      appendLine("Description: ${exitInfo.description.orEmpty()}")
      appendLine()
      appendLine("Trace:")
      appendLine(exitInfo.traceText())
      appendLine()
      appendLine("Current Launch Breadcrumbs:")
      appendBreadcrumbs()
    }

    writeAppFiles(context, fileName, report, LATEST_EXIT_FILE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      runCatching { writePublicDownloads(context, fileName, report) }
        .onFailure { Log.w(TAG, "Failed to write public previous-exit report", it) }
    }
    recordBreadcrumb(context, "Previous process exit report written:${exitInfo.reasonName()}")
  }

  @TargetApi(Build.VERSION_CODES.R)
  private fun ApplicationExitInfo.isReportableExitReason(): Boolean =
    reason == ApplicationExitInfo.REASON_CRASH ||
      reason == ApplicationExitInfo.REASON_CRASH_NATIVE ||
      reason == ApplicationExitInfo.REASON_ANR ||
      reason == ApplicationExitInfo.REASON_INITIALIZATION_FAILURE

  @TargetApi(Build.VERSION_CODES.R)
  private fun ApplicationExitInfo.reasonName(): String =
    when (reason) {
      ApplicationExitInfo.REASON_CRASH -> "CRASH"
      ApplicationExitInfo.REASON_CRASH_NATIVE -> "CRASH_NATIVE"
      ApplicationExitInfo.REASON_ANR -> "ANR"
      ApplicationExitInfo.REASON_INITIALIZATION_FAILURE -> "INITIALIZATION_FAILURE"
      else -> "REASON_$reason"
    }

  @TargetApi(Build.VERSION_CODES.R)
  private fun ApplicationExitInfo.traceText(): String =
    runCatching {
        traceInputStream?.use { inputStream ->
          val outputStream = ByteArrayOutputStream()
          val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
          var remainingBytes = MAX_EXIT_TRACE_BYTES
          while (remainingBytes > 0) {
            val readSize = inputStream.read(buffer, 0, minOf(buffer.size, remainingBytes))
            if (readSize <= 0) break
            outputStream.write(buffer, 0, readSize)
            remainingBytes -= readSize
          }
          outputStream.toByteArray().toString(Charsets.UTF_8)
        }
      }
      .getOrNull()
      .orEmpty()
      .ifBlank { "No trace available." }

  private fun StringBuilder.appendHeader(context: Context) {
    appendLine("JasmineAgent Diagnostics")
    appendLine("Generated: ${timestamp()}")
    appendLine("Package: ${context.packageName}")
    appendLine("Version: ${appVersion(context)}")
    appendLine("Process: ${android.os.Process.myPid()}")
    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
    appendLine("Android: ${Build.VERSION.RELEASE} / API ${Build.VERSION.SDK_INT}")
    appendLine("ABIs: ${Build.SUPPORTED_ABIS.joinToString()}")
    appendLine()
  }

  private fun StringBuilder.appendBreadcrumbs() {
    val snapshot =
      synchronized(lock) {
        breadcrumbs.toList()
      }

    if (snapshot.isEmpty()) {
      appendLine("No breadcrumbs recorded.")
    } else {
      snapshot.forEach { appendLine(it) }
    }
  }

  private fun appVersion(context: Context): String =
    runCatching {
        val packageInfo =
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
              context.packageName,
              PackageManager.PackageInfoFlags.of(0),
            )
          } else {
            @Suppress("DEPRECATION") context.packageManager.getPackageInfo(context.packageName, 0)
          }
        val versionCode =
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
          } else {
            @Suppress("DEPRECATION") packageInfo.versionCode.toLong()
          }
        "${packageInfo.versionName} ($versionCode)"
      }
      .getOrElse { "unknown (${it.javaClass.simpleName})" }

  private fun writeAppFiles(context: Context, fileName: String, text: String, latestName: String) {
    val internalDir = File(context.filesDir, DIAGNOSTICS_DIR).apply { mkdirs() }
    File(internalDir, latestName).writeText(text)
    File(internalDir, fileName).writeText(text)

    context.getExternalFilesDir(null)?.let { baseDir ->
      val externalDir = File(baseDir, DIAGNOSTICS_DIR).apply { mkdirs() }
      File(externalDir, latestName).writeText(text)
      File(externalDir, fileName).writeText(text)
    }
  }

  private fun writeLatestOnly(context: Context, fileName: String, text: String) {
    val internalDir = File(context.filesDir, DIAGNOSTICS_DIR).apply { mkdirs() }
    File(internalDir, fileName).writeText(text)

    context.getExternalFilesDir(null)?.let { baseDir ->
      val externalDir = File(baseDir, DIAGNOSTICS_DIR).apply { mkdirs() }
      File(externalDir, fileName).writeText(text)
    }
  }

  private fun writePublicDownloads(context: Context, fileName: String, text: String) {
    val values =
      ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "text/plain")
        put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$DOWNLOADS_DIR")
        put(MediaStore.Downloads.IS_PENDING, 1)
      }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return

    try {
      resolver.openOutputStream(uri)?.use { outputStream ->
        outputStream.write(text.toByteArray(Charsets.UTF_8))
      }
      values.clear()
      values.put(MediaStore.Downloads.IS_PENDING, 0)
      resolver.update(uri, values, null, null)
    } catch (throwable: Throwable) {
      runCatching { resolver.delete(uri, null, null) }
      throw throwable
    }
  }

  private fun stackTraceToString(throwable: Throwable): String {
    val writer = StringWriter()
    throwable.printStackTrace(PrintWriter(writer))
    return writer.toString()
  }

  private fun timestamp(): String = formatDate("yyyy-MM-dd HH:mm:ss.SSS")

  private fun fileTimestamp(): String = formatDate("yyyyMMdd-HHmmss-SSS")

  private fun formatDate(pattern: String, date: Date = Date()): String =
    SimpleDateFormat(pattern, Locale.US).format(date)
}
