package com.lhzkml.jasmineagent.ui

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
  const val BOTTOM_NAVIGATION = "main_bottom_navigation"
  const val NAVIGATION_RAIL = "main_navigation_rail"
  const val ELASTIC_INDICATOR = "main_bottom_navigation_elastic_indicator"
  const val PAGER = "main_navigation_pager"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
  val pagerState = rememberPagerState(pageCount = { appDestinations.size })
  val coroutineScope = rememberCoroutineScope()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val selectedDestination = appDestinations[pagerState.currentPage]

  LaunchedEffect(selectedDestination.key) {
    if (selectedDestination.key != Main) {
      drawerState.close()
    }
  }

  BoxWithConstraints {
    val useNavigationRail = maxWidth >= NavigationRailBreakpoint
    val onDestinationSelected: (NavKey) -> Unit = { destination ->
      val page = appDestinations.indexOfFirst { it.key == destination }
      if (page >= 0 && page != pagerState.currentPage) {
        coroutineScope.launch { pagerState.animateScrollToPage(page) }
      }
    }

    Scaffold(
      topBar = {
        JasmineTopAppBar(
          showNavigationIcon = selectedDestination.key == Main,
          onNavigationClick = { coroutineScope.launch { drawerState.open() } },
        )
      },
      bottomBar = {
        if (!useNavigationRail) {
          JasmineNavigationBar(selectedDestination.key, onDestinationSelected)
        }
      },
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
      Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        if (useNavigationRail) {
          JasmineNavigationRail(selectedDestination.key, onDestinationSelected)
        }
        HorizontalPager(
          state = pagerState,
          modifier = Modifier.weight(1f).fillMaxSize().testTag(NavigationSemantics.PAGER),
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

@Composable
private fun DestinationNavHost(rootKey: NavKey) {
  val backStack = rememberNavBackStack(rootKey)

  Box(modifier = Modifier.fillMaxSize()) {
    NavDisplay(
      backStack = backStack,
      onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
      entryDecorators =
        listOf(
          rememberSaveableStateHolderNavEntryDecorator(),
          rememberViewModelStoreNavEntryDecorator(),
        ),
      entryProvider = entryProvider { AgentEntryProvider() },
    )
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

@Composable
private fun JasmineNavigationBar(selectedKey: NavKey?, onDestinationSelected: (NavKey) -> Unit) {
  val colorScheme = MaterialTheme.colorScheme
  val selectedIndex = appDestinations.indexOfFirst { it.key == selectedKey }.coerceAtLeast(0)

  BoxWithConstraints(modifier = Modifier.testTag(NavigationSemantics.BOTTOM_NAVIGATION)) {
    NavigationBar(
      modifier = Modifier.fillMaxWidth(),
      containerColor = colorScheme.surface,
      contentColor = colorScheme.onSurface,
    ) {
      appDestinations.forEach { destination ->
        val label = stringResource(destination.labelResId)
        val contentDescription = stringResource(destination.contentDescriptionResId)

        NavigationBarItem(
          selected = selectedKey == destination.key,
          onClick = { onDestinationSelected(destination.key) },
          icon = { Icon(imageVector = destination.icon, contentDescription = contentDescription) },
          label = { Text(label) },
          colors = jellyNavigationItemColors(colorScheme),
        )
      }
    }

    ElasticNavigationIndicator(
      selectedIndex = selectedIndex,
      destinationCount = appDestinations.size,
      maxWidth = maxWidth,
      color = colorScheme.primary,
    )
  }
}

@Composable
private fun JasmineNavigationRail(selectedKey: NavKey?, onDestinationSelected: (NavKey) -> Unit) {
  val colorScheme = MaterialTheme.colorScheme

  NavigationRail(
    modifier = Modifier.testTag(NavigationSemantics.NAVIGATION_RAIL),
    containerColor = colorScheme.surface,
    contentColor = colorScheme.onSurface,
  ) {
    appDestinations.forEach { destination ->
      val label = stringResource(destination.labelResId)
      val contentDescription = stringResource(destination.contentDescriptionResId)

      NavigationRailItem(
        selected = selectedKey == destination.key,
        onClick = { onDestinationSelected(destination.key) },
        icon = { Icon(imageVector = destination.icon, contentDescription = contentDescription) },
        label = { Text(label) },
      )
    }
  }
}

@Composable
private fun jellyNavigationItemColors(colorScheme: ColorScheme) =
  NavigationBarItemDefaults.colors(
    selectedIconColor = colorScheme.primary,
    selectedTextColor = colorScheme.primary,
    unselectedIconColor = colorScheme.onSurfaceVariant,
    unselectedTextColor = colorScheme.onSurfaceVariant,
    indicatorColor = Color.Transparent,
  )

@Composable
private fun BoxScope.ElasticNavigationIndicator(
  selectedIndex: Int,
  destinationCount: Int,
  maxWidth: Dp,
  color: Color,
) {
  val tabWidth = maxWidth / destinationCount
  val horizontalInset = tabWidth * IndicatorHorizontalInsetFraction
  val transition = updateTransition(targetState = selectedIndex, label = "bottom_nav_indicator")
  val left =
    transition.animateDp(
      transitionSpec = {
        tween(
          durationMillis =
            if (targetState > initialState) SlowEdgeDurationMillis else FastEdgeDurationMillis,
          easing = FastOutSlowInEasing,
        )
      },
      label = "indicator_left",
    ) { index ->
      indicatorLeft(index, tabWidth, horizontalInset)
    }
  val right =
    transition.animateDp(
      transitionSpec = {
        tween(
          durationMillis =
            if (targetState > initialState) FastEdgeDurationMillis else SlowEdgeDurationMillis,
          easing = FastOutSlowInEasing,
        )
      },
      label = "indicator_right",
    ) { index ->
      indicatorRight(index, tabWidth, horizontalInset)
    }
  val width = maxOf(right.value - left.value, 0.dp)

  Box(
    modifier =
      Modifier.align(Alignment.BottomStart)
        .offset {
          IntOffset(
            x = left.value.roundToPx(),
            y = -IndicatorBottomPadding.roundToPx(),
          )
        }
        .width(width)
        .height(IndicatorHeight)
        .clip(RoundedCornerShape(percent = 50))
        .testTag(NavigationSemantics.ELASTIC_INDICATOR)
        .background(color)
  )
}

private fun drawerItemTestTag(key: NavKey): String =
  when (key) {
    Main -> NavigationSemantics.DRAWER_ITEM_AGENTS
    BlankOne -> NavigationSemantics.DRAWER_ITEM_BLANK_ONE
    BlankTwo -> NavigationSemantics.DRAWER_ITEM_BLANK_TWO
    else -> error("Unsupported drawer destination: $key")
  }

private fun indicatorLeft(index: Int, tabWidth: Dp, horizontalInset: Dp): Dp =
  (tabWidth * index.toFloat()) + horizontalInset

private fun indicatorRight(index: Int, tabWidth: Dp, horizontalInset: Dp): Dp =
  (tabWidth * (index + 1).toFloat()) - horizontalInset

private const val IndicatorHorizontalInsetFraction = 0.32f
private const val FastEdgeDurationMillis = 170
private const val SlowEdgeDurationMillis = 430
private val NavigationRailBreakpoint = 600.dp
private val DrawerTopPadding = 12.dp
private val DrawerItemHorizontalPadding = 12.dp
private val IndicatorHeight = 5.dp
private val IndicatorBottomPadding = 7.dp
