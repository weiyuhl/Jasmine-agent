package com.lhzkml.jasmineagent.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lhzkml.jasmineagent.core.designsystem.theme.AgentMaterialTheme
import com.lhzkml.jasmineagent.core.navigation.BlankOne
import com.lhzkml.jasmineagent.core.navigation.BlankTwo
import com.lhzkml.jasmineagent.core.navigation.Main
import com.lhzkml.jasmineagent.core.navigation.NavigationEntryRegistrar
import com.lhzkml.jasmineagent.feature.agent.R
import com.lhzkml.jasmineagent.feature.agent.navigation.BlankDestinationSemantics
import com.lhzkml.jasmineagent.feature.agent.ui.AgentSemantics
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NavigationTest {

  @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun mainScreen_showsSaveButton() {
    composeTestRule
      .onNodeWithText(composeTestRule.activity.getString(R.string.agent_action_save))
      .assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.SAVE_BUTTON).assertExists()
  }

  @Test
  fun mainScreen_showsAppBars() {
    composeTestRule.onNodeWithTag(NavigationSemantics.TOP_APP_BAR).assertExists()
    composeTestRule.onNodeWithTag(NavigationSemantics.BOTTOM_NAVIGATION).assertExists()
    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_agents_label)
      )
      .assertExists()
    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_blank_one_label)
      )
      .assertExists()
    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_blank_two_label)
      )
      .assertExists()
  }

  @Test
  fun bottomNavigation_opensBlankTabs() {
    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_blank_one_label)
      )
      .performClick()
    composeTestRule.onNodeWithTag(BlankDestinationSemantics.BLANK_ONE).assertExists()

    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_blank_two_label)
      )
      .performClick()
    composeTestRule.onNodeWithTag(BlankDestinationSemantics.BLANK_TWO).assertExists()
  }

  @Test
  fun drawer_opensBlankTab() {
    composeTestRule.onNodeWithTag(NavigationSemantics.DRAWER_OPEN_BUTTON).performClick()
    composeTestRule.onNodeWithTag(NavigationSemantics.DRAWER).assertExists()

    composeTestRule.onNodeWithTag(NavigationSemantics.DRAWER_ITEM_BLANK_ONE).performClick()

    composeTestRule.onNodeWithTag(BlankDestinationSemantics.BLANK_ONE).assertExists()
  }

  @Test
  fun pagerSwipe_opensBlankTabs() {
    composeTestRule.onNodeWithTag(NavigationSemantics.PAGER).performTouchInput { swipeLeft() }
    composeTestRule.onNodeWithTag(BlankDestinationSemantics.BLANK_ONE).assertExists()

    composeTestRule.onNodeWithTag(NavigationSemantics.PAGER).performTouchInput { swipeLeft() }
    composeTestRule.onNodeWithTag(BlankDestinationSemantics.BLANK_TWO).assertExists()
  }

  @Test
  fun deepLink_opensMatchingTab() {
    composeTestRule.setContent {
      AgentMaterialTheme {
        MainNavigation(
          navigationEntryRegistrars = setOf(TestNavigationEntryRegistrar()),
          deepLinkUri = BlankOne.DEEP_LINK,
        )
      }
    }

    composeTestRule.onNodeWithTag(BlankDestinationSemantics.BLANK_ONE).assertExists()
  }

  @Test
  fun expandedWidth_keepsBottomNavigation() {
    composeTestRule.setContent {
      Box(Modifier.requiredSize(900.dp, 700.dp)) {
        AgentMaterialTheme {
          MainNavigation(navigationEntryRegistrars = setOf(TestNavigationEntryRegistrar()))
        }
      }
    }

    composeTestRule.onNodeWithTag(NavigationSemantics.BOTTOM_NAVIGATION).assertExists()
  }
}

private class TestNavigationEntryRegistrar : NavigationEntryRegistrar {
  @SuppressLint("ComposableNaming")
  @Composable
  override fun EntryProviderScope<NavKey>.registerEntries() {
    entry<Main> { Box(Modifier.fillMaxSize()) }
    entry<BlankOne> {
      Box(Modifier.fillMaxSize().testTag(BlankDestinationSemantics.BLANK_ONE))
    }
    entry<BlankTwo> {
      Box(Modifier.fillMaxSize().testTag(BlankDestinationSemantics.BLANK_TWO))
    }
  }
}
