# Notifications

Notification channels, foreground-service notifications, and user-visible execution updates belong here.

This module currently provides `NotificationGateway`, default Agent/diagnostic channel specs,
notification building, posting, cancellation, and Android 13+ notification permission awareness.

Compatibility notes:

- Android 8+ requires notification channels before posting notifications.
- Android 9+ requires the app to declare `FOREGROUND_SERVICE` before using foreground services.
- Android 13+ requires `POST_NOTIFICATIONS` before posting non-exempt notifications.
- Specific Android 14+ foreground-service type permissions should be declared only when a concrete
  service type is implemented.
