# Eat.io

A native Android food delivery portfolio app built with Kotlin and Jetpack Compose. Explore restaurants, customize dishes, apply a coupon, place a local order, and follow its simulated delivery progress.

The interface is in Brazilian Portuguese, with a purple visual identity inspired by delivery apps. The project demonstrates reactive local persistence, domain rules, dependency injection, and unidirectional UI state management.

## Demo

Screenshots below were captured before the Eat.io rename and still display the previous brand.

| Home | Search | Restaurant |
| --- | --- | --- |
| ![Home](docs/screenshots/01_home_screen.png) | ![Search](docs/screenshots/02_search_screen.png) | ![Restaurant](docs/screenshots/03_restaurant_detail.png) |

| Dish customization | Cart | Checkout |
| --- | --- | --- |
| ![Customization](docs/screenshots/04_dish_customization.png) | ![Cart](docs/screenshots/05_cart_screen.png) | ![Checkout](docs/screenshots/06_checkout_screen.png) |

| Order tracking | Order history | Profile |
| --- | --- | --- |
| ![Tracking](docs/screenshots/07_order_tracking.png) | ![History](docs/screenshots/08_orders_history.png) | ![Profile](docs/screenshots/09_profile_screen.png) |

## Features

- Restaurant discovery, menu browsing, search, and category filters.
- Required and optional dish selections, quantity controls, and price calculation.
- Single-restaurant cart with confirmation before replacing its contents.
- Coupons (`PURPLE10`, `EATME10`, and `PURPLE15`) carried from the cart into checkout and the stored order.
- Delivery address and payment method selection for local orders.
- Transactional order creation: saving the order, its items and options, and clearing the cart succeed or roll back together.
- Checkout submission guard, persistence error feedback, and retry after failure.
- Persisted order history and manually simulated delivery status changes.
- Demonstration profile, wallet balance, and preference controls.

## Scope and limitations

This is a local demonstration app. Room stores a seeded restaurant catalog, cart, and orders; there is no remote backend, authentication service, real payment processing, GPS tracking, or courier integration. Payment methods represent selections saved on an order.

Profile preferences and wallet changes live in ViewModel memory and reset when that state is recreated. Database upgrades currently use destructive migration, so stored data may be lost after a schema version change. UI navigation uses string routes with typed arguments.

## Architecture

```text
:app                  Compose screens, navigation, ViewModels, and Hilt wiring
:core:domain-shared    Pure Kotlin models, repository contracts, and use cases
:core:database        Room entities, DAOs, mappers, seed data, and repositories
:core:designsystem    Theme, spacing tokens, reusable components, and skeletons
```

The domain module has no Android dependency. Repository interfaces decouple presentation from persistence. ViewModels expose immutable UI state through `StateFlow` and handle intents; one-time effects use channels. Screens collect state with lifecycle awareness.

Prices use a `Money` value class backed by integer cents (`Long`). Arithmetic checks overflow, and percentage calculations use explicit rounding. Checkout receives the selected discount through a navigation argument backed by `SavedStateHandle`.

## Technology

| Area | Tools |
| --- | --- |
| Language | Kotlin, Coroutines, Flow |
| UI | Jetpack Compose, Material 3, Coil |
| Navigation | Navigation Compose |
| Dependency injection | Dagger Hilt |
| Persistence | Room / SQLite |
| Tests | JUnit 4/5, MockK, Turbine, Truth, Coroutines Test |
| Android | Minimum SDK 26, compile/target SDK 35 |

Dependency versions are defined in [the version catalog](gradle/libs.versions.toml).

## Run locally

Requirements: JDK 17, Android SDK 35, and an Android device or emulator running API 26 or newer. Open the project in Android Studio and allow Gradle to sync, or use the included wrapper:

```bash
# Build a debug APK
./gradlew assembleDebug

# Install on a connected device or emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The catalog is seeded automatically when the local database is empty. No API key or backend setup is required. The application ID remains `com.samuelbaldasso.ifoodclone`.

## Validation

```bash
# Android module unit tests and pure Kotlin domain tests
./gradlew testDebugUnitTest :core:domain-shared:test

# Android static analysis
./gradlew lintDebug
```

Tests cover monetary arithmetic and rounding, dish pricing and validation, repository behavior with mocked DAOs, and ViewModel state and effects. Checkout regression tests cover coupon forwarding, repeated confirmation, and retry after persistence failure. Repository tests also verify persistence failure reporting and cancellation propagation.

These unit tests do not prove real SQLite transaction behavior or complete UI flows. Room integration tests and Compose UI tests remain future improvements; the instrumented test currently included is the Android template smoke test.

## Architecture decisions

The following records are currently written in Portuguese:

- [ADR-001: Integer cents for monetary values](docs/adr/ADR-001-whole-cents-money-domain.md)
- [ADR-002: Embedded Room database](docs/adr/ADR-002-embedded-room-database-backend.md)
- [ADR-003: Unidirectional data flow](docs/adr/ADR-003-udf-mvi-architecture.md)
- [ADR-004: Native splash screen and edge-to-edge](docs/adr/ADR-004-native-splash-and-edge-to-edge.md)
- [ADR-005: Shimmer loading skeletons](docs/adr/ADR-005-compose-shimmer-skeletons.md)
