package com.lhzkml.jasmineagent.core.rust

import uniffi.jasmine_core.AgentNameValidation
import uniffi.jasmine_core.AgentNameValidationKind
import uniffi.jasmine_core.normalizeAgentName
import uniffi.jasmine_core.validateAgentName

/** Kotlin facade over the UniFFI-generated Rust agent-name core. */
object AgentNameCore {

  /** Returns the canonical form used before validation and persistence. */
  fun normalize(name: String): String = normalizeAgentName(name)

  /** Validates an agent name by delegating the rule evaluation to Rust. */
  fun validate(name: String): CoreAgentNameValidation = validateAgentName(name).toCoreValidation()
}

/** Stable Kotlin validation model exposed to domain code instead of generated UniFFI types. */
sealed interface CoreAgentNameValidation {
  data object Valid : CoreAgentNameValidation

  data object EmptyInput : CoreAgentNameValidation

  data class TooShort(val actual: Int, val min: Int) : CoreAgentNameValidation

  data class TooLong(val actual: Int, val max: Int) : CoreAgentNameValidation

  data class InvalidCharacters(val invalidChars: Set<Char>) : CoreAgentNameValidation
}

private fun AgentNameValidation.toCoreValidation(): CoreAgentNameValidation =
  when (kind) {
    AgentNameValidationKind.VALID -> CoreAgentNameValidation.Valid
    AgentNameValidationKind.EMPTY_INPUT -> CoreAgentNameValidation.EmptyInput
    AgentNameValidationKind.TOO_SHORT -> CoreAgentNameValidation.TooShort(actual, limit)
    AgentNameValidationKind.TOO_LONG -> CoreAgentNameValidation.TooLong(actual, limit)
    AgentNameValidationKind.INVALID_CHARACTERS ->
      CoreAgentNameValidation.InvalidCharacters(invalidCharacters.toSet())
  }
