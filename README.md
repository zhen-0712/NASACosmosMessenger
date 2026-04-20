# NASA Cosmos Messenger 

A conversational Android app where users can chat with **Nova**, an AI assistant that reveals what the universe looked like on any date — powered by NASA's Astronomy Picture of the Day (APOD) API.

---

## Architecture

This project follows **MVVM + Repository Pattern** with a clean layer separation:

```
app/
├── data/
│   ├── local/          # Room database (favorites + APOD cache)
│   ├── model/          # Data models & DateParser
│   ├── remote/         # Retrofit + NASA API service
│   └── repository/     # ApodRepository, FavoriteRepository
├── ui/
│   ├── chat/           # Nova chat screen
│   ├── favorites/      # Favorites screen
│   └── theme/          # Color, typography, theme
├── utils/              # StarCardGenerator, ShareUtils
└── viewmodel/          # ChatViewModel, FavoritesViewModel
```

**Why Kotlin + Jetpack Compose?**
- Kotlin is the recommended language for Android development with modern, concise syntax and null safety
- Jetpack Compose enables declarative UI with less boilerplate, aligning with LINE's internal tech stack
- MVVM + Repository cleanly separates concerns — UI, business logic, and data access are independently testable
- StateFlow + Coroutines provide reactive, lifecycle-aware data streams without manual lifecycle management

**Tech Stack:**
- Language: **Kotlin**
- UI: **Jetpack Compose**
- Architecture: **MVVM + Repository**
- Database: **Room** (favorites + offline cache)
- Networking: **Retrofit + OkHttp**
- Image Loading: **Coil**
- Navigation: **Navigation Compose**
- Testing: **JUnit4 + MockK + Coroutines Test**
- CI/CD: **GitHub Actions**

---

## Core Features

### Tab 1 — Nova Chat
- Conversational chat UI with message bubbles (user on right, Nova on left)
- Input a date → Nova fetches the NASA APOD for that date
- Long press on an image message to add it to favorites
- Auto-scroll to latest message
- Keyboard-aware layout (bottom navigation hides when keyboard is open)

### Tab 2 — Favorites
- Grid view of all saved space photos
- Each card shows image, title, and date with an overlay gradient design
- Tap the delete button to remove a favorite (with confirmation dialog)

---

## Supported Date Formats

Nova recognizes the following date input formats:

| Format | Example |
|--------|---------|
| `YYYY/MM/DD` | `1995/06/20` |
| `YYYY-MM-DD` | `1995-06-20` |
| `YYYY.MM.DD` | `1995.06.20` |
| Single-digit month/day | `2000/1/5` → normalized to `2000-01-05` |

Dates embedded in sentences are also recognized:
> "我的生日是 1995/06/20，那天宇宙長什麼樣子？"

**Valid date range:** 1995-06-16 (APOD launch date) to today.

---

## Bonus Features

### 1. Offline Cache (Room)
Previously fetched APOD data is stored in a local Room database. When the device is offline:
- If the requested date has been cached → returns cached data
- If no date is specified → returns the most recently cached APOD
- If no cache exists → shows a clear error message

### 2. Share Star Card
Tap the share icon on any APOD image card to generate a **birthday star card** — a stylized image combining the APOD photo, title, and date — and share it via the system share sheet.

### 3. Custom Cosmos Theme (其他)
A hand-crafted color palette inspired by **雪光の静物 (Winter Light Still Life)** for a refined, elegant aesthetic:
- `WinterGray #394045` — primary text and headers
- `IceBlue #5D696D` — secondary elements
- `SilverMilk #A0B2C7` — accents and highlights
- `SoftWhite #E5E8EB` — backgrounds

All UI components follow this unified theme — chat bubbles, date picker, navigation bar, and favorites cards.

### 4. GitHub Actions CI/CD (其他)
Automated testing pipeline to ensure code quality on every push:
- **Every push to any branch** → runs Unit Tests (~2 min)
- **PR to main** → runs both Unit Tests + Instrumented Tests on Android emulator

### 5. Compose DatePicker (其他)
Replaced the system default Android DatePickerDialog with Jetpack Compose's native `DatePicker`, fully styled to match the app's color scheme.

### 6. Keyboard-Aware Navigation (其他)
Bottom navigation bar automatically hides when the soft keyboard appears, maximizing visible chat area and preventing layout overlap.

---

## Testing

```
Unit Tests (JUnit4 + MockK)
├── parser/
│   ├── DateParserFormatTest        — slash / dash / dot formats
│   ├── DateParserExtractionTest    — date extraction from sentences
│   ├── DateParserBoundaryTest      — APOD earliest date boundary
│   └── DateParserInvalidInputTest  — empty / invalid inputs
├── viewmodel/
│   ├── ChatViewModelMessageTest    — send message, API success/failure, video type
│   ├── ChatViewModelFavoriteTest   — add favorite, duplicate detection
│   ├── FavoritesViewModelTest      — list display, delete operation
│   └── ShareStarCardTest           — share eligibility validation

Instrumented Tests (Room in-memory + AndroidJUnit4)
├── db/
│   ├── FavoriteDaoTest             — insert, delete, isFavorite, ordering
│   ├── ApodCacheDaoTest            — cache insert, query, replace
│   └── OfflineCacheTest            — end-to-end offline scenario tests
├── repository/
│   └── ApodRepositoryTest          — online fetch + cache, offline fallback
└── utils/
    └── StarCardGeneratorTest       — bitmap generation and file output
```

---

## Setup

1. Clone the repository
2. Get a free NASA API Key from [https://api.nasa.gov/](https://api.nasa.gov/)
3. Add to `local.properties` (do not commit this file):
   ```
   NASA_API_KEY=your_api_key_here
   ```
4. Build and run in Android Studio (Electric Eel or later)

**Requirements:** Android API 26+, Kotlin 2.1+, JDK 21

---

## Screen Recording

The screen recording demonstrates:
- Date recognition in multiple formats (`1995/06/20`, `1995-06-20`)
- Long press to add to favorites + Snackbar confirmation
- Browsing and deleting favorites in Tab 2
- Offline cache fallback behavior
- Share star card feature

---

*Built with Kotlin + Jetpack Compose for LINE TECH FRESH 2026 Android Pre-Assessment*
