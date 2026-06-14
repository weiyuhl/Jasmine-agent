package com.lhzkml.jasmineagent.feature.agent.ui

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhzkml.jasmineagent.core.ui.JasmineTheme
import com.lhzkml.jasmineagent.feature.agent.R
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Error
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Loading
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Success

@Composable
fun AgentScreen(modifier: Modifier = Modifier, viewModel: AgentViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val addAgentState by viewModel.addAgentState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val resources = LocalResources.current
  val screenContentDescription = stringResource(R.string.agent_screen_content_description)
  val snackbarContentDescription = stringResource(R.string.agent_snackbar_content_description)

  LaunchedEffect(resources, viewModel) {
    viewModel.events.collect { event ->
      when (event) {
        is AgentEvent.ShowError -> snackbarHostState.showSnackbar(event.error.message(resources))
        is AgentEvent.AgentAdded ->
          snackbarHostState.showSnackbar(
            resources.getString(R.string.agent_added_message, event.name)
          )
      }
    }
  }

  Box(
    modifier =
      modifier.fillMaxSize().testTag(AgentSemantics.SCREEN).semantics {
        contentDescription = screenContentDescription
      }
  ) {
    when (val s = state) {
      is Loading -> LoadingContent(Modifier.fillMaxSize())
      is Error ->
        ErrorContent(
          s.throwable.message,
          s.canRetry,
          viewModel::retryLoadAgents,
          Modifier.fillMaxSize(),
        )
      is Success ->
        AgentContent(
          items = s.data,
          onSave = viewModel::addAgent,
          addAgentState = addAgentState,
          onResetAddAgentState = viewModel::resetAddAgentState,
          modifier = Modifier.fillMaxSize(),
        )
    }

    SnackbarHost(
      hostState = snackbarHostState,
      modifier =
        Modifier.align(Alignment.BottomCenter)
          .padding(16.dp)
          .testTag(AgentSemantics.SNACKBAR)
          .semantics {
            contentDescription = snackbarContentDescription
            liveRegion = LiveRegionMode.Polite
          },
    )
  }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
  val contentDescription = stringResource(R.string.agent_loading_content_description)

  Box(
    modifier =
      modifier.safeDrawingPadding().testTag(AgentSemantics.LOADING).semantics {
        this.contentDescription = contentDescription
        liveRegion = LiveRegionMode.Polite
      },
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun ErrorContent(
  message: String?,
  canRetry: Boolean,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val fallbackMessage = stringResource(R.string.agent_error_unknown)
  val retryLabel = stringResource(R.string.agent_action_retry)
  val errorContentDescription = stringResource(R.string.agent_error_content_description)
  val resolvedMessage = message ?: fallbackMessage

  Box(modifier = modifier.safeDrawingPadding(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = resolvedMessage,
        modifier =
          Modifier.testTag(AgentSemantics.ERROR_MESSAGE).semantics {
            contentDescription = errorContentDescription
            error(resolvedMessage)
            liveRegion = LiveRegionMode.Assertive
          },
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
      )
      if (canRetry) {
        Button(
          modifier =
            Modifier.testTag(AgentSemantics.RETRY_BUTTON).semantics {
              role = Role.Button
              contentDescription = retryLabel
            },
          onClick = onRetry,
        ) {
          Text(retryLabel)
        }
      }
    }
  }
}

@Composable
internal fun AgentContent(
  items: List<String>,
  onSave: (name: String) -> Unit,
  addAgentState: AddAgentState,
  onResetAddAgentState: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val resources = LocalResources.current
  val formContentDescription = stringResource(R.string.agent_form_content_description)

  Column(
    modifier.safeDrawingPadding().padding(16.dp).testTag(AgentSemantics.FORM).semantics {
      contentDescription = formContentDescription
    }
  ) {
    var nameAgent by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(addAgentState) {
      when (addAgentState) {
        is AddAgentState.Success -> {
          nameAgent = ""
          validationError = null
          onResetAddAgentState()
        }
        is AddAgentState.Error -> {
          validationError = addAgentState.error.message(resources)
        }
        else -> {
          validationError = null
        }
      }
    }

    AddAgentForm(
      nameAgent = nameAgent,
      validationError = validationError,
      addAgentState = addAgentState,
      onNameChange = {
        nameAgent = it
        validationError = null
      },
      onSave = { onSave(nameAgent) },
    )

    if (items.isEmpty()) {
      EmptyAgents()
    } else {
      AgentList(items)
    }
  }
}

@Composable
private fun AddAgentForm(
  nameAgent: String,
  validationError: String?,
  addAgentState: AddAgentState,
  onNameChange: (String) -> Unit,
  onSave: () -> Unit,
) {
  val agentNameLabel = stringResource(R.string.agent_field_name_label)
  val saveLabel = stringResource(R.string.agent_action_save)

  Row(
    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Column(modifier = Modifier.weight(1f)) {
      TextField(
        modifier =
          Modifier.fillMaxWidth().testTag(AgentSemantics.NAME_FIELD).semantics {
            contentDescription = agentNameLabel
            validationError?.let { error(it) }
          },
        value = nameAgent,
        onValueChange = onNameChange,
        label = { Text(agentNameLabel) },
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
                color = MaterialTheme.colorScheme.error,
              )
            }
          },
        enabled = addAgentState !is AddAgentState.Adding,
      )
    }

    Button(
      modifier =
        Modifier.width(96.dp)
          .align(Alignment.CenterVertically)
          .testTag(AgentSemantics.SAVE_BUTTON)
          .semantics {
            role = Role.Button
            contentDescription = saveLabel
          },
      onClick = onSave,
      enabled = addAgentState !is AddAgentState.Adding && nameAgent.isNotBlank(),
    ) {
      if (addAgentState is AddAgentState.Adding) {
        CircularProgressIndicator(
          modifier = Modifier.width(24.dp),
          color = MaterialTheme.colorScheme.onPrimary,
        )
      } else {
        Text(saveLabel)
      }
    }
  }
}

