package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecordStatus
import com.lhzkml.jasmineagent.core.ui.JasmineTheme

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
  JasmineTheme {
    AgentContent(
      items = previewAgents("Compose", "Room", "Kotlin"),
      agentName = "",
      onAgentNameChange = {},
      onSave = {},
      onDelete = { _, _ -> },
      addAgentState = AddAgentState.Idle,
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
  JasmineTheme {
    AgentContent(
      items = emptyList(),
      agentName = "",
      onAgentNameChange = {},
      onSave = {},
      onDelete = { _, _ -> },
      addAgentState = AddAgentState.Idle,
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
  JasmineTheme {
    AgentContent(
      items = previewAgents("Compose"),
      agentName = "",
      onAgentNameChange = {},
      onSave = {},
      onDelete = { _, _ -> },
      addAgentState = AddAgentState.Error(AddAgentError.EmptyName),
    )
  }
}

private fun previewAgents(vararg names: String): List<AgentRecord> =
  names.mapIndexed { index, name ->
    AgentRecord(
      uid = index + 1,
      name = name,
      createdAt = 0L,
      updatedAt = 0L,
      status = AgentRecordStatus.ACTIVE,
      description = null,
    )
  }
