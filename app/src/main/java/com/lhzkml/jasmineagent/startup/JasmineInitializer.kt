package com.lhzkml.jasmineagent.startup

import android.content.Context
import androidx.profileinstaller.ProfileInstaller
import androidx.startup.Initializer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class JasmineInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    val executor = profileExecutor()
    ProfileInstaller.writeProfile(
      context.applicationContext,
      executor,
      object : ProfileInstaller.DiagnosticsCallback {
        override fun onDiagnosticReceived(code: Int, data: Any?) = Unit

        override fun onResultReceived(code: Int, data: Any?) {
          executor.shutdown()
        }
      },
    )
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
