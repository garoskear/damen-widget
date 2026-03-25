# RingerWidget 🔔

> **A pet project born from a simple idea — built with the help of Claude (AI assistant by Anthropic).**
> This app is a real-world example of how AI tools can empower people without a software development or app-dev background to bring their ideas to life as fully functional, working apps. The concept was ideated by me; the code was executed using Claude Code.

---

## What is RingerWidget?

RingerWidget is a lightweight Android home screen widget app that gives you instant, one-tap control over your phone's audio — without unlocking your phone or diving into Settings.

It ships two widgets you can add to your home screen:

| Widget | Size | What it does |
|---|---|---|
| **Ringer Toggle** | 1×1 | Tap to switch between Ring 🔔 and Vibrate 📳 mode |
| **Volume Slider** | 4×1 | Tap any of the 10 bars to set media volume instantly |

---

## How it works

### Ringer Toggle Widget
- The widget displays a **bell icon** (blue) when the phone is in Ring mode, and a **vibrate icon** (orange) when in Vibrate mode.
- Tapping the widget launches a transparent background activity (`ToggleActivity`) that flips the ringer mode using Android's `AudioManager`.
- The new mode is saved to `SharedPreferences` immediately so the widget re-renders with the correct state without any lag.
- A subtle haptic click confirms the action.

### Volume Slider Widget
- Renders a row of **10 tappable boxes** representing volume levels 0–10.
- Active (filled) boxes are shown in blue; inactive ones in grey, giving a visual bar-graph feel.
- Tapping any box fires a transparent `SetVolumeActivity` that maps the tapped level to the device's actual media volume scale and sets it via `AudioManager`.
- Haptic feedback confirms each tap.

### App Architecture
```
MainActivity         ← Launcher screen; checks & requests DnD permission
RingerWidget         ← Glance AppWidget for the 1×1 ringer toggle
VolumeWidget         ← Glance AppWidget for the 4×1 volume slider
ToggleActivity       ← Transparent trampoline: toggles ringer mode, triggers widget refresh
SetVolumeActivity    ← Transparent trampoline: sets media volume, triggers widget refresh
RingerWidgetReceiver ← AppWidgetProvider wiring for RingerWidget
VolumeWidgetReceiver ← AppWidgetProvider wiring for VolumeWidget
```

The app uses **Jetpack Glance** for composable widget UIs, which means the widget layout is written in a Compose-like DSL rather than traditional RemoteViews XML.

---

## Permissions

| Permission | Why it's needed |
|---|---|
| `ACCESS_NOTIFICATION_POLICY` | Required to change the ringer mode (Ring / Vibrate / Silent) on Android 6.0+ |
| `VIBRATE` | Used for haptic feedback when the widget is tapped |

On first launch, the app will prompt you to grant **Do Not Disturb access** from system settings if it hasn't been granted yet.

---

## How to add the widgets to your home screen

1. Install the app and open it once to grant the required permission.
2. Long-press an empty area on your home screen.
3. Tap **Widgets**.
4. Find **Ringer Widget** in the list and choose:
   - **Ringer Toggle** (1×1) — a small square toggle
   - **Volume Slider** (4×1) — a wide bar for volume control
5. Drag your chosen widget onto the home screen and release.

---

## Tech Stack

- **Language:** Kotlin
- **UI (Widgets):** Jetpack Glance (Compose-based AppWidget API)
- **UI (App):** Jetpack Compose + Material 3
- **Min SDK:** Android 8.0 (API 26)
- **Build System:** Gradle with Kotlin DSL

---

## Building from source

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Let Gradle sync and download dependencies.
4. Run on a physical device or emulator (API 26+).

```bash
git clone https://github.com/YOUR_USERNAME/RingerWidget.git
cd RingerWidget
# Open in Android Studio, or build from CLI:
./gradlew assembleDebug
```

---

## Motivation & Story

I'm not a developer by background, but I had a simple, recurring frustration: switching my phone to vibrate (and back) always took too many taps. I knew exactly what I wanted — a single widget button on my home screen — but I didn't know how to build it.

This project is proof that you don't need to be an engineer to ship a working app in 2025. Using **Claude Code**, I was able to describe what I wanted in plain English, iterate on the design, debug issues, and end up with a fully functional Android app. AI tools like Claude aren't replacing developers — they're giving everyone else a seat at the table.

---

## License

MIT — feel free to use, modify, and distribute.
