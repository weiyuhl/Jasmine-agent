package com.lhzkml.jasmineagent.platform.telemetry

import android.util.Log
import javax.inject.Inject

enum class TelemetryLevel {
  Debug,
  Info,
  Warning,
  Error,
}

data class TelemetryEvent(
  val name: String,
  val attributes: Map<String, String> = emptyMap(),
)

data class TelemetryMetric(
  val name: String,
  val value: Double,
  val unit: String,
  val attributes: Map<String, String> = emptyMap(),
)

interface TelemetrySink {
  fun breadcrumb(message: String, attributes: Map<String, String> = emptyMap())

  fun event(event: TelemetryEvent)

  fun metric(metric: TelemetryMetric)

  fun error(
    message: String,
    throwable: Throwable? = null,
    attributes: Map<String, String> = emptyMap(),
  )
}

class LogcatTelemetrySink @Inject constructor() : TelemetrySink {
  override fun breadcrumb(message: String, attributes: Map<String, String>) {
    Log.d(TAG, format(message, attributes))
  }

  override fun event(event: TelemetryEvent) {
    Log.i(TAG, format("event:${event.name}", event.attributes))
  }

  override fun metric(metric: TelemetryMetric) {
    Log.i(
      TAG,
      format(
        "metric:${metric.name}=${metric.value} ${metric.unit}",
        metric.attributes,
      ),
    )
  }

  override fun error(message: String, throwable: Throwable?, attributes: Map<String, String>) {
    Log.e(TAG, format(message, attributes), throwable)
  }

  private fun format(message: String, attributes: Map<String, String>): String {
    val cleanMessage = message.sanitize()
    if (attributes.isEmpty()) {
      return cleanMessage
    }
    val cleanAttributes =
      attributes.entries.take(MaxAttributes).joinToString(prefix = " {", postfix = "}") {
        "${it.key.sanitize()}=${it.value.sanitize()}"
      }
    return cleanMessage + cleanAttributes
  }

  private companion object {
    const val TAG = "JasmineTelemetry"
    const val MaxAttributes = 32
  }
}

internal fun String.sanitize(): String =
  replace('\n', ' ').replace('\r', ' ').take(MaxTelemetryFieldLength)

private const val MaxTelemetryFieldLength = 256
