# Changelog

All notable changes to this project are recorded here.

## Unreleased

- Added project onboarding documentation, contribution guidance, changelog, and license.
- Removed unused Paging 3 dependencies and stale Paging DAO entry points.
- Expanded `AgentRepository` to match DAO capabilities.
- Strengthened SQLCipher passphrase derivation with a persisted random secret.
- Removed old Room migration compatibility code and v1 schema history.
- Added explicit baseline profile rules for startup and agent feature hot paths.
- Replaced sample Material colors with a Jasmine brand palette and disabled dynamic color by default.
- Added Dokka-based API documentation generation and verification tasks.

## 2026-06-14

- Upgraded Android Gradle Plugin, Kotlin, Compose, Hilt, KSP, Room, Navigation 3, and related AndroidX dependencies.
- Built and verified the release APK with warnings enabled.
- Added repository and database security tests for the updated data layer.
- Cleaned local assistant configuration from version control and ignore rules.
