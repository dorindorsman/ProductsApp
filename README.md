# ProductsApp

A modern Android application demonstrating clean architecture, offline-first design, and Kotlin best practices.

---

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/dorindorsman/ProductsApp.git
   ```
2. Open the project in **Android Studio Hedgehog** or later
3. Wait for Gradle sync to complete
4. Run on a physical device or emulator (API 26+)

> **Note:** Biometric authentication requires a physical device with enrolled fingerprint or face recognition.

---

## Testing Credentials

| Field    | Value       |
|----------|-------------|
| Username | user        |
| Password | password123 |

---

## Features

### Authentication
- Username/password login with input validation
- Biometric authentication (fingerprint/face)
- Persistent login session via DataStore
- Lottie animation on login screen
- Auto-login on app relaunch

### Products
- Paginated product list (Paging 3 + RemoteMediator)
- Search, sorting, and category filtering
- Product detail screen
- Offline caching — Room as single source of truth

### Favorites
- Add/remove favorites with local persistence
- Undo remove action via Snackbar

### Settings
- Dark/light mode toggle
- Language switching (English/Hebrew)
- Logout

### CRUD
- Add/Edit/Delete products locally
- Locally modified products persist across sessions
- Reset local changes from API

---

## Architecture

The app follows **MVVM + Clean Architecture** with a clear separation of layers:

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities, mappers
│   ├── remote/         # Retrofit API, DTOs
│   └── repository/     # Repository implementations, RemoteMediator
├── domain/
│   ├── model/          # Domain models
│   └── repository/     # Repository interfaces
├── ui/
│   ├── auth/           # Login screen + ViewModel
│   ├── products/       # Product list, detail, add/edit
│   ├── favorites/      # Favorites screen + ViewModel
│   ├── settings/       # Settings screen + ViewModel
│   ├── navigation/     # NavGraph, BottomNavBar
│   └── common/         # Shared UI utilities
└── di/                 # Hilt modules
```

### Key Architecture Decisions

**Offline-first with RemoteMediator** — Room is the single source of truth. The `ProductsRemoteMediator` fetches from the API and writes to Room, while the UI always reads from Room via `PagingSource`. Locally modified products and favorites are preserved across remote syncs.

**Unidirectional data flow** — Each screen has a corresponding ViewModel exposing `UiState` via `StateFlow`. UI events flow up to the ViewModel; state flows down to the UI.

**Dependency injection** — Hilt provides scoped dependencies across all layers, making the codebase testable and modular.

---

## Tech Stack

| Category       | Library                        |
|----------------|-------------------------------|
| Language       | Kotlin                        |
| UI             | Jetpack Compose + Material 3  |
| Architecture   | MVVM + Clean Architecture     |
| Async          | Coroutines + Flow             |
| DI             | Hilt                          |
| Networking     | Retrofit + OkHttp             |
| Local Storage  | Room + DataStore              |
| Pagination     | Paging 3                      |
| Images         | Coil                          |
| Animation      | Lottie                        |
| Biometric      | AndroidX Biometric            |

---

## AI Usage Report

### Tools Used
- **Claude (claude.ai)** — used for specific isolated tasks during development

### What AI Assisted With
- Generating Room entity boilerplate (entity + DAO skeleton)
- Setting up the `libs.versions.toml` dependency catalog structure
- Quick reference for Paging 3 `RemoteMediator` API signature
- Providing String resource templates for Hebrew\English localization and Implementing them in the app
- Help to draft the README structure and content outline
- Helping to identify bugs by providing partial code snippets for error reproduction and debugging

### What Was Implemented and Reviewed Manually
- Overall architecture design — the decision to use MVVM + Clean Architecture with feature-based packaging
- All ViewModel logic, UiState modeling, and state management
- The offline-first strategy — deciding which data to preserve on remote sync
- Navigation graph and back stack management
- Biometric authentication integration and `FragmentActivity` migration
- Dark mode and language switching implementation
- All bug fixes throughout development
- Hebrew string resources and localization
- Splash screen and session persistence logic
- Full code review of any AI-generated snippets before integration

### Example Prompts Used

1. *"What are the required Gradle dependencies for Room with Paging 3 support in a Kotlin DSL project?"*

2. *"What is the correct `RemoteMediator` method signature in Paging 3?"*

3. *"Generate a basic Room entity class for a Product with these fields: id, title, price, thumbnail, category, rating, stock, brand"*

### How Correctness Was Verified
- All AI-generated code was reviewed and adapted manually before use
- Tested on a physical Samsung device throughout development
- Each feature verified end-to-end after implementation
- AI was used as a reference tool, not as a code generator for logic or architecture