package com.lhzkml.jasmineagent.ui

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
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
}
