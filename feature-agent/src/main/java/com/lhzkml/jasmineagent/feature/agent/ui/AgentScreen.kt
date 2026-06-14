package com.lhzkml.jasmineagent.feature.agent.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhzkml.jasmineagent.core.ui.JasmineTheme
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Error
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Loading
import com.lhzkml.jasmineagent.feature.agent.ui.AgentUiState.Success

@Composable
fun AgentScreen(modifier: Modifier = Modifier, viewModel: AgentViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val addAgentState by viewModel.addAgentState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is AgentEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
        is AgentEvent.AgentAdded ->
          snackbarHostState.showSnackbar("Agent '${event.name}' added successfully")
      }
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
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
      modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
    )
  }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
  Box(modifier = modifier.safeDrawingPadding(), contentAlignment = Alignment.Center) {
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
  Box(modifier = modifier.safeDrawingPadding(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = message ?: "Unknown error occurred",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyLarge,
      )
      if (canRetry) {
        Button(onClick = onRetry) { Text("Retry") }
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
  Column(modifier.safeDrawingPadding().padding(16.dp)) {
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
          validationError = addAgentState.error.message
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
  Row(
    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Column(modifier = Modifier.weight(1f)) {
      TextField(
        modifier = Modifier.fillMaxWidth(),
        value = nameAgent,
        onValueChange = onNameChange,
        label = { Text("Agent name") },
        isError = validationError != null,
        supportingText =
          validationError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        enabled = addAgentState !is AddAgentState.Adding,
      )
    }

    Button(
      modifier = Modifier.width(96.dp).align(Alignment.CenterVertically),
      onClick = onSave,
      enabled = addAgentState !is AddAgentState.Adding && nameAgent.isNotBlank(),
    ) {
      if (addAgentState is AddAgentState.Adding) {
        CircularProgressIndicator(
          modifier = Modifier.width(24.dp),
          color = MaterialTheme.colorScheme.onPrimary,
        )
      } else {
        Text("Save")
      }
    }
  }
}

@Composable
private fun EmptyAgents() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(
      text = "No agents yet. Add one above!",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun AgentList(items: List<String>) {
  LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(items) { item ->
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        Text(
          text = item,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    }
  }
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
