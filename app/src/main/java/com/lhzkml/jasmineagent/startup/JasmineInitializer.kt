package com.lhzkml.jasmineagent.startup

import android.content.Context
import androidx.profileinstaller.ProfileInstaller
import androidx.startup.Initializer
import java.util.concurrent.Executors

class JasmineInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    val executor = Executors.newSingleThreadExecutor()
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
}
