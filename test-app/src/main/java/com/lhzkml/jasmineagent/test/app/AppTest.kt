package com.lhzkml.jasmineagent.test.app

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.lhzkml.jasmineagent.feature.agent.ui.AgentSemantics
import com.lhzkml.jasmineagent.ui.MainActivity
import com.lhzkml.jasmineagent.ui.NavigationSemantics
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AppTest {
  @get:Rule(order = 0) var hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

  private val device: UiDevice
    get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

  @Test
  fun mainScreen_displaysFakeAgents() {
    composeTestRule.onNodeWithText("One", substring = true).assertExists()
  }

  @Test
  fun addAgent_displaysInList() {
    val agentName = "E2E Test Agent"

    composeTestRule.onNodeWithTag(AgentSemantics.NAME_FIELD).performTextInput(agentName)
    composeTestRule.onNodeWithTag(AgentSemantics.SAVE_BUTTON).performClick()

    composeTestRule.onNodeWithText(agentName).assertExists()
  }

  @Test
  fun navigation_tabs_switchCorrectly() {
    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_blank_one_label)
      )
      .performClick()

    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_blank_two_label)
      )
      .performClick()

    composeTestRule
      .onNodeWithText(
        composeTestRule.activity.getString(com.lhzkml.jasmineagent.R.string.nav_agents_label)
      )
      .performClick()

    // Should be back on the agents screen
    composeTestRule.onNodeWithTag(AgentSemantics.SAVE_BUTTON).assertExists()
  }

  @Test
  fun navigationSuite_isVisible() {
    composeTestRule.onNodeWithTag(NavigationSemantics.NAVIGATION_SUITE).assertExists()
  }

  @Test
  fun topAppBar_isVisible() {
    composeTestRule.onNodeWithTag(NavigationSemantics.TOP_APP_BAR).assertExists()
  }

  @Test
  fun app_isForegroundForEndToEndJourney() {
    val packageName =
      ApplicationProvider.getApplicationContext<android.content.Context>().packageName

    device.wait(Until.hasObject(By.pkg(packageName).depth(0)), LaunchTimeoutMillis)

    composeTestRule.onNodeWithTag(NavigationSemantics.TOP_APP_BAR).assertExists()
  }
}

private const val LaunchTimeoutMillis = 5_000L
