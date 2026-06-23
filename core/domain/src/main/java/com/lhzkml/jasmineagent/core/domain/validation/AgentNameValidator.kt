package com.lhzkml.jasmineagent.core.domain.validation

import com.lhzkml.jasmineagent.core.rust.AgentNameCore
import com.lhzkml.jasmineagent.core.rust.CoreAgentNameValidation

/** Result of validating a user-provided agent name. */
sealed interface ValidationResult {
  data object Valid : ValidationResult

  data class Invalid(val error: ValidationError) : ValidationResult
}

/** Domain-level validation errors independent from UI copy and resources. */
sealed interface ValidationError {
  data object EmptyInput : ValidationError

  data class TooShort(val actual: Int, val min: Int) : ValidationError

  data class TooLong(val actual: Int, val max: Int) : ValidationError

  data class InvalidCharacters(val invalidChars: Set<Char>) : ValidationError

  data class AlreadyExists(val name: String) : ValidationError
}

/** Domain validator that normalizes and validates names through the Rust core facade. */
object AgentNameValidator {
  /** Returns the persisted canonical form for a name. */
  fun normalize(name: String): String = AgentNameCore.normalize(name)

  /** Validates a name without accessing storage or UI resources. */
  fun validate(name: String): ValidationResult =
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
