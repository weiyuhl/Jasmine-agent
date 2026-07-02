package com.lhzkml.jasmineagent.platform.files

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

enum class PlatformFileScope {
  Files,
  Cache,
  NoBackup,
  ExternalFiles,
  ExternalCache,
}

interface AppFileGateway {
  fun directory(scope: PlatformFileScope): File

  fun resolve(scope: PlatformFileScope, relativePath: String): File

  fun createTempFile(
    prefix: String,
    suffix: String,
    scope: PlatformFileScope = PlatformFileScope.Cache,
  ): File

  fun contentUriFor(file: File): Uri

  fun readText(uri: Uri): String

  fun writeText(uri: Uri, text: String, mode: String = "w")
}

class AndroidAppFileGateway @Inject constructor(@ApplicationContext private val context: Context) :
  AppFileGateway {

  override fun directory(scope: PlatformFileScope): File =
    when (scope) {
      PlatformFileScope.Files -> context.filesDir
      PlatformFileScope.Cache -> context.cacheDir
      PlatformFileScope.NoBackup -> context.noBackupFilesDir
      PlatformFileScope.ExternalFiles -> context.getExternalFilesDir(null) ?: context.filesDir
      PlatformFileScope.ExternalCache -> context.externalCacheDir ?: context.cacheDir
    }.also { it.mkdirs() }

  override fun resolve(scope: PlatformFileScope, relativePath: String): File =
    directory(scope).resolveInside(relativePath)

  override fun createTempFile(prefix: String, suffix: String, scope: PlatformFileScope): File =
    File.createTempFile(prefix, suffix, directory(scope))

  override fun contentUriFor(file: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.files", file)

  override fun readText(uri: Uri): String =
    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
      ?: error("Unable to open input stream for $uri")

  override fun writeText(uri: Uri, text: String, mode: String) {
    context.contentResolver.openOutputStream(uri, mode)?.bufferedWriter()?.use { it.write(text) }
      ?: error("Unable to open output stream for $uri")
  }
}

private fun File.resolveInside(relativePath: String): File {
  val candidate = File(this, relativePath).canonicalFile
  val root = canonicalFile
  require(candidate.path == root.path || candidate.path.startsWith(root.path + File.separator)) {
    "Path escapes platform file root: $relativePath"
  }
  candidate.parentFile?.mkdirs()
  return candidate
}
