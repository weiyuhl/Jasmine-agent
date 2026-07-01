# JasmineAgent

JasmineAgent is a Kotlin-first Android sample for an Agent-style mobile app. It uses a layered multi-module architecture with Jetpack Compose, Hilt, Room, SQLCipher, Navigation 3, Rust/UniFFI, and focused quality tooling.

## Project Structure

- `app`: application assembly, startup wiring, main Activity, Navigation 3 host, release signing, and packaging rules.
- `core/common`: common result/error primitives and low-level shared types.
- `core/model`: shared read-only models and domain enums.
- `core/navigation`: Navigation 3 route keys and feature entry registration contracts.
- `core/designsystem`: Compose Material 3 theme and design-system primitives.
- `core/ui`: compatibility UI module that forwards theme access to `core/designsystem`.
- `core/domain`: repository contracts, use cases, and pure Kotlin domain validation policies.
- `core/database`: Room entities, DAO, SQLCipher database setup, and database security tests.
- `core/datastore`: DataStore infrastructure.
- `core/network`: network infrastructure.
- `core/testing`: shared Android test runner and testing infrastructure.
- `data/agent`: Agent repository implementation and data-source/native bridge integration.
- `feature/home/api`: Home feature public API.
- `feature/home/impl`: Agent home UI, ViewModel, Navigation 3 entry registration, and feature tests.
- `native/bridge`: Kotlin-facing Rust/UniFFI bridge.
- `rust`: Cargo workspace for Rust crates.
- `benchmark`: Macrobenchmark and baseline-performance verification.
- `test-app`: instrumented-test host application.

## Requirements

- JDK 26
- Android SDK with API 37
- Gradle wrapper from this repository

This workspace targets `JAVA_HOME=D:\jdk-26`. JDK 17 can start older Android builds, but this project now uses Java/Kotlin target 26.

## Common Commands

```powershell
$env:JAVA_HOME='D:\jdk-26'
$env:Path='D:\jdk-26\bin;' + $env:Path
.\gradlew.bat :app:compileDebugKotlin --warning-mode all
```

Run the main local verification chain:

```powershell
$env:JAVA_HOME='D:\jdk-26'
$env:Path='D:\jdk-26\bin;' + $env:Path
.\gradlew.bat testDebugUnitTest detekt spotlessCheck --warning-mode all
```

Generate aggregated API documentation from Kotlin KDoc and Java Javadoc comments:

```powershell
$env:JAVA_HOME='D:\jdk-26'
$env:Path='D:\jdk-26\bin;' + $env:Path
.\gradlew.bat :apiDocs --warning-mode all
```

The generated Dokka HTML entry point is `build/dokka/html/index.html`.

## Architecture

The app follows the report's layered modular shape:

- `app` is a thin assembly layer and does not directly depend on `data:*` or call Rust FFI.
- UI composables collect immutable state from ViewModels and send user events upward.
- ViewModels orchestrate domain use cases and expose UI state.
- Domain use cases own business intent and depend on repository contracts, not data sources or native code.
- Data repositories hide DAO, DataStore, network, and Rust bridge details from higher layers.
- Feature implementations contribute Navigation 3 entries through Hilt multibindings.
- Shared route keys live in `core/navigation`; shared read models live in `core/model`.

Paging 3 is intentionally not included because the UI does not consume `PagingData` yet. If pagination is needed later, introduce it from DAO through Repository to UI in one complete flow.

## Security Notes

- The database is encrypted with SQLCipher.
- The SQLCipher passphrase is derived with PBKDF2-HMAC-SHA256 using a persisted random salt and a persisted random secret stored in encrypted preferences.
- Release signing secrets must stay outside source control. Use `keystore.properties.example` as a template.

## Documentation

- `docs/architecture-alignment.md`: current alignment with `Android端Agent应用架构深度研究报告.md`.
- `:apiDocs`: generates aggregated Dokka API docs from code comments.
- `:checkApiDocs`: verification task for ensuring API docs remain buildable.
- `CONTRIBUTING.md`: development workflow and contribution expectations.
- `CHANGELOG.md`: notable project changes.
- `BUILD_VERIFICATION.md`: build verification notes.
- `FINAL_COMPLETE_REPORT.md`, `FIXES_COMPLETE_REPORT.md`, `FIXES_SUMMARY.md`: historical remediation reports.
