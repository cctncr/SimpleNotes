# SimpleNotes

> **⚠️ WORK IN PROGRESS**: This project is still under active development. Features may be incomplete or subject to change.

A clean, minimalist note-taking application for Android that demonstrates modern Android development practices.

## Project Overview

SimpleNotes is a straightforward note-taking app that allows users to create, view, delete, and reorder notes. The project serves as a practical implementation of MVVM architecture with Dependency Injection using Hilt, Room database, Kotlin Coroutines, and other modern Android development components.

## Features

### Implemented Features
- Create new notes with title and content
- View list of all notes
- Delete notes with swipe gestures
- Reorder notes with drag and drop
- Persistent storage with Room database

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room
- **Asynchronous Programming**: Kotlin Coroutines
- **UI Components**: RecyclerView, ConstraintLayout, Material Components
- **Android Jetpack**: ViewModel, LiveData, ViewBinding

## Architecture

SimpleNotes follows the MVVM (Model-View-ViewModel) architecture pattern with a clean separation of concerns:

### Model Layer
- **Entity**: Defines the data structure for notes
- **DAO**: Provides methods to interact with the database
- **Database**: Configures the Room database
- **Repository**: Acts as a mediator between data sources and ViewModels

### View Layer
- **Activities**: Display UI and handle user interactions
- **Adapters**: Connect data to RecyclerViews

### ViewModel Layer
- **ViewModels**: Hold UI-related data and survive configuration changes
- **LiveData**: Observe data changes and update the UI accordingly

### Dependency Injection Layer
- **Hilt Modules**: Provide instances of dependencies
- **Component Annotations**: Define scopes and dependencies

## Setup Instructions

1. Clone the repository:
   ```
   git clone https://github.com/cctncr/SimpleNotes.git
   ```

2. Open the project in Android Studio (Hedgehog or newer recommended)

3. Sync the project with Gradle files

4. Run the app on an emulator or physical device

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/simplenotes/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── dao/          # Data Access Objects
│   │   │   │   │   ├── database/     # Room Database
│   │   │   │   │   └── entity/       # Data Models
│   │   │   │   └── repository/       # Data Repositories
│   │   │   ├── di/                   # Dependency Injection
│   │   │   ├── ui/                   # UI Components
│   │   │   │   ├── addnote/          # Add Note Screen
│   │   │   │   └── main/             # Main Screen
│   │   │   ├── viewmodel/            # ViewModels
│   │   │   └── SimpleNotesApplication.kt
│   │   └── res/                      # Resources
│   ├── androidTest/                  # Instrumentation Tests
│   └── test/                         # Unit Tests
└── build.gradle.kts                  # App level Gradle
```

## Screenshots

_Screenshots will be added when the UI is finalized._

## Development Roadmap

- ✅ Initial project setup
- ✅ Implement MVVM architecture
- ✅ Add Room database integration
- ✅ Implement note creation and listing
- ✅ Add swipe to delete functionality
- ✅ Add drag and drop reordering
- ✅ Implement Hilt for dependency injection
- ⬜ Add note editing functionality
- ⬜ Implement search feature
- ⬜ Add unit and UI tests
- ⬜ Implement dark theme support
- ⬜ Add data backup feature

## Contributing

As this project is still in development, contributions are welcome. Please feel free to submit a pull request.