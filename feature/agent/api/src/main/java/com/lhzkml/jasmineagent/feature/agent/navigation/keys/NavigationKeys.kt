package com.lhzkml.jasmineagent.feature.agent.navigation.keys

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Top-level destinations for the bottom navigation / navigation rail.
 *
 * Each key is serializable so that Navigation 3 can persist and restore the back stack across
 * process death.
 */
@Serializable
data object Main : NavKey {
  /** Deep link pattern for the agents list screen. */
  const val DEEP_LINK = "jasmineagent://agents"
}

@Serializable
data object BlankOne : NavKey {
  const val DEEP_LINK = "jasmineagent://blank-one"
}

@Serializable
data object BlankTwo : NavKey {
  const val DEEP_LINK = "jasmineagent://blank-two"
}
