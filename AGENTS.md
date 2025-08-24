# Repository Guidelines

## Project Structure & Module Organization
- common: Shared Kotlin Multiplatform code and resources. Sources in `src/<target>Main/kotlin`, shared resources via `src/commonMain/moko-resources`.
- android: Android app module. App ID from Gradle extras; flavors `foss` and `play`.
- desktop: Compose Desktop app. Entry at `desktop/src/jvmMain/kotlin/Main.kt`; icons in `desktop/src/jvmMain/resources`.
- iosApp: Xcode workspace and iOS-specific code (`iosApp/iosApp.xcworkspace`). CocoaPods integrates the `common` framework.
- Build scripts: Root `build.gradle.kts`, module `build.gradle.kts`, and `conveyor.conf` for desktop packaging.

## Build, Test, and Development Commands
- Desktop build: `./gradlew :desktop:build` — builds the desktop app JARs/artifacts.
- Desktop run: `./gradlew :desktop:run` — runs Compose Desktop locally.
- Desktop packages (examples):
  - Windows x64: `conveyor -Kapp.machines=windows.amd64 make windows-zip`
  - macOS arm64: `conveyor -Kapp.machines=mac.arm64 make unnotarized-mac-zip`
- Android debug: `./gradlew :android:assembleDebug` (install: `:android:installDebug`).
- iOS archive → IPA: `./gradlew buildIPA` (requires Xcode; outputs under `iosApp/output`).

## Coding Style & Naming Conventions
- Language: Kotlin (KMP + Compose). Prefer 4-space indentation, Kotlin standard naming (classes UpperCamelCase, functions lowerCamelCase, constants UPPER_SNAKE_CASE).
- Packages: `dev.zwander.*` (e.g., `dev.zwander.common`). Place platform code in `src/<target>Main/kotlin`.
- Formatting: Use IDE/Gradle defaults; Android Lint is enabled (`android { lint { abortOnError=false } }`). No ktlint/spotless configured.

## Testing Guidelines
- No formal test suites in repo today. PRs adding tests are welcome.
- Suggested layout: `common/src/commonTest/kotlin` for shared tests; platform tests in `android/src/test|androidTest`.
- Run (if added): `./gradlew test` for unit tests; `connectedAndroidTest` for device tests.

## Commit & Pull Request Guidelines
- Commits: Short, imperative subject lines (e.g., "Update dependencies", "Fix Conveyor build"). Keep focused; optionally include scope.
- PRs: Provide a clear description, link related issues, and note target platforms. Include screenshots or screen recordings for UI changes.
- Checks: Ensure app builds for affected targets (`:desktop`, `:android`, or iOS via Xcode). Update resources/strings when needed.

## Security & Configuration Tips
- Do not commit secrets. Versioning and app IDs come from root Gradle extras (`build.gradle.kts`); adjust there when bumping versions.
- Bugsnag is integrated; verify configuration per platform before release. Conveyor builds require the Conveyor CLI installed.

