# Platform Layer

Android OS adapters live here as the app grows: permissions, notifications, file access, telemetry, foreground services, and other framework-facing facades.

Keep platform APIs behind Kotlin interfaces so feature modules and Rust bridge code do not depend directly on Android framework details.
