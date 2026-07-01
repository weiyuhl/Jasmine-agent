package com.lhzkml.jasmineagent.feature.agent.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.jasmineagent.core.navigation.BlankOne
import com.lhzkml.jasmineagent.core.navigation.BlankTwo
import com.lhzkml.jasmineagent.core.navigation.Main
import com.lhzkml.jasmineagent.core.navigation.NavigationEntryRegistrar
import com.lhzkml.jasmineagent.feature.agent.ui.AgentScreen
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

object BlankDestinationSemantics {
  const val BLANK_ONE = "blank_one_screen"
  const val BLANK_TWO = "blank_two_screen"
}

class HomeNavigationEntryRegistrar @Inject constructor() : NavigationEntryRegistrar {
  @Composable
  override fun EntryProviderScope<NavKey>.registerEntries() {
    AgentEntryProvider()
  }
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

@Module
@InstallIn(SingletonComponent::class)
interface HomeNavigationModule {
  @Binds
  @IntoSet
  fun bindHomeNavigationEntryRegistrar(
    registrar: HomeNavigationEntryRegistrar
  ): NavigationEntryRegistrar
}
