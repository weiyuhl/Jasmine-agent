package com.lhzkml.jasmineagent.feature.agent.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main
import com.lhzkml.jasmineagent.feature.agent.ui.AgentScreen

@Composable
fun EntryProviderScope<NavKey>.AgentEntryProvider() {
  entry<Main> { AgentScreen() }
}
