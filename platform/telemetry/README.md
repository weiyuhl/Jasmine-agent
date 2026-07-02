# Telemetry

Diagnostics and metrics facades belong here. Keep collection policy explicit and privacy-aware.

This module currently provides `TelemetrySink`, a Hilt-bound Logcat implementation for breadcrumbs,
events, metrics, and errors. Attribute fields are newline-stripped and length-limited before logging.
