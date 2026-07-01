@file:Suppress("UnusedPrivateMember")

package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecordStatus
import com.lhzkml.jasmineagent.core.ui.theme.AgentMaterialTheme

@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Foldable", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showBackground = true)
private annotation class AgentFormFactorPreviews

@AgentFormFactorPreviews
@Composable
private fun DefaultPreview() {
  AgentMaterialTheme {
    AgentContent(
      items = previewAgents("Compose", "Room", "Kotlin"),
      agentName = "",
      addAgentState = AddAgentState.Idle,
      actions = PreviewActions,
    )
  }
}

@AgentFormFactorPreviews
@Composable
private fun EmptyStatePreview() {
  AgentMaterialTheme {
    AgentContent(
      items = emptyList(),
      agentName = "",
      addAgentState = AddAgentState.Idle,
      actions = PreviewActions,
    )
  }
}

@Preview(name = "Phone large font", showBackground = true, fontScale = 1.5f)
@Composable
private fun ErrorStatePreview() {
  AgentMaterialTheme {
    AgentContent(
      items = previewAgents("Compose"),
      agentName = "",
      addAgentState = AddAgentState.Error(AddAgentError.EmptyName),
      actions = PreviewActions,
    )
  }
}

private val PreviewActions =
  AgentContentActions(
    onAgentNameChange = {},
    onSave = {},
    onDelete = { _, _ -> },
  )

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