@Composable
private fun EmptyAgents() {
  val emptyMessage = stringResource(R.string.agent_empty_message)

  Box(
    modifier =
      Modifier.fillMaxSize().testTag(AgentSemantics.EMPTY_STATE).semantics {
        contentDescription = emptyMessage
        liveRegion = LiveRegionMode.Polite
      },
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = emptyMessage,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun AgentList(items: List<String>) {
  val listContentDescription = stringResource(R.string.agent_list_content_description)

  LazyColumn(
    modifier =
      Modifier.testTag(AgentSemantics.LIST).semantics {
        contentDescription = listContentDescription
      },
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(items) { item ->
      val itemContentDescription =
        stringResource(R.string.agent_list_item_content_description, item)

      Surface(
        modifier =
          Modifier.fillMaxWidth().semantics {
            contentDescription = itemContentDescription
          },
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        Text(
          text = item,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).semantics { heading() },
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    }
  }
}

private fun AddAgentError.message(resources: Resources): String =
  when (this) {
    AddAgentError.EmptyName -> resources.getString(R.string.agent_error_empty_name)
    is AddAgentError.NameTooLong ->
      resources.getQuantityString(R.plurals.agent_error_name_too_long, actual, actual, max)
    is AddAgentError.NameTooShort ->
      resources.getQuantityString(R.plurals.agent_error_name_too_short, actual, actual, min)
    is AddAgentError.InvalidCharacters ->
      resources.getString(
        R.string.agent_error_invalid_characters,
        invalidChars.joinToString(", ") { "'$it'" },
      )
    is AddAgentError.DuplicateName -> resources.getString(R.string.agent_error_duplicate_name, name)
    is AddAgentError.DatabaseError ->
      message ?: resources.getString(R.string.agent_error_database_fallback)
  }

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
  JasmineTheme {
    AgentContent(
      items = listOf("Compose", "Room", "Kotlin"),
      onSave = {},
      addAgentState = AddAgentState.Idle,
      onResetAddAgentState = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
  JasmineTheme {
    AgentContent(
      items = emptyList(),
      onSave = {},
      addAgentState = AddAgentState.Idle,
      onResetAddAgentState = {},
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
  JasmineTheme {
    AgentContent(
      items = listOf("Compose"),
      onSave = {},
      addAgentState = AddAgentState.Error(AddAgentError.EmptyName),
      onResetAddAgentState = {},
    )
  }
}
