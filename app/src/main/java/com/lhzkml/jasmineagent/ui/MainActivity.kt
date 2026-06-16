package com.lhzkml.jasmineagent.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lhzkml.jasmineagent.core.ui.JasmineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    enableTransparentSystemBars()
    super.onCreate(savedInstanceState)
    setContent {
      JasmineTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavigation()
        }
      }
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    enableTransparentSystemBars()
  }

  private fun enableTransparentSystemBars() {
    val transparent = android.graphics.Color.TRANSPARENT
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(scrim = transparent, darkScrim = transparent),
      navigationBarStyle = SystemBarStyle.light(scrim = transparent, darkScrim = transparent),
    )
  }
}
