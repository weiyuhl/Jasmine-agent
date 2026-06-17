package com.lhzkml.jasmineagent.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.lhzkml.jasmineagent.R
import com.lhzkml.jasmineagent.feature.agent.navigation.AgentEntryProvider
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main

private data class AppDestination(val key: NavKey, @StringRes val labelResId: Int)

private val appDestinations =
  listOf(AppDestination(key = Main, labelResId = R.string.nav_agents_label))

object NavigationSemantics {
  const val TOP_APP_BAR = "main_top_app_bar"
  const val BOTTOM_NAVIGATION = "main_bottom_navigation"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  Scaffold(
    topBar = { JasmineTopAppBar() },
    bottomBar = { JasmineNavigationBar(backStack.lastOrNull(), backStack::navigateToTopLevel) },
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators =
          listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
          ),
        entryProvider = entryProvider { AgentEntryProvider() },
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JasmineTopAppBar() {
  TopAppBar(
    modifier = Modifier.testTag(NavigationSemantics.TOP_APP_BAR),
    title = { Text(text = stringResource(R.string.app_name)) },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
      ),
  )
}

@Composable
private fun JasmineNavigationBar(selectedKey: NavKey?, onDestinationSelected: (NavKey) -> Unit) {
  NavigationBar(
    modifier = Modifier.testTag(NavigationSemantics.BOTTOM_NAVIGATION),
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    appDestinations.forEach { destination ->
      val label = stringResource(destination.labelResId)
      val contentDescription = stringResource(R.string.nav_agents_content_description)

      NavigationBarItem(
        selected = selectedKey == destination.key,
        onClick = { onDestinationSelected(destination.key) },
        icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = contentDescription) },
        label = { Text(label) },
      )
    }
  }
}

private fun MutableList<NavKey>.navigateToTopLevel(destination: NavKey) {
  if (lastOrNull() == destination) return

  clear()
  add(destination)
}
