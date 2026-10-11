# MoviQ Transit — Real-Time Public Bus Transportation Platform

MoviQ Transit is a complete, real‑time public bus transportation solution consisting of two Android applications and a shared backend:

- **Passenger App (`moviq-passenger`)**: Search bus journeys, compare fares and schedules, track live buses on Google Maps with real‑time driver GPS, and view stop‑progression timelines.
- **Driver App (`moviq-driver`)**: Authenticate driver shifts, verify pre‑trip vehicle checks, automatically collect and transmit device GPS coordinates via a foreground service, and manage active trip status.
- **Shared Backend (`moviq-backend`)**: FastAPI REST and WebSocket server for route queries, active‑trip lifecycle, location ingestion, and real‑time WebSocket coordinate streaming.

---

## 🎨 Implemented UI Screens

The platform implements a polished 6‑screen design system:

| # | Screen | App | Description |
|---|---|---|---|
| **01** | **Home & Search** | Passenger | Origin/destination inputs, swap action, time controls, search button, recent searches, nearby stops. |
| **02** | **Search Results** | Passenger | Journey list with sorting (fastest, cheapest, fewest transfers), departure/arrival times, fare display. |
| **03** | **Live Bus & Map** | Passenger | Interactive Google Maps canvas, bus route polyline, live bus marker, next‑stop preview, “Board at Central Station”. |
| **04** | **Route Stop Timeline** | Passenger | Vertical stop‑progression timeline with live speed and passenger‑capacity indicators. |
| **05** | **Shift & Pre‑Trip** | Driver | Shift greeting, line assignment card, GPS lock & LTE status, 3‑point pre‑trip checklist, “Start Trip” action. |
| **06** | **Active Trip Console** | Driver | Active line status, live GPS transmission badge, next stop banner, distance & ETA metrics, stop sequence, seats counter, “Hold to End Service”. |

---

## 📂 Project Structure

```text
Transit/
├── moviq-passenger/          # Android module for passengers
│   ├── src/main/java/com/moviq/transit/passenger/
│   │   ├── ui/
│   │   │   ├── screens/        # HomeScreen, SearchResultsScreen, LiveBusMapScreen, RouteTimelineScreen
│   │   │   ├── components/     # Bottom navigation, status bar, journey cards
│   │   │   └── theme/          # Design colors, typography
│   │   ├── data/               # Models, Retrofit API service, WebSocket live‑location listener
│   │   └── MainActivity.kt     # Jetpack Compose navigation & live GPS flow
│   └── AndroidManifest.xml
│
├── moviq-driver/             # Android module for drivers
│   ├── src/main/java/com/moviq/transit/driver/
│   │   ├── ui/
│   │   │   ├── screens/        # ShiftPreTripScreen, ActiveTripConsoleScreen
│   │   │   └── theme/          # Design tokens & typography
│   │   ├── service/            # LocationTrackingService (foreground GPS service)
│   │   ├── data/               # Driver models, Retrofit API, GPS uploader
│   │   └── MainActivity.kt     # Permissions handling & service lifecycle
│   └── AndroidManifest.xml
│
├── moviq-backend/            # Shared FastAPI backend
│   ├── app/
│   │   ├── models.py           # SQLAlchemy database schema
│   │   ├── schemas.py          # Pydantic schemas
│   │   ├── database.py         # SQLite / PostgreSQL connection
│   │   ├── seed_data.py        # Seed data for lines, stops, and active trips
│   │   ├── routers/            # routes.py, trips.py, location.py, auth.py
│   │   └── main.py             # FastAPI app with CORS & WebSocket support
│   ├── run_server.py           # Server runner script
│   └── requirements.txt
│
├── gradle/                    # Gradle wrapper & version catalog (libs.versions.toml)
├── build.gradle.kts
├── settings.gradle.kts        # Includes modules `:moviq-passenger` and `:moviq-driver`
├── local.properties           # SDK location & Maps API key
└── local.properties.example   # Configuration template
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio Flamingo** (or newer) with JDK 21
- **Python 3.10+** and **pip**
- **Android SDK** and **Google Maps API key** (add to `local.properties`)

### Backend Setup
```bash
cd moviq-backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python run_server.py
```
The server will be available at `http://localhost:8000` with Swagger UI at `/docs`.

### Android Apps
1. Open the project root in Android Studio.
2. Sync Gradle – the wrapper will automatically download the required distribution.
3. Select the run configuration for either `moviq-passenger` or `moviq-driver` and launch on an emulator or physical device.

---

## 📍 Real‑Time Location Flow
```mermaid
flowchart LR
    A[Driver Android Device] -->|POST /api/location/update| B[MoviQ Backend WebSocket Manager]
    B -->|Broadcast new coordinate payload| C[Passenger Android Device]
    C -->|WebSocket listener| D[Google Maps Bus Marker updates]
```

---

## 🤝 Contributing
We welcome contributions! Please follow these steps:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/awesome-feature`).
3. Ensure code formatting with `./gradlew ktlintFormat` for Android and `black` for Python.
4. Write unit tests where applicable.
5. Submit a pull request with a clear description of changes.

---

## 📄 License
This project is licensed under the **MIT License** – see the `LICENSE` file for details.

---

```
