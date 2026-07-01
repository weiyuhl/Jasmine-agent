package com.lhzkml.jasmineagent.core.data

import com.lhzkml.jasmineagent.core.domain.validation.AgentNamePolicy
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError
import com.lhzkml.jasmineagent.core.domain.validation.ValidationResult
import com.lhzkml.jasmineagent.core.rust.AgentNameCore
import com.lhzkml.jasmineagent.core.rust.CoreAgentNameValidation
import javax.inject.Inject

/** Agent-name policy backed by the Rust core through the Kotlin-friendly native bridge. */
class RustAgentNamePolicy @Inject constructor() : AgentNamePolicy {
  override fun normalize(name: String): String = AgentNameCore.normalize(name)

  override fun validate(name: String): ValidationResult =
    when (val validation = AgentNameCore.validate(name)) {
      CoreAgentNameValidation.Valid -> ValidationResult.Valid
      CoreAgentNameValidation.EmptyInput -> ValidationResult.Invalid(ValidationError.EmptyInput)
      is CoreAgentNameValidation.TooShort ->
        ValidationResult.Invalid(ValidationError.TooShort(validation.actual, validation.min))
      is CoreAgentNameValidation.TooLong ->
        ValidationResult.Invalid(ValidationError.TooLong(validation.actual, validation.max))
      is CoreAgentNameValidation.InvalidCharacters ->
        ValidationResult.Invalid(ValidationError.InvalidCharacters(validation.invalidChars))
    }
}
