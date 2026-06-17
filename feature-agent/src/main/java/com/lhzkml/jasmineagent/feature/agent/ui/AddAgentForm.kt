package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.lhzkml.jasmineagent.core.ui.JasmineBlack
import com.lhzkml.jasmineagent.core.ui.JasmineBodyLarge
import com.lhzkml.jasmineagent.core.ui.JasmineBorder
import com.lhzkml.jasmineagent.core.ui.JasmineContainer
import com.lhzkml.jasmineagent.core.ui.JasmineError
import com.lhzkml.jasmineagent.core.ui.JasmineLabelLarge
import com.lhzkml.jasmineagent.core.ui.JasmineTextMuted
import com.lhzkml.jasmineagent.core.ui.JasmineWhite
import com.lhzkml.jasmineagent.feature.agent.R

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
internal fun AddAgentForm(
  nameAgent: String,
  addAgentState: AddAgentState,
  onNameChange: (String) -> Unit,
  onSave: () -> Unit,
) {
  val resources = LocalResources.current
  val validationError = (addAgentState as? AddAgentState.Error)?.error?.message(resources)
  val agentNameLabel = stringResource(R.string.agent_field_name_label)
  val saveLabel = stringResource(R.string.agent_action_save)

  FlexBox(
    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
    config = {
      direction(FlexDirection.Row)
      wrap(FlexWrap.Wrap)
      alignItems(FlexAlignItems.Center)
      gap(16.dp)
    },
  ) {
    AgentNameField(
      nameAgent = nameAgent,
      validationError = validationError,
      agentNameLabel = agentNameLabel,
      onNameChange = onNameChange,
      enabled = addAgentState !is AddAgentState.Adding,
      modifier =
        Modifier.widthIn(min = 220.dp).flex {
          grow(1f)
          shrink(1f)
        },
    )

    SaveAgentButton(
      saveLabel = saveLabel,
      addAgentState = addAgentState,
      enabled = addAgentState !is AddAgentState.Adding && nameAgent.isNotBlank(),
      onSave = onSave,
      modifier = Modifier.width(96.dp).flex { shrink(0f) },
    )
  }
}

@Composable
private fun AgentNameField(
  nameAgent: String,
  validationError: String?,
  agentNameLabel: String,
  onNameChange: (String) -> Unit,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  TextField(
    modifier =
      modifier.fillMaxWidth().testTag(AgentSemantics.NAME_FIELD).semantics {
        contentDescription = agentNameLabel
        validationError?.let { error(it) }
      },
    value = nameAgent,
    onValueChange = onNameChange,
    textStyle = JasmineBodyLarge,
    label = { Text(agentNameLabel, style = JasmineLabelLarge, color = JasmineTextMuted) },
    isError = validationError != null,
    supportingText =
      validationError?.let {
        {
          Text(
            text = it,
            modifier =
              Modifier.semantics {
                error(it)
                liveRegion = LiveRegionMode.Assertive
              },
            color = JasmineError,
          )
        }
      },
    colors =
      TextFieldDefaults.colors(
        focusedTextColor = JasmineBlack,
        unfocusedTextColor = JasmineBlack,
        disabledTextColor = JasmineTextMuted,
        errorTextColor = JasmineBlack,
        focusedContainerColor = JasmineWhite,
        unfocusedContainerColor = JasmineWhite,
        disabledContainerColor = JasmineContainer,
        errorContainerColor = JasmineWhite,
        cursorColor = JasmineBlack,
        errorCursorColor = JasmineError,
        focusedIndicatorColor = JasmineBlack,
        unfocusedIndicatorColor = JasmineBorder,
        disabledIndicatorColor = JasmineBorder,
        errorIndicatorColor = JasmineError,
        focusedLabelColor = JasmineBlack,
        unfocusedLabelColor = JasmineTextMuted,
        disabledLabelColor = JasmineTextMuted,
        errorLabelColor = JasmineError,
        focusedSupportingTextColor = JasmineTextMuted,
        unfocusedSupportingTextColor = JasmineTextMuted,
        disabledSupportingTextColor = JasmineTextMuted,
        errorSupportingTextColor = JasmineError,
      ),
    enabled = enabled,
  )
}

@Composable
private fun SaveAgentButton(
  saveLabel: String,
  addAgentState: AddAgentState,
  enabled: Boolean,
  onSave: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Button(
    modifier =
      modifier.testTag(AgentSemantics.SAVE_BUTTON).semantics {
        role = Role.Button
        contentDescription = saveLabel
      },
    onClick = onSave,
    enabled = enabled,
    colors =
      ButtonDefaults.buttonColors(
        containerColor = JasmineBlack,
        contentColor = JasmineWhite,
        disabledContainerColor = JasmineBlack.copy(alpha = DisabledContainerAlpha),
        disabledContentColor = JasmineWhite.copy(alpha = DisabledContentAlpha),
      ),
  ) {
    if (addAgentState is AddAgentState.Adding) {
      CircularProgressIndicator(modifier = Modifier.width(24.dp), color = JasmineWhite)
    } else {
      Text(
        text = saveLabel,
        color = if (enabled) JasmineWhite else JasmineWhite.copy(alpha = DisabledContentAlpha),
        style = JasmineLabelLarge,
      )
    }
  }
}

private const val DisabledContainerAlpha = 0.38f
private const val DisabledContentAlpha = 0.74f
