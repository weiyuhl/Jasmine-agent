package com.lhzkml.jasmineagent.feature.agent.ui

import com.lhzkml.jasmineagent.core.domain.validation.ValidationError

internal fun ValidationError.toAddAgentError(): AddAgentError =
  when (this) {
    is ValidationError.EmptyInput -> AddAgentError.EmptyName
    is ValidationError.TooLong -> AddAgentError.NameTooLong(actual, max)
    is ValidationError.TooShort -> AddAgentError.NameTooShort(actual, min)
    is ValidationError.InvalidCharacters ->
      AddAgentError.InvalidCharacters(
        "Invalid characters: ${invalidChars.joinToString(", ") { "'$it'" }}"
      )
    is ValidationError.AlreadyExists -> AddAgentError.DuplicateName(name)
    is ValidationError.Custom -> AddAgentError.CustomError(message)
  }
