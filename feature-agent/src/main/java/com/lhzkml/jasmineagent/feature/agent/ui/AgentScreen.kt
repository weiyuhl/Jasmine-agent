package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexJustifyContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.ui.JasmineStyles
import com.lhzkml.jasmineagent.feature.agent.R
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Error
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Loading
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Success

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
fun AgentScreen(modifier: Modifier = Modifier, viewModel: AgentViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val addAgentState by viewModel.addAgentState.collectAsStateWithLifecycle()
  val agentName by viewModel.agentName.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val resources = LocalResources.current
  val screenContentDescription = stringResource(R.string.agent_screen_content_description)
  val snackbarContentDescription = stringResource(R.string.agent_snackbar_content_description)

  AgentEventHandler(viewModel, snackbarHostState)

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
          s.error.message(resources),
          s.canRetry,
          viewModel::retryLoadAgents,
          Modifier.fillMaxSize(),
        )
      is Success ->
        AgentContent(
          items = s.data,
          agentName = agentName,
          onAgentNameChange = viewModel::onAgentNameChanged,
          onSave = viewModel::addAgent,
          onDelete = viewModel::deleteAgent,
          addAgentState = addAgentState,
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
      snackbar = { snackbarData ->
        Snackbar(
          snackbarData = snackbarData,
          containerColor = MaterialTheme.colorScheme.inverseSurface,
          contentColor = MaterialTheme.colorScheme.inverseOnSurface,
          actionColor = MaterialTheme.colorScheme.inverseOnSurface,
          dismissActionContentColor = MaterialTheme.colorScheme.inverseOnSurface,
        )
      },
    )
  }
}

private val AgentGridMinCellWidth = 280.dp

@Composable
private fun AgentEventHandler(
  viewModel: AgentViewModel,
  snackbarHostState: SnackbarHostState,
) {
  val resources = LocalResources.current

  LaunchedEffect(resources, viewModel) {
    viewModel.events.collect { event ->
      val message =
        when (event) {
          is AgentEvent.ShowError -> event.error.message(resources)
          is AgentEvent.ShowDeleteError -> event.error.message(resources)
          is AgentEvent.AgentAdded -> resources.getString(R.string.agent_added_message, event.name)
          is AgentEvent.AgentDeleted ->
            resources.getString(R.string.agent_deleted_message, event.name)
        }
      snackbarHostState.showSnackbar(message)
    }
  }
}

@OptIn(ExperimentalFlexBoxApi::class)
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
    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
  }
}

@OptIn(ExperimentalFlexBoxApi::class)
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
    FlexBox(
      config = {
        direction(FlexDirection.Column)
        alignItems(FlexAlignItems.Center)
        justifyContent(FlexJustifyContent.Center)
        gap(16.dp)
      }
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
          shape = RoundedCornerShape(8.dp),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
          Text(
            retryLabel,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
internal fun AgentContent(
  items: List<AgentRecord>,
  agentName: String,
  onAgentNameChange: (String) -> Unit,
  onSave: () -> Unit,
  onDelete: (uid: Int, name: String) -> Unit,
  addAgentState: AddAgentState,
  modifier: Modifier = Modifier,
) {
  val formContentDescription = stringResource(R.string.agent_form_content_description)
  val formStyleState = remember { MutableStyleState(null) }

  Column(
    modifier
      .safeDrawingPadding()
      .styleable(formStyleState, JasmineStyles.agentFormContainer)
      .testTag(AgentSemantics.FORM)
      .semantics {
        contentDescription = formContentDescription
      }
  ) {
    AddAgentForm(
      nameAgent = agentName,
      addAgentState = addAgentState,
      onNameChange = onAgentNameChange,
      onSave = onSave,
    )

    if (items.isEmpty()) {
      EmptyAgents()
    } else {
      AgentList(items, onDelete)
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

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
private fun AgentList(items: List<AgentRecord>, onDelete: (uid: Int, name: String) -> Unit) {
  val listContentDescription = stringResource(R.string.agent_list_content_description)
  val deleteLabel = stringResource(R.string.agent_action_delete)

  LazyVerticalGrid(
    columns = GridCells.Adaptive(AgentGridMinCellWidth),
    modifier =
      Modifier.testTag(AgentSemantics.LIST).semantics {
        contentDescription = listContentDescription
      },
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(items, key = { it.uid }) { item ->
      AgentListItem(item, deleteLabel, onDelete)
    }
  }
}

@OptIn(ExperimentalFlexBoxApi::class)
@Composable
private fun AgentListItem(
  item: AgentRecord,
  deleteLabel: String,
  onDelete: (uid: Int, name: String) -> Unit,
) {
  val itemContentDescription =
    stringResource(R.string.agent_list_item_content_description, item.name)
  val deleteContentDescription =
    stringResource(R.string.agent_action_delete_content_description, item.name)

  Surface(
    modifier =
      Modifier.fillMaxWidth().semantics {
        contentDescription = itemContentDescription
      },
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    FlexBox(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      config = {
        direction(FlexDirection.Row)
        alignItems(FlexAlignItems.Center)
        justifyContent(FlexJustifyContent.SpaceBetween)
        gap(8.dp)
      },
    ) {
      Text(
        text = item.name,
        modifier =
          Modifier.widthIn(min = 0.dp)
            .flex {
              grow(1f)
              shrink(1f)
            }
            .semantics {
              heading()
              contentDescription = itemContentDescription
            },
        style = MaterialTheme.typography.bodyLarge,
      )
      TextButton(
        modifier =
          Modifier.testTag(AgentSemantics.deleteButton(item.uid)).semantics {
            role = Role.Button
            contentDescription = deleteContentDescription
          },
        onClick = { onDelete(item.uid, item.name) },
        shape = RoundedCornerShape(8.dp),
        colors =
          ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
      ) {
        Text(
          deleteLabel,
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}
