# Permissions

Permission request orchestration and capability grants belong here. UI modules should send intents and observe state instead of calling low-level permission APIs directly.

This module currently provides `PermissionGateway`, a Hilt-bound facade for checking runtime
permission state, platform-version applicability, missing permissions, and rationale metadata.
