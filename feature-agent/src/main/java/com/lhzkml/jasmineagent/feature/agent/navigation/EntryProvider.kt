package com.lhzkml.jasmineagent.feature.agent.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main
import com.lhzkml.jasmineagent.feature.agent.ui.AgentScreen

@Composable
fun EntryProviderScope<NavKey>.AgentEntryProvider() {
  entry<Main> { AgentScreen(modifier = Modifier.padding(16.dp)) }
}
