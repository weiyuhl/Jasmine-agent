package com.lhzkml.jasmineagent.feature.agent.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecord
import com.lhzkml.jasmineagent.core.domain.repository.AgentRecordStatus
import com.lhzkml.jasmineagent.core.ui.JasmineTheme

@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Foldable", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showBackground = true)
private annotation class AgentScreenshotFormFactors

@PreviewTest
@AgentScreenshotFormFactors
@Composable
private fun AgentContentDefaultScreenshot() {
  JasmineTheme {
    AgentContent(
      items = screenshotAgents("Compose", "Room", "Kotlin"),
      agentName = "",
      onAgentNameChange = {},
      onSave = {},
      onDelete = { _, _ -> },
      addAgentState = AddAgentState.Idle,
    )
  }
}

@PreviewTest
@AgentScreenshotFormFactors
@Composable
private fun AgentContentEmptyScreenshot() {
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

@PreviewTest
@Preview(name = "Phone large font", showBackground = true, fontScale = 1.5f)
@Composable
private fun AgentContentErrorLargeFontScreenshot() {
  JasmineTheme {
    AgentContent(
      items = screenshotAgents("Compose"),
      agentName = "",
      onAgentNameChange = {},
      onSave = {},
      onDelete = { _, _ -> },
      addAgentState = AddAgentState.Error(AddAgentError.EmptyName),
    )
  }
}

private fun screenshotAgents(vararg names: String): List<AgentRecord> =
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
