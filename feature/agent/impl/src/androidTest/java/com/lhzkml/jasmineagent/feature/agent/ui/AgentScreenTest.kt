package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecordStatus
import com.lhzkml.jasmineagent.core.ui.theme.AgentMaterialTheme
import com.lhzkml.jasmineagent.feature.agent.R
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
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
      AgentMaterialTheme {
        AgentContent(
          items = FAKE_DATA,
          agentName = "",
          addAgentState = AddAgentState.Idle,
          actions = NoOpActions,
        )
      }
    }
  }

  @Test
  fun firstItem_exists() {
    composeTestRule.onNodeWithText(FAKE_DATA.first().name).assertExists()
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
      .onNodeWithTag(AgentSemantics.deleteButton(FAKE_DATA.first().uid))
      .assertContentDescriptionEquals(
        context.getString(R.string.agent_action_delete_content_description, FAKE_DATA.first().name)
      )

    composeTestRule
      .onNodeWithContentDescription(
        context.getString(R.string.agent_list_item_content_description, FAKE_DATA.first().name)
      )
      .assertExists()
  }

  @Test
  fun deleteButton_invokesDeleteAction() {
    val deleted = AtomicReference<Pair<Int, String>?>()

    composeTestRule.setContent {
      AgentMaterialTheme {
        AgentContent(
          items = FAKE_DATA,
          agentName = "",
          addAgentState = AddAgentState.Idle,
          actions = NoOpActions.copy(onDelete = { uid, name -> deleted.set(uid to name) }),
        )
      }
    }

    composeTestRule.onNodeWithTag(AgentSemantics.deleteButton(FAKE_DATA.first().uid)).performClick()

    assertEquals(FAKE_DATA.first().uid to FAKE_DATA.first().name, deleted.get())
  }

  @Test
  fun emptyState_exposesTalkBackSemantics() {
    val emptyMessage = context.getString(R.string.agent_empty_message)

    composeTestRule.setContent {
      AgentMaterialTheme {
        AgentContent(
          items = emptyList(),
          agentName = "",
          addAgentState = AddAgentState.Idle,
          actions = NoOpActions,
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
      AgentMaterialTheme {
        AgentContent(
          items = FAKE_DATA,
          agentName = "",
          addAgentState = AddAgentState.Error(AddAgentError.EmptyName),
          actions = NoOpActions,
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
        AgentMaterialTheme {
          AgentContent(
            items = FAKE_DATA,
            agentName = "",
            addAgentState = AddAgentState.Idle,
            actions = NoOpActions,
          )
        }
      }
    }

    composeTestRule.onNodeWithTag(AgentSemantics.NAME_FIELD).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.SAVE_BUTTON).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.LIST).assertExists()
    composeTestRule.onNodeWithText(context.getString(R.string.agent_action_save)).assertExists()
  }

  @Test
  fun expandedWidthAndLargeFont_keepPrimaryControlsDiscoverable() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 1.5f)) {
        Box(Modifier.requiredSize(900.dp, 700.dp)) {
          AgentMaterialTheme {
            AgentContent(
              items = FAKE_DATA,
              agentName = "",
              addAgentState = AddAgentState.Idle,
              actions = NoOpActions,
            )
          }
        }
      }
    }

    composeTestRule.onNodeWithTag(AgentSemantics.NAME_FIELD).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.SAVE_BUTTON).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.LIST).assertExists()
    composeTestRule.onNodeWithTag(AgentSemantics.deleteButton(FAKE_DATA.first().uid)).assertExists()
  }
}

private val FAKE_DATA =
  listOf(
    agentRecord(1, "Compose"),
    agentRecord(2, "Room"),
    agentRecord(3, "Kotlin"),
  )

private val NoOpActions =
  AgentContentActions(
    onAgentNameChange = {},
    onSave = {},
    onDelete = { _, _ -> },
  )

private fun agentRecord(uid: Int, name: String): AgentRecord =
  AgentRecord(
    uid = uid,
    name = name,
    createdAt = 0L,
    updatedAt = 0L,
    status = AgentRecordStatus.ACTIVE,
    description = null,
  )
