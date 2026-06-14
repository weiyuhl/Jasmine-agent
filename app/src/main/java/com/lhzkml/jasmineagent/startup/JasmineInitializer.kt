package com.lhzkml.jasmineagent.startup

import android.content.Context
import androidx.startup.Initializer

class JasmineInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    // Application initialization logic here
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
