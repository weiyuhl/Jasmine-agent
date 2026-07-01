# JasmineAgent

JasmineAgent is a Kotlin-first Android sample that demonstrates a modular architecture with Jetpack Compose, Hilt, Room, SQLCipher, Navigation 3, and focused quality tooling.

## Project Structure

- `app`: Android application entry point, release signing, startup wiring, navigation host, and packaging rules.
- `feature-agent`: Agent feature UI, ViewModel, navigation entry binding, and feature tests.
- `feature-agent-navigation`: Shared Navigation 3 route keys and navigation contracts.
- `core-domain`: Use cases and validation rules.
- `core-data`: Repository contracts and repository implementation.
- `core-database`: Room entities, DAO, SQLCipher database setup, and database security tests.
- `core-ui`: Compose theme, color, typography, and shared UI styling.
- `core-testing`: Shared Android test runner and testing dependencies.
- `test-app`: Instrumented-test host application.

## Requirements

- JDK 26
- Android SDK with API 37
- Gradle wrapper from this repository

This workspace targets `JAVA_HOME=D:\jdk-26`. JDK 17 can start Gradle, but it cannot compile
modules because the project now uses Java/Kotlin target 26.

## Common Commands

```powershell
$env:JAVA_HOME='D:\jdk-26'
.\gradlew.bat testDebugUnitTest detekt spotlessCheck --warning-mode all
```

Generate aggregated API documentation from Kotlin KDoc and Java Javadoc comments:

```powershell
$env:JAVA_HOME='D:\jdk-26'
.\gradlew.bat :apiDocs --warning-mode all
```

The generated Dokka HTML entry point is `build/dokka/html/index.html`.

Build a signed release APK with the local temporary signing properties:

```powershell
$env:JAVA_HOME='D:\jdk-26'
$store = Join-Path (Get-Location) 'build\local-signing\release-temp.p12'
.\gradlew.bat :app:assembleRelease `
  "-Pandroid.injected.signing.store.file=$store" `
  -Pandroid.injected.signing.store.password=temporary-release-pass `
  -Pandroid.injected.signing.key.alias=jasmine `
  -Pandroid.injected.signing.key.password=temporary-release-pass
```

## Architecture

The app follows a layered modular shape:

- UI collects state from ViewModels and owns screen layout concerns.
- ViewModels orchestrate use cases and expose immutable UI state.
- Domain use cases validate intent and map business outcomes.
- Data repositories hide DAO details from higher layers.
- Database code owns Room schema and SQLCipher setup.

Paging 3 is intentionally not included because the UI does not consume `PagingData` yet. If pagination is needed later, introduce it from DAO through Repository to UI in one complete flow.

## Security Notes

- The database is encrypted with SQLCipher.
- The SQLCipher passphrase is derived with PBKDF2-HMAC-SHA256 using a persisted random salt and a persisted random secret stored in encrypted preferences.
- Release signing secrets must stay outside source control. Use `keystore.properties.example` as a template.

## Quality Gates

The main verification chain is:

```powershell
.\gradlew.bat testDebugUnitTest detekt spotlessCheck :checkApiDocs :app:assembleRelease :feature-agent:compileDebugAndroidTestKotlin :test-app:assembleDebug --warning-mode all --continue
```

## Documentation

- `:apiDocs`: generates aggregated Dokka API docs from code comments.
- `:checkApiDocs`: verification task for ensuring API docs remain buildable.
- `CONTRIBUTING.md`: development workflow and contribution expectations.
- `CHANGELOG.md`: notable project changes.
- `BUILD_VERIFICATION.md`: build verification notes.
- `FINAL_COMPLETE_REPORT.md`, `FIXES_COMPLETE_REPORT.md`, `FIXES_SUMMARY.md`: historical remediation reports.
