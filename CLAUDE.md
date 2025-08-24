# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
HINT Control is a cross-platform Kotlin Multiplatform application for controlling T-Mobile Home Internet gateways (Arcadyan KVD21, TMOG4AR, Sagemcom Fast 5688W, Sercomm TMOG4SE, Nokia 5G21). Built with JetBrains Compose Multiplatform, it targets Android, iOS, Desktop (Windows/macOS/Linux).

## Key Technologies
- **Kotlin Multiplatform** with Compose Multiplatform UI framework
- **Gradle** (Kotlin DSL) for build management
- **Conveyor** for desktop distribution packaging
- **Moko Resources** for multiplatform resource management
- **Room/SQLDelight** for database operations
- **Bugsnag** for error reporting
- **Ktor** for HTTP client operations

## Build Commands

### Desktop
```bash
# Build desktop application
./gradlew :desktop:build

# Create platform-specific packages using Conveyor (after building)
conveyor -Kapp.machines=windows.amd64 make windows-zip  # Windows x86
conveyor -Kapp.machines=mac.aarch64 make unnotarized-mac-zip  # Apple Silicon Mac
conveyor -Kapp.machines=linux.amd64 make debian-package  # Linux Debian
```

### Android
```bash
# Build APK
./gradlew :android:assembleDebug
./gradlew :android:assembleRelease

# Install on connected device
./gradlew :android:installDebug
```

### iOS
```bash
# Build IPA archive
./gradlew buildIPA

# Or use Xcode directly:
# 1. Open iosApp/iosApp.xcworkspace in Xcode
# 2. Build and run on device/simulator
```

## Architecture Overview

### Module Structure
- **common/**: Shared multiplatform code
  - `commonMain/`: Core business logic, UI components, models
  - `androidMain/`: Android-specific implementations
  - `iosMain/`: iOS-specific implementations  
  - `desktopMain/`: Desktop-specific implementations
  - `darwinMain/`: Shared Apple platform code

### Key Components
- **Model Layer** (`common/src/commonMain/kotlin/dev/zwander/common/model/`)
  - `GlobalModel`: Application-wide state management
  - `MainModel`: Main data and device control logic
  - `SettingsModel`: User preferences and configuration
  - `UserModel`: Authentication and user session

- **Data Adapters** (`common/src/commonMain/kotlin/dev/zwander/common/model/adapters/`)
  - Device-specific implementations for different gateway models (Nokia, Arcadyan, etc.)
  - JSON serialization/deserialization for API communication

- **UI Pages** (`common/src/commonMain/kotlin/dev/zwander/common/pages/`)
  - `LoginPage`: Gateway authentication
  - `MainPage`: Primary device control interface
  - `ClientListPage`: Connected devices management
  - `WifiConfigPage`: WiFi settings configuration
  - `SettingsPage`: Application settings

- **HTTP Client** (`common/src/commonMain/kotlin/dev/zwander/common/util/HTTPClient.kt`)
  - Gateway communication abstraction
  - Cookie management via `GlobalCookiesStorage`

## Development Patterns

### State Management
- Uses Kotlin Flow and MutableStateFlow for reactive state
- Compose collectAsState/collectAsMutableState for UI binding
- PersistentMutableStateFlow for persisted preferences

### Platform-Specific Code
- Expect/actual pattern for platform implementations
- Platform detection via `korlibs.platform.Platform`
- Native interop for iOS (Bugsnag) via .def files

### Resource Management
- Moko Resources for strings, images, colors
- Localization support via XML string resources (30+ languages)
- Platform-specific resource loading utilities

### Error Handling
- Custom exceptions in `common/exceptions/`
- Bugsnag integration for crash reporting
- HTTP error state management via GlobalModel