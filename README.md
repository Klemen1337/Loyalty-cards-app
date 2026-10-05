# Loyalty Cards

Offline Android and iOS app for keeping store loyalty cards (IKEA, Spar, Mercator, Merkur, ...) in one place.
Built with Kotlin Multiplatform and Compose Multiplatform: UI, storage and logic are shared, the platform apps are thin shells.

## Project layout

```
shared/        Kotlin Multiplatform module: all UI, view models and storage
  commonMain/
    kotlin/app/loyaltycards/
      App.kt                 Root composable
      domain/                LoyaltyCard, BarcodeFormat
      data/                  CardRepository + SQLDelight implementation
      di/AppContainer.kt     Manual dependency wiring
      ui/cards/              Card list screen (view, drag to reorder, remove with undo)
      ui/theme/              Material 3 theme (light + dark)
    sqldelight/              Database schema and queries (seeds sample cards on first launch)
  androidMain/               Android SQLite driver
  iosMain/                   iOS SQLite driver + MainViewController entry point
androidApp/    Android application (Activity + Application class)
iosApp/        Xcode project (SwiftUI shell hosting the shared Compose UI)
```

Cards are stored locally in SQLite via [SQLDelight](https://sqldelight.github.io/sqldelight/). No network access.

## Requirements

- JDK 17+ (Android Studio's bundled JBR works)
- Android Studio (latest stable) with the Kotlin Multiplatform plugin
- For iOS: a Mac with Xcode 16+

## Running

**Android:** open the project folder in Android Studio, let Gradle sync, pick the `androidApp` run configuration and run it on an emulator or device.
From the command line: `./gradlew :androidApp:installDebug`.

**iOS (on a Mac):** open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator. The build phase calls Gradle to compile the shared framework.
To run on a real device, set your `TEAM_ID` in `iosApp/Configuration/Config.xcconfig`.

## Before publishing

- `applicationId` / bundle id are `app.loyaltycards` placeholders: change them in `androidApp/build.gradle.kts` and `iosApp/Configuration/Config.xcconfig` before the first store upload, since they can't change afterwards.
- App icons, release signing and store listings are not set up yet.
