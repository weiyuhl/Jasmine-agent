# Background

Reliable background work orchestration belongs here. Feature and data modules should schedule
durable work through this platform facade instead of talking to WorkManager directly from UI code.

This module currently provides `BackgroundTaskGateway`, a Hilt-bound WorkManager facade for one-time
and periodic work, constraints, unique-work policies, observation, and cancellation.

Foreground policy:

- Use WorkManager for deferrable, reliable work.
- Use foreground work only for user-visible long-running tasks.
- Build foreground notifications through `:platform:notifications` so Android 8+ channels and
  Android 13+ notification permission checks stay centralized.
