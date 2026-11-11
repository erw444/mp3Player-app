# MP3 Player

A simple, offline Android music player. It reads the audio already on your device
through `MediaStore`, groups it by album, and plays it with `MediaPlayer`. No account,
no network access, no ads.

Originally based on the
[Kotlin Android Studio tutorial: the simple music player](https://codingwithsara.com/kotlin-android-studio-tutorial-the-simple-music-player/).

## Features

- Browse your music library as an alphabetical list of albums.
- Tap an album to see its songs, or tap its play icon to play the whole album.
- Tap a song to play just that song.
- Shuffle: the floating button plays every song on the device in random order.
- Player screen with album art, seek bar, elapsed/remaining time, play/pause,
  restart, and skip. Swipe left on the album art to skip to the next song.

## Requirements

| Item | Version |
| --- | --- |
| Android | 8.0 (API 26) or newer |
| JDK (to build) | 17 (Gradle 8.13 does not support JDK 25) |
| Android SDK | Platform 36 |

Gradle 8.13 and the Android Gradle Plugin 8.13 are fetched by the Gradle wrapper.
Dependency versions are managed in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Build and run

Point Gradle at your Android SDK (Android Studio does this automatically) by creating
`local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
```

Then, with JDK 17 on `JAVA_HOME`:

```sh
./gradlew assembleDebug        # build the debug APK
./gradlew installDebug         # install on a connected device/emulator
./gradlew testDebugUnitTest    # run unit tests
./gradlew lintDebug            # run Android lint
```

The debug APK is written to `app/build/outputs/apk/debug/`. Release builds
(`assembleRelease`) are minified with R8 and need a signing config of your own.

You can also open the project in Android Studio and press **Run**.

## Permissions

The app asks for access to audio files on first launch:

- `READ_MEDIA_AUDIO` on Android 13 (API 33) and newer
- `READ_EXTERNAL_STORAGE` on Android 12 and older

If permission is denied, the library stays empty. Only files that `MediaStore`
flags as music are listed, and `.wma` files are skipped.

## Project layout

```
app/src/main/java/com/erw/mp3player/
├── AlbumNavigationActivity.kt   # launcher: permission request, album list, shuffle
├── SongNavigationActivity.kt    # songs in one album
├── PlayMusicActivity.kt         # player: queue, MediaPlayer, progress updates
├── OnSwipeTouchListener.java    # swipe gesture helper
├── Insets.kt                    # edge-to-edge system bar padding
├── adapters/ holders/           # RecyclerView adapters and view holders
└── services/FileSystemScanService.kt  # MediaStore queries, MP3/Album models
```

Built with Kotlin, View Binding, AndroidX, Material 3, and coroutines.

## Known limitations

- Playback stops when the player screen is closed; there is no background service,
  notification, or lock-screen controls.
- The library is not refreshed while the app is open; relaunch after adding music.
- No playlists, search, or equalizer.

## License

No license has been specified for this project.

