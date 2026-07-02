# OS Adapters

Android framework wrappers that expose lifecycle-safe, testable OS capabilities to domain and sandbox layers.

This module currently provides `AndroidRuntimeInfo`, a Hilt-bound facade for reading SDK level,
preview state, security patch, SDK Extension versions, device model, manufacturer, and supported
ABI information without leaking `Build` calls through feature or sandbox code.

Version policy:

- `AndroidApiLevel.ANDROID_8` matches the current Gradle `minSdk` installation floor.
- `AndroidApiLevel.ANDROID_9` is the compatibility milestone for legacy behavior checks.
- `AndroidApiLevel.ANDROID_17` is the current target/compile platform used by the app.
- Feature and sandbox modules should depend on this facade instead of scattering `Build.VERSION`
  checks through business logic.
