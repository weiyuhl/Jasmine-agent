package com.lhzkml.jasmineagent.core.domain.validation

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

/** Port used by domain use cases to normalize and validate agent names. */
interface AgentNamePolicy {
  fun normalize(name: String): String

  fun validate(name: String): ValidationResult
}

/** Pure Kotlin fallback policy for domain tests and non-Rust callers. */
object AgentNameValidator : AgentNamePolicy {
  private const val MIN_AGENT_NAME_LENGTH = 2
  private const val MAX_AGENT_NAME_LENGTH = 100

  /** Returns the persisted canonical form for a name. */
  override fun normalize(name: String): String = name.trim()

  /** Validates a name without accessing storage or UI resources. */
  override fun validate(name: String): ValidationResult {
    val normalizedName = normalize(name)
    val actualLength = normalizedName.length
    val invalidChars =
      normalizedName
        .asSequence()
        .filterNot {
          it.isLetterOrDigit() || it.isWhitespace() || it == '-' || it == '_' || it == '.'
        }
        .toSet()

    return when {
      normalizedName.isEmpty() -> ValidationResult.Invalid(ValidationError.EmptyInput)
      actualLength < MIN_AGENT_NAME_LENGTH ->
        ValidationResult.Invalid(ValidationError.TooShort(actualLength, MIN_AGENT_NAME_LENGTH))
      actualLength > MAX_AGENT_NAME_LENGTH ->
        ValidationResult.Invalid(ValidationError.TooLong(actualLength, MAX_AGENT_NAME_LENGTH))
      invalidChars.isNotEmpty() ->
        ValidationResult.Invalid(ValidationError.InvalidCharacters(invalidChars))
      else -> ValidationResult.Valid
    }
  }
}
