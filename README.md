
# Basic Exo Player app

This project is a small Android application designed to demonstrate a media player queue feature. It provides basic queue management using **StateFlow** for reactive UI updates.

## Features
- **Display the current media queue** alongside the player view.
- **Playback chaining**: Automatically play the next media in the queue when the current media ends.
- **Media selection**: Start playback of any media in the queue by clicking on it.
- **Queue management**: Add or remove media from the current queue.

## Architecture and Implementation
The project follows a reactive UI architecture using **Jetpack Compose** for building the interface and **StateFlow** for managing state in the `PlayerViewModel`.

### `PlayerViewModel`
The `PlayerViewModel` is responsible for managing the UI state and building it from two underlying state flows:

```kotlin
// Holds the index of the currently selected track
val selectedTrackFlow = MutableStateFlow<Int?>(null)

// Holds the list of track IDs in the queue
val playListFlow = MutableStateFlow<List<Int>>(emptyList())
```

The UI observes a combined state flow built from `selectedTrackFlow` and `playListFlow` to update the media queue accordingly.

### Adding Tracks
This demo works with local audio files, I used the regular android picker, tap the FAB to open it. I placed a sample mp3 file at the root of the project for this purpose.
I know that a nice UI was out of the scope of the exercise, but MetaDataReader is there to access the filenames so tracks look better in the queue
Aside from this small deviation, you'll notice that I took the ugly UI assignment very seriously :)


### Player Playback State Tracking
A small wrapper around `Player.Listener` exposes a new flow:

```kotlin
val playerPlaybackStateFlow = playbackObserver.playerPlaybackStateFlow
```

This flow tracks the player’s state and helps detect when the current song reaches its end using `onPlaybackStateChanged` with `STATE_ENDED`. While this implementation has some **side effects** (e.g., `clearMedias()` can trigger this event), it worked without introducing any bugs in this exercise.

### Unit Tests
The project includes a **comprehensive suite of unit tests** for the `PlayerViewModel` to ensure the correctness of state management and playback behavior.

### Limitation
Beside the usage of STATE_ENDED, the app don't support config change, process kill etc.. though it could be easily done by saving the Uris in SavedStateHandle and building the track list flow around it

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. drag & drop the sample mp3 to your emulator
4. Build and run the project
5. Run tests using `./gradlew test`
