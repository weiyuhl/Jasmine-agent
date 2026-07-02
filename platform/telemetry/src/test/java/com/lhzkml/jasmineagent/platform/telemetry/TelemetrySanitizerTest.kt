package com.lhzkml.jasmineagent.platform.telemetry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TelemetrySanitizerTest {
  @Test
  fun sanitize_removesLineBreaks() {
    val sanitized = "first\nsecond\rthird".sanitize()

    assertEquals("first second third", sanitized)
  }

  @Test
  fun sanitize_truncatesLongFields() {
    val sanitized = "a".repeat(300).sanitize()

    assertEquals(256, sanitized.length)
    assertFalse(sanitized.contains('\n'))
  }
}
