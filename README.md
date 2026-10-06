# Loyalty Cards

Offline Android and iOS app for keeping store loyalty cards (IKEA, Spar, Mercator, Merkur, ...) in one place.
Built with Kotlin Multiplatform and Compose Multiplatform: UI, storage and logic are shared, the platform apps are thin shells.

## Project layout

```
shared/        Kotlin Multiplatform module: all UI, view models and storage
  commonMain/
    kotlin/app/loyaltycards/
      App.kt                 Root composable
      domain/                LoyaltyCard, BarcodeFormat, StoreCatalog (known stores and colors)
      data/                  CardRepository + SQLDelight implementation
      di/AppContainer.kt     Manual dependency wiring
      ui/cards/              Wallet (stacked cards, enlarged card) and edit mode (reorder, remove)
      ui/add/                Add card (manual entry; the scan panel is a placeholder)
      ui/components/         Card visuals, barcode drawing, buttons
      ui/theme/              Theme from the design: colors, DM Sans type (light + dark)
    composeResources/font/   DM Sans (SIL Open Font License, see licenses/)
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

## Stores and logos

The store list (names, card names, colors) and the store logos are generated from `docs/brands/`:
edit `docs/brands/brands.json` or the SVGs in `docs/brands/logos/`, then run `python scripts/generate_brands.py`.
Logos are single-color [Simple Icons](https://simpleicons.org) drawings (CC0). The marks themselves are the brands'
trademarks; the app says it isn't affiliated with any store. To drop a brand's logo after a takedown request, delete its
SVG (or add it to `SKIPPED_LOGOS`) and regenerate.
