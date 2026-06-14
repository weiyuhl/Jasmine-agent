package com.lhzkml.jasmineagent.core.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AgentNameValidatorTest {

  @Test
  fun validate_withValidName_returnsValid() {
    val result = AgentNameValidator.validate("ValidAgent")
    assertTrue("Should be valid", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withValidNameWithSpaces_returnsValid() {
    val result = AgentNameValidator.validate("Valid Agent Name")
    assertTrue("Should be valid", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withValidSpecialCharacters_returnsValid() {
    val result = AgentNameValidator.validate("Agent-Name_123.0")
    assertTrue("Should be valid", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withEmptyString_returnsEmptyInput() {
    val result = AgentNameValidator.validate("")
    assertTrue("Should be invalid", result is ValidationResult.Invalid)
    assertEquals(
      "Error should be EmptyInput",
      ValidationError.EmptyInput,
      (result as ValidationResult.Invalid).error,
    )
  }

  @Test
  fun validate_withBlankString_returnsEmptyInput() {
    val result = AgentNameValidator.validate("   ")
    assertTrue("Should be invalid", result is ValidationResult.Invalid)
    assertEquals(
      "Error should be EmptyInput",
      ValidationError.EmptyInput,
      (result as ValidationResult.Invalid).error,
    )
  }

  @Test
  fun validate_withTooShortName_returnsTooShort() {
    val result = AgentNameValidator.validate("A")
    assertTrue("Should be invalid", result is ValidationResult.Invalid)
    val error = (result as ValidationResult.Invalid).error
    assertTrue("Error should be TooShort", error is ValidationError.TooShort)
    assertEquals("Actual length should be 1", 1, (error as ValidationError.TooShort).actual)
    assertEquals("Min length should be 2", 2, error.min)
  }

  @Test
  fun validate_withTooLongName_returnsTooLong() {
    val longName = "A".repeat(101)
    val result = AgentNameValidator.validate(longName)
    assertTrue("Should be invalid", result is ValidationResult.Invalid)
    val error = (result as ValidationResult.Invalid).error
    assertTrue("Error should be TooLong", error is ValidationError.TooLong)
    assertEquals("Actual length should be 101", 101, (error as ValidationError.TooLong).actual)
    assertEquals("Max length should be 100", 100, error.max)
  }

  @Test
  fun validate_withInvalidCharacters_returnsInvalidCharacters() {
    val result = AgentNameValidator.validate("Agent@#$%")
    assertTrue("Should be invalid", result is ValidationResult.Invalid)
    val error = (result as ValidationResult.Invalid).error
    assertTrue("Error should be InvalidCharacters", error is ValidationError.InvalidCharacters)
    val invalidChars = (error as ValidationError.InvalidCharacters).invalidChars
    assertTrue("Should contain @", invalidChars.contains('@'))
    assertTrue("Should contain #", invalidChars.contains('#'))
    assertTrue("Should contain \$", invalidChars.contains('$'))
    assertTrue("Should contain %", invalidChars.contains('%'))
  }

  @Test
  fun validate_withLeadingAndTrailingSpaces_trimAndValidate() {
    val result = AgentNameValidator.validate("  ValidAgent  ")
    assertTrue("Should be valid after trimming", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withMinimumLength_returnsValid() {
    val result = AgentNameValidator.validate("AB")
    assertTrue("Should be valid with 2 characters", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withMaximumLength_returnsValid() {
    val maxName = "A".repeat(100)
    val result = AgentNameValidator.validate(maxName)
    assertTrue("Should be valid with 100 characters", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withUnicode_returnsValid() {
    val result = AgentNameValidator.validate("Agent中文名称")
    assertTrue("Should be valid with unicode", result is ValidationResult.Valid)
  }

  @Test
  fun validate_withNumbers_returnsValid() {
    val result = AgentNameValidator.validate("Agent123")
    assertTrue("Should be valid with numbers", result is ValidationResult.Valid)
  }
}
