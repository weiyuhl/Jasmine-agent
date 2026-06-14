package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AgentScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent {
      AgentContent(
        items = FAKE_DATA,
        onSave = {},
        addAgentState = AddAgentState.Idle,
        onResetAddAgentState = {},
      )
    }
  }

  @Test
  fun firstItem_exists() {
    composeTestRule.onNodeWithText(FAKE_DATA.first()).assertExists()
  }
}

private val FAKE_DATA = listOf("Compose", "Room", "Kotlin")
