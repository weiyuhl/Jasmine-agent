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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.lhzkml.jasmineagent.core.designsystem.theme.AgentMaterialTheme
import com.lhzkml.jasmineagent.core.navigation.NavigationEntryRegistrar
import com.lhzkml.jasmineagent.diagnostics.CrashReporter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Suppress("LateinitUsage")
  @Inject
  lateinit var navigationEntryRegistrars: Set<@JvmSuppressWildcards NavigationEntryRegistrar>

  /** Deep link URI extracted from the launch intent, if any. */
  private var deepLinkUri by mutableStateOf<String?>(null)
  private var contentBreadcrumbRecorded = false

  override fun onCreate(savedInstanceState: Bundle?) {
    CrashReporter.recordBreadcrumb(this, "MainActivity.onCreate:start")
    enableTransparentSystemBars()
    CrashReporter.recordBreadcrumb(this, "MainActivity.edgeToEdge:applied")
    extractDeepLink(intent)
    CrashReporter.recordBreadcrumb(this, "MainActivity.deepLink:extracted")
    super.onCreate(savedInstanceState)
    CrashReporter.recordBreadcrumb(this, "MainActivity.superOnCreate:completed")
    setContent {
      if (!contentBreadcrumbRecorded) {
        contentBreadcrumbRecorded = true
        CrashReporter.recordBreadcrumb(this, "MainActivity.setContent:firstComposition")
      }
      AgentMaterialTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
          contentColor = MaterialTheme.colorScheme.onBackground,
        ) {
          MainNavigation(
            navigationEntryRegistrars = navigationEntryRegistrars,
            deepLinkUri = deepLinkUri,
          )
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    CrashReporter.recordBreadcrumb(this, "MainActivity.onNewIntent")
    super.onNewIntent(intent)
    extractDeepLink(intent)
  }

  private fun extractDeepLink(intent: Intent?) {
    deepLinkUri = intent?.data?.toString()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    CrashReporter.recordBreadcrumb(this, "MainActivity.onConfigurationChanged")
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
