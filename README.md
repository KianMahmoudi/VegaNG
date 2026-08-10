<div align="center">

# ⚡ VegaNG

**A fast & lightweight VPN client for Android**

VegaNG is a modern, high-performance VPN application that leverages the V2Ray core to deliver fast and reliable connections.

[![Android](https://img.shields.io/badge/Android-8.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange?style=for-the-badge)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

</div>

---
## ✨ Features

| ⚡ Performance | 🎨 Design |
|:---------------|:----------|
| 🕐 Single & batch latency testing | 🎯 Material 3 with Jetpack Compose |
| 📊 Auto-sort configs by lowest ping | 🌍 English & Farsi (RTL) |
| 🗑️ One-tap cleanup of non-working configs | 🚀 Clean empty-state UI |
| 🔒 Stable & secure V2Ray core | 🌓 Light & Dark theme support |

---
## 🖼️ Screenshots

<p align="center">
  <img src="screenshots/home_light.jpg" width="150" title="Home Light">
  <img src="screenshots/home_dark.jpg" width="150" title="Home Dark">
  <img src="screenshots/configs_light.jpg" width="150" title="Configs Light">
  <img src="screenshots/configs_dark.jpg" width="150" title="Configs Dark">
  <img src="screenshots/connected_light.jpg" width="150" title="Connected Light">
  <img src="screenshots/connected_dark.jpg" width="150" title="Connected Dark">
</p>

## 📥 Installation

Download the latest APK from [Releases](https://github.com/KianMahmoudi/VegaNG/releases).

> **Note:** Since the app is not published on Google Play, Android will prompt you to allow installation from unknown sources. Simply enable it for this install.

---
## 🚀 Quick Start

```
1. Get → Fetch configs from the server
2. Test → Measure ping for all configs
3. Sort → Fastest servers on top
4. Clean → Remove non-working configs
5. Tap a config → Connect
```

---
## 🛠️ Build from Source

**Prerequisites:** Android Studio · JDK 17+ · Android SDK 35

```bash
# Clone the repository
git clone https://github.com/KianMahmoudi/VegaNG.git

# Build a debug APK
./gradlew assembleDebug

# Build a release APK
./gradlew assembleRelease

# APK output:
app/build/outputs/apk/{debug,release}/app-{debug,release}.apk
```

## 🏗️ Architecture

Built with a clean **MVVM** architecture:

```
┌───────────────────────────────────┐
│            UI Layer               │
│   Jetpack Compose · Material 3    │
├───────────────────────────────────┤
│         ViewModel (Hilt)          │
│   UI state · events · coroutines  │
├───────────────────────────────────┤
│          Repository               │
│   ConfigRepository · VpnRepository │
├───────────────────────────────────┤
│           Data Layer              │
│   Room DB · Config Parser         │
├───────────────────────────────────┤
│            VPN Core               │
│      libv2ray · tun2socks         │
└───────────────────────────────────┘
```

## Tech stack
 - Language: Kotlin
 - UI: Jetpack Compose + Material 3
 - Architecture: MVVM + Repository pattern
 - DI: Hilt
 - Database: Room
 - VPN Core: libv2ray + tun2socks
 - Async: Coroutines + Flow
---
