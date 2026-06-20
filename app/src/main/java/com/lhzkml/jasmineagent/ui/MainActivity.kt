package com.lhzkml.jasmineagent.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.lhzkml.jasmine.components.Surface
import com.lhzkml.jasmine.theme.JasmineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  /** Deep link URI extracted from the launch intent, if any. */
  private var deepLinkUri: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    enableTransparentSystemBars()
    extractDeepLink(intent)
    super.onCreate(savedInstanceState)
    setContent {
      JasmineTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = JasmineTheme.colorScheme.background,
          contentColor = JasmineTheme.colorScheme.onBackground,
        ) {
          MainNavigation(deepLinkUri = deepLinkUri)
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    extractDeepLink(intent)
  }

  private fun extractDeepLink(intent: Intent?) {
    deepLinkUri = intent?.data?.toString()
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      window.isNavigationBarContrastEnforced = false
    }
  }
}
