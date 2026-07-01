package com.lhzkml.jasmineagent.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Registers a feature's Navigation 3 destinations without letting app source depend on impl code.
 */
interface NavigationEntryRegistrar {
  @Composable fun EntryProviderScope<NavKey>.registerEntries()
}
