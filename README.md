# ProductsApp

A modern Android application built as part of a take-home assignment, demonstrating clean architecture, offline-first design, and Kotlin best practices.

---

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/dorindorsman/ProductsApp.git
   ```
2. Open the project in **Android Studio Hedgehog** or later
3. Wait for Gradle sync to complete
4. Run on a physical device or emulator (API 26+)

> **Note:** Biometric authentication requires a physical device with an enrolled fingerprint or face recognition.

---

## Testing Credentials

| Field    | Value       |
|----------|-------------|
| Username | user        |
| Password | password123 |

---

## Features

### Authentication
- Username/password login with input validation and loading states
- Biometric authentication (fingerprint/face) via AndroidX Biometric
- Persistent login session stored in DataStore
- Lottie animation on the login screen
- Splash screen that auto-routes returning users — no login flash

### Products
- Paginated product list powered by Paging 3
- Custom `PagingSource` with Room as local cache and API fallback
- Product detail screen with full information
- Search by title with 300ms debounce
- Sorting: default, price (asc/desc), rating, name A-Z
- Category filtering via horizontal filter chips
- Offline support: app remains functional with no internet connection

### Favorites
- Add/remove favorite products with a single tap
- Favorites persisted locally in Room
- Favorites survive app restarts and remote data syncs
- Undo remove action via Snackbar

### Settings
- Dark/light mode toggle, persisted via DataStore
- Language switching between English and Hebrew, applied via locale + Activity recreate
- Logout clears session and returns to login screen

### CRUD
- Add new products locally
- Edit existing products (both API-sourced and locally created)
- Delete locally modified products
- Locally modified products are preserved across remote syncs
- Reset all local changes and reload from API

### Testing
- Unit tests for `AuthRepository`, `ProductRepository`, `AuthViewModel`, `ProductsViewModel`
- MockK for mocking dependencies
- Turbine for Flow assertion
- Coroutines Test for coroutine-based testing

---

## Architecture

The app follows **MVVM + Clean Architecture** with strict separation of concerns across three layers:

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities, mappers
│   ├── remote/         # Retrofit API interface, response DTOs
│   └── repository/     # Repository implementations, PagingSource
├── domain/
│   ├── model/          # Pure Kotlin domain models
│   └── repository/     # Repository interfaces (contracts)
├── ui/
│   ├── auth/           # Login screen + AuthViewModel
│   ├── products/       # Product list, detail, add/edit dialog + ViewModels
│   ├── favorites/      # Favorites screen + FavoritesViewModel
│   ├── settings/       # Settings screen + SettingsViewModel
│   ├── splash/         # Splash screen for session routing
│   ├── navigation/     # NavGraph, BottomNavBar, Screen sealed class
│   └── common/         # Shared UI utilities
└── di/                 # Hilt modules (Network, Database, Repository)
```

### Key Architecture Decisions

**Offline-first** — `ProductsPagingSource` fetches from the API and writes to Room on every load. If the network call fails, it falls back to the local Room cache. Locally modified products and favorites are excluded from delete queries, preserving user data across syncs.

**Single source of truth** — Room is the only data source the UI reads from. The API only feeds Room; the UI never reads from the network directly.

**Unidirectional data flow** — Each screen has a dedicated ViewModel that exposes a single `UiState` via `StateFlow`. UI events flow up to the ViewModel; state flows down to the UI. No business logic lives in Composables.

**Feature-based packaging** — Each feature (auth, products, favorites, settings) is self-contained with its own screen, ViewModel, and any feature-specific components. This improves navigability and maintainability over type-based packaging.

**Session management** — Login state is stored as a boolean in DataStore. The Splash Screen reads this value on launch and routes to either the login screen or the main app, eliminating the login flash experienced with naive `startDestination` approaches.

---

## Tech Stack

| Category       | Library                                  |
|----------------|------------------------------------------|
| Language       | Kotlin                                   |
| UI             | Jetpack Compose + Material 3             |
| Architecture   | MVVM + Clean Architecture                |
| Async          | Coroutines + Flow                        |
| DI             | Hilt                                     |
| Networking     | Retrofit + OkHttp                        |
| Local Storage  | Room + DataStore                         |
| Pagination     | Paging 3                                 |
| Images         | Coil                                     |
| Animation      | Lottie                                   |
| Biometric      | AndroidX Biometric                       |
| Testing        | JUnit4 + MockK + Turbine + Coroutines Test |

---

## AI Usage Report

### Tools Used
- **Claude (claude.ai)** — used as a reference and boilerplate tool during development

### What AI Assisted With
- Generating initial Room entity and DAO boilerplate
- Setting up the `libs.versions.toml` version catalog structure
- Looking up correct method signatures for Paging 3 APIs

### What Was Implemented and Reviewed Manually
- Overall architecture design — layer separation, feature-based packaging, and dependency direction
- All ViewModel logic and `UiState` modeling
- Offline-first strategy — deciding which records to preserve on remote sync (favorites, locally modified)
- Navigation graph structure and back stack behavior
- Biometric integration and the `FragmentActivity` migration required to support it
- Dark mode and language switching via DataStore and `recreate()`
- Splash screen and persistent session logic
- All bug fixes: favorites persistence, edit/delete behavior, auto-login loop, duplicate products
- Hebrew localization and string resources
- Unit test design and implementation
- Manual review and adaptation of all AI-generated snippets before use

### Example Prompts Used

1. *"What are the required Gradle dependencies for Room with Paging 3 support in a Kotlin DSL project?"*

2. *"What is the correct method signature for PagingSource's load function in Paging 3?"*

3. *"Generate a basic Room entity class for a Product with fields: id, title, price, thumbnail, category, rating, stock, brand"*

### How Correctness Was Verified
- All AI-generated snippets were reviewed and manually adapted before integration
- Features tested end-to-end on a physical Samsung device
- Edge cases verified: no internet, empty favorites, reset after local edits, app restart after changes
- Unit tests written independently to validate core repository and ViewModel logic