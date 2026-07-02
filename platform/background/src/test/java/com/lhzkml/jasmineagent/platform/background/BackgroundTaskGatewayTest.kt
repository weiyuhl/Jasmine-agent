package com.lhzkml.jasmineagent.platform.background

import androidx.work.CoroutineWorker
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

  @Test
  fun expeditedSpecRejectsInitialDelay() {
    val spec =
      OneTimeBackgroundWorkSpec(
        workerClass = CoroutineWorker::class,
        expedited = true,
        initialDelay = Duration.ofSeconds(5),
      )

    assertThrows(IllegalArgumentException::class.java) { requireValidExpeditedSpec(spec) }
  }

  @Test
  fun expeditedSpecRejectsUnsupportedConstraints() {
    val spec =
      OneTimeBackgroundWorkSpec(
        workerClass = CoroutineWorker::class,
        expedited = true,
        constraints = BackgroundWorkConstraints(requiresCharging = true),
      )

    assertThrows(IllegalArgumentException::class.java) { requireValidExpeditedSpec(spec) }
  }

  @Test
  fun expeditedSpecAcceptsNetworkAndStorageConstraints() {
    val spec =
      OneTimeBackgroundWorkSpec(
        workerClass = CoroutineWorker::class,
        expedited = true,
        constraints =
          BackgroundWorkConstraints(
            networkType = BackgroundNetworkType.Connected,
            requiresStorageNotLow = true,
          ),
      )

    requireValidExpeditedSpec(spec)
  }

  @Test
  fun nonExpeditedSpecAllowsInitialDelayAndConstraints() {
    val spec =
      OneTimeBackgroundWorkSpec(
        workerClass = CoroutineWorker::class,
        initialDelay = Duration.ofMinutes(1),
        constraints = BackgroundWorkConstraints(requiresCharging = true),
      )

    requireValidExpeditedSpec(spec)
  }
}
