package com.lhzkml.jasmineagent.feature.agent.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.jasmineagent.feature.agent.R
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankOne
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.BlankTwo
import com.lhzkml.jasmineagent.feature.agent.navigation.keys.Main
import com.lhzkml.jasmineagent.feature.agent.ui.AgentScreen

object BlankDestinationSemantics {
  const val BLANK_ONE = "blank_one_screen"
  const val BLANK_TWO = "blank_two_screen"
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntryProviderScope<NavKey>.AgentEntryProvider() {
  entry<Main>(
    metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { EmptyDetailPlaceholder() })
  ) {
    AgentScreen()
  }
  entry<BlankOne> { BlankScreen(BlankDestinationSemantics.BLANK_ONE) }
  entry<BlankTwo> { BlankScreen(BlankDestinationSemantics.BLANK_TWO) }
}

@Composable
private fun BlankScreen(testTag: String) {
  Box(modifier = Modifier.fillMaxSize().testTag(testTag))
}

@Composable
private fun EmptyDetailPlaceholder() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(
      text = stringResource(R.string.agent_empty_message),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}
