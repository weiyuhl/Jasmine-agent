# Platform Layer

Android OS adapters live here: permissions, notifications, file access, telemetry, foreground services, and other framework-facing facades.

Keep platform APIs behind Kotlin interfaces so feature modules and Rust bridge code do not depend directly on Android framework details.

Compatibility target:

- The project compiles and targets Android 17 / API 37.
- The current Gradle `minSdk` remains Android 8 / API 26, which includes Android 9 devices.
- Platform code keeps Android 9 / API 28 behavior explicit for permission, file, notification, and
  foreground-service boundaries.
- Newer dangerous permissions are exposed as requestable capabilities, but broad permissions should
  only be declared by features that actually need them.

Current modules:

- `:platform:background` owns WorkManager scheduling, foreground-work info, cancellation, and observation.
- `:platform:os` exposes runtime/device information.
- `:platform:permissions` checks runtime permission state and rationale metadata.
- `:platform:files` owns app file roots, scoped file resolution, FileProvider URIs, and URI text IO.
- `:platform:notifications` owns notification channels, notification building, posting, and cancellation.
- `:platform:telemetry` exposes a privacy-aware telemetry sink backed by Logcat for now.
