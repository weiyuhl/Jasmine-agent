package com.lhzkml.jasmineagent.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.lhzkml.jasmineagent.R
import com.lhzkml.jasmineagent.feature.agent.navigation.AgentEntryProvider
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankOne
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankTwo
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main
import kotlinx.coroutines.launch

private data class AppDestination(
  val key: NavKey,
  @StringRes val labelResId: Int,
  @StringRes val contentDescriptionResId: Int,
  val icon: ImageVector,
)

private val appDestinations =
  listOf(
    AppDestination(
      key = Main,
      labelResId = R.string.nav_agents_label,
      contentDescriptionResId = R.string.nav_agents_content_description,
      icon = Icons.Filled.Home,
    ),
    AppDestination(
      key = BlankOne,
      labelResId = R.string.nav_blank_one_label,
      contentDescriptionResId = R.string.nav_blank_one_content_description,
      icon = Icons.Filled.Add,
    ),
    AppDestination(
      key = BlankTwo,
      labelResId = R.string.nav_blank_two_label,
      contentDescriptionResId = R.string.nav_blank_two_content_description,
      icon = Icons.Filled.Info,
    ),
  )

object NavigationSemantics {
  const val TOP_APP_BAR = "main_top_app_bar"
  const val DRAWER = "main_navigation_drawer"
  const val DRAWER_OPEN_BUTTON = "main_navigation_drawer_open_button"
  const val DRAWER_ITEM_AGENTS = "main_navigation_drawer_item_agents"
  const val DRAWER_ITEM_BLANK_ONE = "main_navigation_drawer_item_blank_one"
  const val DRAWER_ITEM_BLANK_TWO = "main_navigation_drawer_item_blank_two"
  const val NAVIGATION_SUITE = "main_navigation_suite"
  const val PAGER = "main_navigation_pager"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainNavigation(deepLinkUri: String? = null) {
  val pagerState = rememberPagerState(pageCount = { appDestinations.size })
  val coroutineScope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val selectedDestination = appDestinations[pagerState.currentPage]
  val navSuiteState = rememberNavigationSuiteScaffoldState()

  // Handle deep links on first composition
  LaunchedEffect(deepLinkUri) {
    when (deepLinkUri) {
      BlankOne.DEEP_LINK -> {
        val page = appDestinations.indexOfFirst { it.key == BlankOne }
        if (page >= 0) pagerState.animateScrollToPage(page)
      }
      BlankTwo.DEEP_LINK -> {
        val page = appDestinations.indexOfFirst { it.key == BlankTwo }
        if (page >= 0) pagerState.animateScrollToPage(page)
      }
    }
  }

  LaunchedEffect(selectedDestination.key) {
    if (selectedDestination.key != Main) {
      drawerState.close()
    }
  }

  Scaffold(
    topBar = {
      JasmineTopAppBar(
        showNavigationIcon = selectedDestination.key == Main,
        onNavigationClick = { coroutineScope.launch { drawerState.open() } },
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
  ) { innerPadding ->
    NavigationSuiteScaffold(
      modifier = Modifier.padding(innerPadding).testTag(NavigationSemantics.NAVIGATION_SUITE),
      state = navSuiteState,
      navigationSuiteItems = {
        appDestinations.forEach { destination ->
          item(
            selected = selectedDestination.key == destination.key,
            onClick = {
              val page = appDestinations.indexOfFirst { it.key == destination.key }
              if (page >= 0 && page != pagerState.currentPage) {
                coroutineScope.launch { pagerState.animateScrollToPage(page) }
              }
            },
            icon = {
              Icon(
                imageVector = destination.icon,
                contentDescription = stringResource(destination.contentDescriptionResId),
              )
            },
            label = { Text(stringResource(destination.labelResId)) },
          )
        }
      },
    ) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize().testTag(NavigationSemantics.PAGER),
      ) { page ->
        DestinationPage(
          destination = appDestinations[page],
          drawerState = drawerState,
          onDrawerDestinationSelected = { destination ->
            val destinationPage = appDestinations.indexOfFirst { it.key == destination }
            if (destinationPage >= 0) {
              coroutineScope.launch {
                drawerState.close()
                if (destinationPage != pagerState.currentPage) {
                  pagerState.animateScrollToPage(destinationPage)
                }
              }
            }
          },
        )
      }
    }
  }
}

@Composable
private fun DestinationPage(
  destination: AppDestination,
  drawerState: DrawerState,
  onDrawerDestinationSelected: (NavKey) -> Unit,
) {
  if (destination.key == Main) {
    DismissibleNavigationDrawer(
      drawerState = drawerState,
      gesturesEnabled = drawerState.isOpen,
      drawerContent = {
        DismissibleDrawerSheet(
          drawerState = drawerState,
          modifier = Modifier.testTag(NavigationSemantics.DRAWER),
        ) {
          Column(Modifier.verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(DrawerTopPadding))
            appDestinations.forEach { drawerDestination ->
              NavigationDrawerItem(
                icon = { Icon(drawerDestination.icon, contentDescription = null) },
                label = { Text(stringResource(drawerDestination.labelResId)) },
                selected = drawerDestination.key == destination.key,
                onClick = { onDrawerDestinationSelected(drawerDestination.key) },
                modifier =
                  Modifier.padding(horizontal = DrawerItemHorizontalPadding)
                    .testTag(drawerItemTestTag(drawerDestination.key)),
              )
            }
          }
        }
      },
      content = { DestinationNavHost(destination.key) },
    )
  } else {
    DestinationNavHost(destination.key)
  }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun DestinationNavHost(rootKey: NavKey) {
  val backStack = rememberNavBackStack(rootKey)
  val entryProvider = entryProvider { AgentEntryProvider() }

  Box(modifier = Modifier.fillMaxSize()) {
    if (rootKey == Main) {
      val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
      val directive =
        remember(windowAdaptiveInfo) {
          calculatePaneScaffoldDirective(windowAdaptiveInfo).copy(horizontalPartitionSpacerSize = 0.dp)
        }
      val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

      NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailSceneStrategy),
        entryDecorators =
          listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
          ),
        entryProvider = entryProvider,
      )
    } else {
      NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryDecorators =
          listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
          ),
        entryProvider = entryProvider,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JasmineTopAppBar(showNavigationIcon: Boolean, onNavigationClick: () -> Unit) {
  TopAppBar(
    modifier = Modifier.testTag(NavigationSemantics.TOP_APP_BAR),
    title = { Text(text = stringResource(R.string.app_name)) },
    navigationIcon = {
      if (showNavigationIcon) {
        IconButton(
          modifier = Modifier.testTag(NavigationSemantics.DRAWER_OPEN_BUTTON),
          onClick = onNavigationClick,
        ) {
          Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(R.string.nav_drawer_open_content_description),
          )
        }
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
      ),
  )
}

private fun drawerItemTestTag(key: NavKey): String =
  when (key) {
    Main -> NavigationSemantics.DRAWER_ITEM_AGENTS
    BlankOne -> NavigationSemantics.DRAWER_ITEM_BLANK_ONE
    BlankTwo -> NavigationSemantics.DRAWER_ITEM_BLANK_TWO
    else -> error("Unsupported drawer destination: $key")
  }

private val DrawerTopPadding = 12.dp
private val DrawerItemHorizontalPadding = 12.dp
