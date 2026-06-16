package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lhzkml.jasmineagent.core.ui.JasmineTheme
import com.lhzkml.jasmineagent.feature.agent.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AgentScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private val context
    get() = composeTestRule.activity

  @Before
  fun setup() {
    composeTestRule.setContent {
      JasmineTheme {
        AgentContent(
          items = FAKE_DATA,
          agentName = "",
          onAgentNameChange = {},
          onSave = {},
          addAgentState = AddAgentState.Idle,
        )
      }
    }
  }

  @Test
  fun firstItem_exists() {
    composeTestRule.onNodeWithText(FAKE_DATA.first()).assertExists()
  }

  @Test
  fun form_exposesTalkBackSemantics() {
    composeTestRule
      .onNodeWithTag(AgentSemantics.FORM)
      .assertContentDescriptionEquals(context.getString(R.string.agent_form_content_description))

    composeTestRule
      .onNodeWithTag(AgentSemantics.LIST)
      .assertContentDescriptionEquals(context.getString(R.string.agent_list_content_description))

    composeTestRule
      .onNodeWithTag(AgentSemantics.NAME_FIELD)
      .assertContentDescriptionEquals(context.getString(R.string.agent_field_name_label))

    composeTestRule
      .onNodeWithTag(AgentSemantics.SAVE_BUTTON)
      .assertContentDescriptionEquals(context.getString(R.string.agent_action_save))

    composeTestRule
      .onNodeWithContentDescription(
        context.getString(R.string.agent_list_item_content_description, FAKE_DATA.first())
      )
      .assertExists()
  }

  @Test
  fun emptyState_exposesTalkBackSemantics() {
    val emptyMessage = context.getString(R.string.agent_empty_message)

    composeTestRule.setContent {
      JasmineTheme {
        AgentContent(
          items = emptyList(),
          agentName = "",
          onAgentNameChange = {},
          onSave = {},
          addAgentState = AddAgentState.Idle,
        )
      }
    }

    composeTestRule.onNodeWithContentDescription(emptyMessage).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.EMPTY_STATE).assertExists()
  }

  @Test
  fun validationError_exposesTalkBackErrorSemantics() {
    val errorMessage = context.getString(R.string.agent_error_empty_name)

    composeTestRule.setContent {
      JasmineTheme {
        AgentContent(
          items = FAKE_DATA,
          agentName = "",
          onAgentNameChange = {},
          onSave = {},
          addAgentState = AddAgentState.Error(AddAgentError.EmptyName),
        )
      }
    }

    composeTestRule.onNodeWithText(errorMessage).assertExists()
    composeTestRule
      .onNodeWithTag(AgentSemantics.NAME_FIELD)
      .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, errorMessage))
  }

  @Test
  fun rtlLayout_keepsPrimaryControlsDiscoverable() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        JasmineTheme {
          AgentContent(
            items = FAKE_DATA,
            agentName = "",
            onAgentNameChange = {},
            onSave = {},
            addAgentState = AddAgentState.Idle,
          )
        }
      }
    }

    composeTestRule.onNodeWithTag(AgentSemantics.NAME_FIELD).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.SAVE_BUTTON).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.LIST).assertExists()
    composeTestRule.onNodeWithText(context.getString(R.string.agent_action_save)).assertExists()
  }
}

private val FAKE_DATA = listOf("Compose", "Room", "Kotlin")
