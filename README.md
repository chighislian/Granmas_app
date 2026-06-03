# Gran's Health 🌸

**Gran's Health** is a comforting, easy-to-use Android application designed specifically for elderly users to track their daily health metrics. It focuses on simplicity and accessibility, helping users manage chronic conditions like Type 2 Diabetes and Hypertension (high blood pressure).

## ✨ Features

-   **🩺 Blood Pressure Tracker**: Log systolic, diastolic, and pulse readings with a clear, large-text interface.
-   **🩸 Blood Sugar Monitor**: Record glucose levels and track whether they were taken before or after meals.
-   **📝 Daily Care Log**: A simple checklist for common symptoms (dizziness, fatigue, etc.) and activity logging to identify patterns in well-being.
-   **💊 Medication Management**: Keep track of daily medications and mark them as taken.
-   **🆘 Emergency Information**: A dedicated screen with quick access to vital medical info, doctor contacts, and emergency family numbers.
-   **📊 Health History**: Review past readings and logs to share with healthcare providers.
-   **🔮 Health Predictions**: Provides insights based on logged data trends.

## 🛠️ Tech Stack

-   **Language**: Kotlin
-   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Modern Android UI)
-   **Database**: [Room](https://developer.android.com/training/data-storage/room) (Local SQLite storage)
-   **Architecture**: MVVM (Model-View-ViewModel)
-   **Navigation**: Jetpack Compose Navigation
-   **Design**: Material 3 with a focus on high contrast and large touch targets for accessibility.

## 🚀 Getting Started

1.  Clone the repository.
2.  Open the project in **Android Studio Ladybug** (or newer).
3.  Sync Gradle and run the app on an emulator or a physical Android device (API 26+).

## 📂 Project Structure

-   `app/src/main/java/com/example/ui`: Contains all Compose screens, components, and the shared ViewModel.
-   `app/src/main/java/com/example/data`: Contains Room entities, DAOs, the database configuration, and the repository layer.
-   `app/src/main/java/com/example/MainActivity.kt`: The main entry point and navigation host.

## 🛠️ Development Process: Step-by-Step

This project was built following modern Android development best practices. Here is the step-by-step journey of how **Gran's Health** came to life:

1.  **Project Foundation & Theme**:
    *   Set up a new Android Studio project with **Jetpack Compose**.
    *   Defined a custom color palette in `Theme.kt` with high-contrast, soothing colors (greens and soft blues) to ensure accessibility for elderly eyes.

2.  **Defining the Data Model (Entities)**:
    *   Created `Entities.kt` in the `data` package.
    *   Defined data structures for everything Gran needs to track: `BloodPressureLog`, `SugarLog`, `SymptomLog`, `Medication`, and `EmergencyInfo`.

3.  **Local Persistence with Room**:
    *   Wrote **DAOs (Data Access Objects)** to handle SQLite queries for saving and retrieving health data.
    *   Set up the `AppDatabase` class to initialize the local Room database.

4.  **The Repository Pattern**:
    *   Implemented `HealthRepository.kt` to act as a clean API for the rest of the app to interact with the database, keeping the data logic separate from the UI.

5.  **State Management with ViewModel**:
    *   Developed `HealthViewModel.kt` to manage the app's state.
    *   Used `StateFlow` and `collectAsStateWithLifecycle` to ensure the UI updates instantly when new health data is logged.

6.  **Building Accessible UI Components**:
    *   Created reusable components like `BottomNavBar.kt` and `SymptomTactileCard.kt`.
    *   Focused on **large touch targets** and **clear icons** to make the app "Gran-friendly."

7.  **Designing the Screens**:
    *   **Dashboard**: A quick-glance view of today's status.
    *   **Logging Screens**: Simple forms for BP and Sugar with easy-to-tap buttons.
    *   **History & Charts**: Implemented custom `Canvas` drawings to show health trends over 7, 14, or 30 days.
    *   **Emergency Screen**: A high-priority area for critical information.

8.  **Navigation & Orchestration**:
    *   Connected all screens in `MainActivity.kt` using **Compose Navigation**.
    *   Implemented a `SplashScreen` for a polished app entry.

9.  **Testing & Refinement**:
    *   Performed iterative testing to ensure the data persists correctly and the navigation flows naturally.
    *   Fixed build configurations (like signing configs) to ensure a smooth development environment.

## 📄 License

This project is for personal use and health tracking assistance.
