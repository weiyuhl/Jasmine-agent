package com.lhzkml.jasmineagent.platform.background

import java.time.Duration
import org.junit.Assert.assertThrows
import org.junit.Test

class BackgroundTaskGatewayTest {
  @Test
  fun periodicIntervalRejectsZeroDuration() {
    assertThrows(IllegalArgumentException::class.java) {
      requireValidPeriodicInterval(Duration.ZERO)
    }
  }

  @Test
  fun periodicIntervalRejectsShortDuration() {
    assertThrows(IllegalArgumentException::class.java) {
      requireValidPeriodicInterval(Duration.ofMinutes(1))
    }
  }

  @Test
  fun periodicIntervalAcceptsWorkManagerMinimumDuration() {
    requireValidPeriodicInterval(Duration.ofMinutes(15))
  }
}
