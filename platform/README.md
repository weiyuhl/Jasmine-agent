# Platform Layer

Android OS adapters live here: permissions, notifications, file access, telemetry, foreground services, and other framework-facing facades.

Keep platform APIs behind Kotlin interfaces so feature modules and Rust bridge code do not depend directly on Android framework details.

Current modules:

- `:platform:os` exposes runtime/device information.
- `:platform:permissions` checks runtime permission state and rationale metadata.
- `:platform:files` owns app file roots, scoped file resolution, FileProvider URIs, and URI text IO.
- `:platform:notifications` owns notification channels, notification building, posting, and cancellation.
- `:platform:telemetry` exposes a privacy-aware telemetry sink backed by Logcat for now.
