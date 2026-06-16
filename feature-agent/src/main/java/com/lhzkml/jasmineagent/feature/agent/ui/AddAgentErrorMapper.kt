package com.lhzkml.jasmineagent.feature.agent.ui

import com.lhzkml.jasmineagent.core.domain.usecase.AddAgentRepositoryError
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError

internal fun AddAgentRepositoryError.toAddAgentError(): AddAgentError =
  when (this) {
    AddAgentRepositoryError.STORAGE -> AddAgentError.Storage
  }

internal fun ValidationError.toAddAgentError(): AddAgentError =
  when (this) {
    is ValidationError.EmptyInput -> AddAgentError.EmptyName
    is ValidationError.TooLong -> AddAgentError.NameTooLong(actual, max)
    is ValidationError.TooShort -> AddAgentError.NameTooShort(actual, min)
    is ValidationError.InvalidCharacters -> AddAgentError.InvalidCharacters(invalidChars)
    is ValidationError.AlreadyExists -> AddAgentError.DuplicateName(name)
  }
