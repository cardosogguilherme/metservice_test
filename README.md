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

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- MVVM with feature interactors
- Hilt (DI), Retrofit + kotlinx.serialization, OkHttp, Coroutines/Flow
- Navigation Compose, Coil 3 (images)
- JUnit + kotlinx-coroutines-test
- `minSdk` 24, `targetSdk`/`compileSdk` 36.


## Decisions & trade-offs

- **Anti-corruption layering.** I kept separate wire, domain, and UI models with
  mappers at each boundary, even though it means more DTOs and mapping code. The
  payoff is that a change to an API response stays contained at the wire layer
  instead of rippling through the interactors and UI.
- **Interactors for business logic.** Business logic lives in feature interactors
  rather than the ViewModels, which keeps ViewModels thin and focused on UI state.
- **Single module, for now.** The project is small enough that extra Gradle modules
  would be overhead without much payoff. As it grows, the ride flow and profile are
  the natural first candidates to split into their own modules.
- **Mock data sources.** With no real backend, the app runs against a mix of mock
  APIs, in-memory stores, and bundled mock JSON.
- **Test coverage.** Tests currently cover the ViewModels. With more time I'd add
  snapshot testing (Paparazzi) for the views and an Espresso journey test covering
  a full ride end to end.

## How this project was built

I used Claude Code as a pair-programming tool throughout, the same way I use
Copilot day to day. The design calls are mine: the MVVM + interactor split, the
anti-corruption layering (Response → ViewItem → State) with mappers at each
boundary, the local-JSON interceptor so the app runs with no backend, and the
in-memory repositories for favorites and ride state. Claude did a lot of the
implementation against those decisions — wiring Retrofit/Hilt, fleshing out
screens and ViewModels, and filling in test coverage — which let me move fast
while keeping the structure deliberate.

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
