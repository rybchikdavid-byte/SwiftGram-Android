# SwiftGram Android

SwiftGram Android is a messenger built on the official
[Telegram Android](https://github.com/TelegramMessenger/Telegram-Android) engine,
rebranded and extended with the features of SwiftGram.

The app talks to Telegram through an independent `api_id`, uses its own name,
icon and package (`app.swiftgram.android`), and is **not** affiliated with
Telegram Messenger LLP.

## Features

SwiftGram Android is a port of the real
[SwiftGram for iOS](https://github.com/Swiftgram/Telegram-iOS): the feature
names and behaviour are taken from its open source, then rebuilt on top of the
official Telegram Android engine. Swift code itself cannot run on Android.

Ported in 1.0.2:

- **Chat list** — message filter by keywords (`Settings.MessageFilter`)
- **Profiles** — profile ID row with tap-to-copy (`Settings.ShowProfileID`)
- **Stories** — hide the stories bar (`Settings.Stories.Hide`)
- **Notifications** — hide the account name (`Settings.HideAccountInNotifications`),
  silence mentions and replies
- **Messages** — seconds in message time (`Settings.secondsInMessages`),
  hide reactions under messages (`Settings.HideReactions`)
- **Calls** — confirmation before a call (`Settings.CallConfirmation`)
- **Account** — hide the phone number in the settings UI
  (`Settings.HidePhoneInSettingsUI`)
- **Other** — sponsored messages disabled

The feature settings live in **Settings → SwiftGram**, grouped into the same
sections as the iOS app: Chat List, Profiles, Stories, Notifications, Messages,
Calls, Account, Other.

## Roadmap

More features from the SwiftGram iOS settings will follow: compact chat list,
small reactions, custom folder tabs, translation backend, voice-to-text
backend, upload/download boost and the context-menu editor.

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
