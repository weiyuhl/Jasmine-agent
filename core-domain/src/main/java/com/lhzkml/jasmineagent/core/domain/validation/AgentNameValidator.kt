package com.lhzkml.jasmineagent.core.domain.validation

sealed interface ValidationResult {
  data object Valid : ValidationResult

  data class Invalid(val error: ValidationError) : ValidationResult
}

sealed interface ValidationError {
  data object EmptyInput : ValidationError

  data class TooShort(val actual: Int, val min: Int) : ValidationError

  data class TooLong(val actual: Int, val max: Int) : ValidationError

  data class InvalidCharacters(val invalidChars: Set<Char>) : ValidationError

  data class AlreadyExists(val name: String) : ValidationError
}

object AgentNameValidator {
  private const val MIN_LENGTH = 2
  private const val MAX_LENGTH = 100
  private val ALLOWED_SPECIAL_CHARS = setOf('-', '_', '.')

  fun validate(name: String): ValidationResult {
    val trimmed = name.trim()

    val invalidChars =
      trimmed
        .filter { !it.isLetterOrDigit() && !it.isWhitespace() && it !in ALLOWED_SPECIAL_CHARS }
        .toSet()

    return when {
      trimmed.isEmpty() -> ValidationResult.Invalid(ValidationError.EmptyInput)
      trimmed.length < MIN_LENGTH ->
        ValidationResult.Invalid(ValidationError.TooShort(trimmed.length, MIN_LENGTH))
      trimmed.length > MAX_LENGTH ->
        ValidationResult.Invalid(ValidationError.TooLong(trimmed.length, MAX_LENGTH))
      invalidChars.isNotEmpty() ->
        ValidationResult.Invalid(ValidationError.InvalidCharacters(invalidChars))
      else -> ValidationResult.Valid
    }
  }
}
