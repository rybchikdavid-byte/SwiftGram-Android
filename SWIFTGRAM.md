# SwiftGram Android

SwiftGram Android is a messenger built on the official
[Telegram Android](https://github.com/TelegramMessenger/Telegram-Android) engine,
rebranded and extended with the features of SwiftGram.

The app talks to Telegram through an independent `api_id`, uses its own name,
icon and package (`app.swiftgram.android`), and is **not** affiliated with
Telegram Messenger LLP.

## Features

- Up to **10 accounts** in one app
- **Sponsored messages disabled**
- **Notification filters**:
  - silence mentions and replies in group chats
  - hide notifications for messages containing your own keywords
- Own package name, icon and signature, so it installs next to Telegram

The feature settings live in **Settings → SwiftGram**.

## Roadmap

SwiftGram for iOS is written in Swift and UIKit, so its code cannot be compiled
for Android. Instead, each SwiftGram feature is rebuilt on top of the official
Telegram Android code. More features (app icon picker, chat filters, message
translation, etc.) will follow in later releases.

## Building

Requirements: JDK 21, Android SDK 36, NDK `27.2.12479018`, CMake 3.22.1.

The release build is automated in `.github/workflows/build.yml` and expects
these repository secrets:

| Secret | Meaning |
| --- | --- |
| `API_ID` | Telegram `api_id` |
| `API_HASH` | Telegram `api_hash` |
| `KEYSTORE_BASE64` | your signing keystore, base64 |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | key alias |
| `KEY_PASSWORD` | key password |

`google-services.json` in this fork is a placeholder for the
`swiftgram-android` project. Replace it with a real Firebase project to get
FCM push notifications; without one, notifications may be delayed when the app
is force-stopped.

Build locally with:

```
./gradlew :TMessagesProj_App:assembleAfatRelease
```

The APK appears in `TMessagesProj_App/build/outputs/apk/afat/release/app.apk`.

## License

This project is based on Telegram Android and is distributed under the same
**GNU General Public License v2 or later**. Full corresponding source code is
published in this repository.
