# Weather Forecast App

## 🛠️ Tech Stack
| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Kotlin | 2.2.21 | Modern, concise, null-safe |
| **UI** | Jetpack Compose | 2025.11.00 | Declarative UI framework |
| **Architecture** | MVVM + Clean | - | Separation of concerns |
| **DI** | Hilt | 2.57.2 | Dependency injection |
| **Database** | Room | 2.8.3 | Local data persistence |
| **Networking** | Retrofit | 3.0.0 | REST API client |
| **Async** | Coroutines + Flow | 1.10.2 | Asynchronous operations |
| **Location** | Play Services | 21.3.0 | Location access |
| **Navigation** | Navigation Compose | 2.8.5 | Type-safe navigation |
| **Testing** | MockK + Turbine | 1.14.6 / 1.2.1 | Unit testing |

### Layer Responsibilities

#### **Data Layer**
- **Responsibility**: Data fetching and persistence
- **Components**:
    - `WeatherApiService`: Retrofit interface for OpenWeatherMap API
    - `WeatherDao`: Room DAO for local database operations
    - `WeatherRepositoryImpl`: Coordinates between API and database
- **Trade-off**: Slightly more complex but provides clear separation and testability

#### **Domain Layer**
- **Responsibility**: Business logic and models
- **Components**:
    - `WeatherForecast`: Domain model (independent of data sources)
    - `WeatherRepository`: Interface defining data operations
- **Design Decision**: Domain models are separate from data models to prevent external API changes from affecting business logic

#### **Presentation Layer**
- **Responsibility**: UI and user interactions
- **Components**:
    - `WeatherViewModel` & `WeatherDetailViewModel`: Manages UI state and business logic
    - Composable screens: Pure UI components
- **Pattern**: MVVM with StateFlow for reactive UI updates

## 🚀 Setup Instructions
1. Clone the repository
2. Open the project in Android Studio

    - Recommended: Android Studio Otter | 2025.2.1 
    - JDK 17
3. Add api key to local.properties file

   `WEATHER_API_KEY=your_api_key_here`
4. Sync gradle
5. Run the app

## 🐛 Known Issues
1. **Tokyo Fallback Not Obvious**: Users might not realize they're seeing Tokyo weather
    - **Fix**: Add clear UI indicator showing fallback location

