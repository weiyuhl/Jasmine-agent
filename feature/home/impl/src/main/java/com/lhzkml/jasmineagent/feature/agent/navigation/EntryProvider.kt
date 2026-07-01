package com.lhzkml.jasmineagent.feature.agent.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankOne
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankTwo
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main
import com.lhzkml.jasmineagent.feature.agent.ui.AgentScreen

object BlankDestinationSemantics {
  const val BLANK_ONE = "blank_one_screen"
  const val BLANK_TWO = "blank_two_screen"
}

@Composable
fun EntryProviderScope<NavKey>.AgentEntryProvider() {
  entry<Main> { AgentScreen() }
  entry<BlankOne> { BlankScreen(BlankDestinationSemantics.BLANK_ONE) }
  entry<BlankTwo> { BlankScreen(BlankDestinationSemantics.BLANK_TWO) }
}

@Composable
private fun BlankScreen(testTag: String) {
  Box(modifier = Modifier.fillMaxSize().testTag(testTag))
}
