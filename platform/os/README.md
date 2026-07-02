# OS Adapters

Android framework wrappers that expose lifecycle-safe, testable OS capabilities to domain and sandbox layers.

This module currently provides `AndroidRuntimeInfo`, a Hilt-bound facade for reading SDK level,
release, device model, manufacturer, and supported ABI information without leaking `Build` calls
through feature or sandbox code.
