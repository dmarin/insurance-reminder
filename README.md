# Insurance Reminder App

A Kotlin Multiplatform application that helps users track insurance expiry dates and sends timely reminders across all their devices.

## Features

### 🌍 Multiplatform Support
- 📱 **Android**: Native Android app with Jetpack Compose
- 🍎 **iOS**: Native iOS app with SwiftUI (framework ready)
- 🌐 **Web**: Progressive Web App with Compose for Web
- 🖥️ **Desktop**: Native desktop app for Windows, macOS, Linux

### 🌐 Internationalization
- 🇪🇸 **Spanish**: Complete Spanish translations
- 🇬🇧 **English**: Full English support
- 🔄 **Auto-detection**: Automatic language detection based on system locale

### 🆓 Free Tier (Guest Mode)
- 📱 **Local Storage**: SQLite database with up to 5 policies
- 📅 **Smart Reminders**: Configurable notification system
- 🔍 **Price Comparison**: Links to major Spanish comparison sites
- 🎨 **Modern UI**: Material Design 3 across all platforms

### 💎 Premium Tier
- ☁️ **Cloud Sync**: Firebase Firestore integration across devices
- 📄 **File Uploads**: Store PDF policy documents in cloud
- 👥 **Partner Sharing**: Share policies with one partner
- 🔒 **Authentication**: Secure email/password login
- 📊 **Advanced Features**: Unlimited policies, export/import data
- 🔔 **Enhanced Notifications**: Cross-device synchronized reminders

### 🔍 Insurance Comparison Integration
- **Comparator.es**: Auto, Home, Health insurance
- **Rastreator.com**: Auto, Home, Health, Life insurance
- **Acierto.com**: Auto, Home, Health, Life insurance
- **Kelisto.es**: Auto, Home, Health insurance

## Supported Insurance Types

- Auto Insurance
- Home Insurance
- Health Insurance
- Life Insurance
- Travel Insurance
- Business Insurance
- Pet Insurance
- Dental Insurance
- Vision Insurance
- Other (custom)

## Screenshots

*Screenshots will be available after Firebase setup and first run*

## Quick Start

### Prerequisites by Platform

#### All Platforms
- **JDK 17+** and **Android Studio** or **IntelliJ IDEA**
- **Firebase account** (required for cloud features)

#### Android
- **Android SDK** (API 24+)
- **Android device/emulator**

#### iOS
- **macOS** with **Xcode 14+**
- **iOS Simulator** or physical iOS device
- **Apple Developer Account** (for device testing)

#### Web
- **Modern web browser** (Chrome, Firefox, Safari, Edge)
- **Node.js** (optional, for advanced development)

### Setup Steps
1. **Clone/Download**: Get this project on your machine
2. **Firebase**: Follow instructions in `SETUP_INSTRUCTIONS.md`
3. **Platform**: Choose your target platform from the commands below
4. **Build**: Use the platform-specific commands to build and run

## Project Structure

```
InsuranceReminderApp/
├── shared/           # Kotlin Multiplatform shared code
├── androidApp/       # Android app (Jetpack Compose)
├── iosApp/           # iOS app (SwiftUI)
├── webApp/           # Web app (Compose for Web)
├── desktopApp/       # Desktop app (Compose Desktop)
├── gradle/           # Version catalog and dependencies
├── build.gradle.kts  # Root build configuration
├── build-all.sh      # Build script for all platforms
├── run-dev.sh        # Development helper script
└── *.md             # Documentation files
```

## Tech Stack

### Core Technologies
- **Kotlin Multiplatform**: Shared business logic across all platforms
- **Jetpack Compose**: Modern declarative UI (Android, Desktop, Web)
- **SwiftUI**: Native iOS user interface
- **Material Design 3**: Consistent design system across platforms

### Platform-Specific
- **Android**: Jetpack Compose + Android SDK
- **iOS**: SwiftUI + iOS SDK (framework integration)
- **Web**: Compose for Web + Progressive Web App features
- **Desktop**: Compose Desktop + JVM

### Backend & Storage
- **Firebase Firestore**: NoSQL cloud database (Premium tier)
- **Firebase Authentication**: Secure user management
- **Firebase Storage**: Cloud file storage for PDFs
- **SQLDelight**: Local SQLite database (Free tier, all platforms)

### Additional Libraries
- **Kotlin Coroutines**: Asynchronous programming
- **Kotlin Serialization**: JSON handling
- **WorkManager**: Background task scheduling (Android)
- **Kotlinx DateTime**: Date/time operations

### Development Tools
- **Version Catalog**: Centralized dependency management
- **Ben Manes Versions Plugin**: Dependency update checking
- **Build Scripts**: Automated building for all platforms

## Getting Started

### Quick Development
```bash
# Make scripts executable
chmod +x build-all.sh run-dev.sh

# Run development helper
./run-dev.sh

# Or build all platforms
./build-all.sh
```

### Platform-Specific Commands

#### Android
```bash
# Build and install Android app
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# Run Android tests
./gradlew :androidApp:testDebugUnitTest
```

#### iOS (requires macOS + Xcode)
```bash
# Build iOS framework
./gradlew :shared:linkDebugFrameworkIosX64
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64  # Apple Silicon Macs
./gradlew :shared:linkDebugFrameworkIosArm64           # Physical devices

# Open in Xcode
open iosApp/iosApp.xcodeproj
```

#### Web (Progressive Web App)
```bash
# Development server with hot reload
./gradlew :webApp:jsBrowserDevelopmentRun

# Production build
./gradlew :webApp:jsBrowserDistribution

# Run web tests
./gradlew :webApp:jsTest
```

#### Desktop
```bash
# Run desktop application
./gradlew :desktopApp:run

# Create distributable package
./gradlew :desktopApp:createDistributable
```

#### Maintenance
```bash
# Clean all builds
./gradlew clean

# Check for dependency updates
./gradlew versionCatalogUpdate

# Build all platforms
./gradlew build
```

## Quick Reference

### First Time Setup
```bash
# 1. Open project in Android Studio/IntelliJ IDEA
# 2. Add google-services.json to androidApp/ (from Firebase Console)
# 3. Choose your platform and run:

# Android (easiest to start with)
./gradlew :androidApp:assembleDebug

# Web (runs in browser)
./gradlew :webApp:jsBrowserDevelopmentRun

# iOS (macOS + Xcode required)
./gradlew :shared:linkDebugFrameworkIosX64
open iosApp/iosApp.xcodeproj

# Desktop (cross-platform)
./gradlew :desktopApp:run
```

### Common Issues
- **Android**: Ensure `google-services.json` is in `androidApp/` directory
- **iOS**: Run framework build command before opening Xcode
- **Web**: Use development server, don't open HTML files directly
- **All**: Run `./gradlew clean` if builds fail

⚠️ **Important**: This app requires Firebase configuration before it can run. See `SETUP_INSTRUCTIONS.md` for complete setup instructions.

## License

This project is created for educational and personal use.