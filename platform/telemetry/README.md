# Telemetry

Diagnostics and metrics facades belong here. Keep collection policy explicit and privacy-aware.

This module currently provides `TelemetrySink`, a Hilt-bound Logcat implementation for breadcrumbs,
events, metrics, and errors. Attribute fields are newline-stripped and length-limited before logging.

Policy:

- Keep telemetry local by default until a concrete analytics or crash-reporting backend is chosen.
- Sanitize user-controlled attributes at the platform boundary.
- Record OS/API context through `:platform:os` instead of collecting raw device details throughout
  feature code.
