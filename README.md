# CityBikes Explorer

A small Android demo app for browsing bike-share stations, viewing a station's
bikes, and running a simulated ride end to end. It is built with Jetpack Compose
and a clean MVVM + interactor architecture, and runs entirely against bundled
mock JSON (no real backend required).

## Screens

- **Stations (Explore)** – list of stations with search-by-name (filters once you
  type 3+ characters) and availability filter pills (All / Bikes available / No bikes),
  with an empty state.
- **Station detail** – hero image (Coil), stats, address/distance/coordinates/last-updated,
  a favorite toggle, and a "Select a Bike" action.
- **Select a bike** – the station's available bikes with battery level.
- **Ride flow** – Confirm/Unlock → Ride in progress (live timer; back is blocked while
  riding) → Ride summary (duration, distance, total cost in NZD).
- **Favorites** – favorited stations (favorites are held in an in-memory cache);
  tapping one deep-links into its detail.
- **Profile** – a mock profile with a support link.

## How this project was built

This codebase was developed collaboratively with Claude (Anthropic's Claude Code).
Claude helped with, in roughly this order:

1. Adding the core stack — **Retrofit, Coroutines, and Hilt** — and an MVVM setup.
2. A **local-JSON mock API**: an OkHttp interceptor (`LocalJsonInterceptor`) serves
   files from `assets/` so the app works with no live server. Swapping in a real API
   later just means removing that interceptor.
3. An **anti-corruption layering** per feature: `Response` (wire) → `ViewItem`
   (interactor/domain) → `State` (ViewModel/UI), each with its own mappers.
4. Building each screen and its **ViewModel + Interactor**, the **Navigation Compose**
   graph, top app bars, Coil image loading with a preview placeholder, and the
   simulated ride with an in-memory `RideRepository`.
5. The "extras": stations **search + filters**, the **Favorites** cache + screen, the
   **Profile** screen, unifying navigation into a single graph, and **unit tests for
   every ViewModel**.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- MVVM with feature interactors
- Hilt (DI), Retrofit + kotlinx.serialization, OkHttp, Coroutines/Flow
- Navigation Compose, Coil 3 (images)
- JUnit + kotlinx-coroutines-test

`minSdk` 24, `targetSdk`/`compileSdk` 36.

## Running the app

### Option A — Android Studio + emulator (or a device)

1. Install [Android Studio](https://developer.android.com/studio) (latest stable).
2. Open this project folder; let Gradle sync and download the SDK components it asks for.
3. Create an emulator via **Device Manager** (any phone image, API 24+), or connect a
   physical device with **USB debugging** enabled.
4. Press **Run** (▶). The app installs and launches.

> Note: the project uses a recent Android Gradle Plugin. If Studio prompts to install a
> matching SDK/build-tools or AGP version, accept it.

### Option B — Install the prebuilt APK

A debug APK is committed at [`dist/citybikesexplorer-debug.apk`](dist/citybikesexplorer-debug.apk).

With a device/emulator connected and `adb` on your PATH:

```bash
adb install -r dist/citybikesexplorer-debug.apk
```

Or copy the file to an Android device and open it (you may need to allow
"install from unknown sources" for your file manager).

### Building the APK yourself

```bash
./gradlew :app:assembleDebug
# output: app/build/outputs/apk/debug/app-debug.apk
```

## Running the tests

```bash
./gradlew :app:testDebugUnitTest
```

This runs the unit tests for all ViewModels.
