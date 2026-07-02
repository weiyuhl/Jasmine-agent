package com.lhzkml.jasmineagent.startup

import android.content.Context
import androidx.profileinstaller.ProfileInstaller
import androidx.startup.Initializer
import com.lhzkml.jasmineagent.diagnostics.CrashReporter
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class JasmineInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    CrashReporter.recordBreadcrumb(context, "JasmineInitializer.create:start")
    val executor = profileExecutor()
    try {
      ProfileInstaller.writeProfile(
        context.applicationContext,
        executor,
        object : ProfileInstaller.DiagnosticsCallback {
          override fun onDiagnosticReceived(code: Int, data: Any?) {
            CrashReporter.recordBreadcrumb(context, "ProfileInstaller.diagnostic:$code")
          }

          override fun onResultReceived(code: Int, data: Any?) {
            CrashReporter.recordBreadcrumb(context, "ProfileInstaller.result:$code")
            executor.shutdown()
          }
        },
      )
      CrashReporter.recordBreadcrumb(context, "JasmineInitializer.create:scheduled")
    } catch (throwable: Throwable) {
      CrashReporter.recordBreadcrumb(context, "JasmineInitializer.create:failed", throwable)
      executor.shutdown()
      throw throwable
    }
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

  private fun profileExecutor(): ThreadPoolExecutor =
    ThreadPoolExecutor(
      0,
      1,
      PROFILE_EXECUTOR_KEEP_ALIVE_SECONDS,
      TimeUnit.SECONDS,
      LinkedBlockingQueue(),
      ThreadFactory { runnable ->
        Thread(runnable, PROFILE_EXECUTOR_THREAD_NAME).apply { isDaemon = true }
      },
    )

  private companion object {
    const val PROFILE_EXECUTOR_KEEP_ALIVE_SECONDS = 30L
    const val PROFILE_EXECUTOR_THREAD_NAME = "JasmineProfileInstaller"
  }
}
