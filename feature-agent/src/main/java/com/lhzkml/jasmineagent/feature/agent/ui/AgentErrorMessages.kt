package com.lhzkml.jasmineagent.feature.agent.ui

import android.content.res.Resources
import com.lhzkml.jasmineagent.feature.agent.R

internal fun AddAgentError.message(resources: Resources): String =
  when (this) {
    AddAgentError.EmptyName -> resources.getString(R.string.agent_error_empty_name)
    is AddAgentError.NameTooLong ->
      resources.getQuantityString(R.plurals.agent_error_name_too_long, actual, actual, max)
    is AddAgentError.NameTooShort ->
      resources.getQuantityString(R.plurals.agent_error_name_too_short, actual, actual, min)
    is AddAgentError.InvalidCharacters ->
      resources.getString(
        R.string.agent_error_invalid_characters,
        invalidChars.joinToString(", ") { "'$it'" },
      )
    is AddAgentError.DuplicateName -> resources.getString(R.string.agent_error_duplicate_name, name)
    AddAgentError.Storage -> resources.getString(R.string.agent_error_database_fallback)
  }

internal fun AgentLoadError.message(resources: Resources): String =
  when (this) {
    AgentLoadError.Storage -> resources.getString(R.string.agent_error_database_fallback)
  }

internal fun DeleteAgentError.message(resources: Resources): String =
  when (this) {
    DeleteAgentError.Storage -> resources.getString(R.string.agent_error_database_fallback)
  }
