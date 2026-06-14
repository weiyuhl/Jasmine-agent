# Contributing

Thank you for improving JasmineAgent. Keep changes small, testable, and aligned with the existing module boundaries.

## Workflow

1. Read `README.md` for the module map and verification commands.
2. Keep feature code in the owning module. Do not move UI concerns into navigation entry providers.
3. Prefer existing architecture: UI -> ViewModel -> UseCase -> Repository -> DAO.
4. Run formatting before review:

```powershell
$env:JAVA_HOME='D:\jdk-17.0.2'
.\gradlew.bat spotlessApply
```

5. Run the verification chain:

```powershell
.\gradlew.bat testDebugUnitTest detekt spotlessCheck :app:assembleRelease :feature-agent:compileDebugAndroidTestKotlin :test-app:assembleDebug --warning-mode all --continue
```

## Code Style

- Kotlin source is formatted by Spotless.
- Avoid broad refactors when a focused fix is enough.
- Keep Compose layout ownership inside screen-level composables.
- Keep domain validation independent from UI error models.
- Add tests when behavior, public contracts, or security-sensitive code changes.

## Dependencies

- Use `gradle/libs.versions.toml` for versions.
- Remove unused dependencies instead of leaving placeholder imports or dead APIs.
- When adding alpha or RC dependencies, document why they are needed.

## Security

- Do not commit real signing keys or passwords.
- Use `keystore.properties.example` as a template.
- Keep database passphrase material in encrypted preferences or Android-backed secure storage.

## Pull Request Checklist

- The change has a clear scope.
- Public APIs and module dependencies still match their exposed types.
- Documentation is updated when behavior or workflow changes.
- `spotlessCheck`, `detekt`, and relevant tests pass.
