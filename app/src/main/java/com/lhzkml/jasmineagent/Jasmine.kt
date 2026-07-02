package com.lhzkml.jasmineagent

import android.app.Application
import android.content.Context
import com.lhzkml.jasmineagent.diagnostics.CrashReporter
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Jasmine : Application() {

  override fun attachBaseContext(base: Context) {
    CrashReporter.install(base)
    CrashReporter.recordBreadcrumb(base, "Application.attachBaseContext")
    super.attachBaseContext(base)
  }

  override fun onCreate() {
    CrashReporter.recordBreadcrumb(this, "Application.onCreate:start")
    super.onCreate()
    CrashReporter.recordBreadcrumb(this, "Application.onCreate:end")
  }
}
