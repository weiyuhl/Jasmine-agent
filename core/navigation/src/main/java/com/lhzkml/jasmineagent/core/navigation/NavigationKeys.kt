package com.lhzkml.jasmineagent.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Main app-level route keys shared between app shell and feature entry registration. */
@Serializable data object Main : NavKey

@Serializable
data object BlankOne : NavKey {
  const val DEEP_LINK = "jasmineagent://blank/one"
}

@Serializable
data object BlankTwo : NavKey {
  const val DEEP_LINK = "jasmineagent://blank/two"
}
