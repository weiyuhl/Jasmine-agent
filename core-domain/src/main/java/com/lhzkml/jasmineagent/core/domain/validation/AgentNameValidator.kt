/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhzkml.jasmineagent.core.domain.validation

sealed interface ValidationResult {
  data object Valid : ValidationResult

  data class Invalid(val error: ValidationError) : ValidationResult
}

sealed interface ValidationError {
  val message: String

  data object EmptyInput : ValidationError {
    override val message: String = "Input cannot be empty"
  }

  data class TooShort(val actual: Int, val min: Int) : ValidationError {
    override val message: String = "Input too short ($actual characters, minimum $min)"
  }

  data class TooLong(val actual: Int, val max: Int) : ValidationError {
    override val message: String = "Input too long ($actual characters, maximum $max)"
  }

  data class InvalidCharacters(val invalidChars: Set<Char>) : ValidationError {
    override val message: String =
      "Invalid characters found: ${invalidChars.joinToString(", ") { "'$it'" }}"
  }

  data class AlreadyExists(val name: String) : ValidationError {
    override val message: String = "Item with name '$name' already exists"
  }

  data class Custom(override val message: String) : ValidationError
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
