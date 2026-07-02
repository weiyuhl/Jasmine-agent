# Permissions

Permission request orchestration and capability grants belong here. UI modules should send intents and observe state instead of calling low-level permission APIs directly.

This module currently provides `PermissionGateway`, a Hilt-bound facade for checking runtime
permission state, platform-version applicability, missing permissions, and rationale metadata.

Compatibility notes:

- Android 9 through Android 12L use legacy shared-storage permissions only when a feature really
  needs direct media access.
- Android 13+ uses granular media permissions and notification runtime permission checks.
- Android 14+ can request partial photo/video access with `ReadMediaVisualUserSelected`.
- Android 17+ exposes local network access as a separate runtime permission.

Do not declare broad dangerous permissions just because this module knows about them. Feature
modules should declare and request only the capabilities they actually use.
