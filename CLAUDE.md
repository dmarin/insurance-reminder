# Claude Development Notes

## Project Overview
This is a **Kotlin Multiplatform** insurance reminder app with support for:
- **Android** (Compose UI)
- **iOS** (SwiftUI)
- **Web** (Compose for Web)

## Critical Development Rules

### ⚠️ MULTIPLATFORM CONSISTENCY
**ALWAYS implement changes across ALL platforms (Android, iOS, Web) unless explicitly told otherwise.**

When making any change:
1. ✅ Android implementation
2. ✅ iOS implementation
3. ✅ Web implementation
4. ✅ Shared business logic (if applicable)

### Key Technologies
- **Shared**: Kotlin Multiplatform, Firebase, SQLite
- **Android**: Jetpack Compose, Material 3
- **iOS**: SwiftUI, Material 3 design system
- **Web**: Compose for Web, CSS custom properties

### Testing Commands
```bash
# Android compilation
./gradlew :androidApp:compileDebugKotlin --no-daemon -q

# Shared module
./gradlew :shared:check --no-daemon -q

# iOS: Build in Xcode or from iOS simulator
```

### Architecture Notes
- Uses `SmartInsuranceRepository` that switches between cloud (authenticated) and local storage (guest mode)
- Material 3 blue theme across all platforms
- Edge-to-edge design with solid blue app bars
- Shared business logic in `shared/` module

## Current Features
- ✅ Cross-platform authentication
- ✅ Insurance CRUD operations
- ✅ Swipe-to-delete (Android/iOS) and click-to-delete (Web)
- ✅ Dark theme support
- ✅ Local and cloud data sync
- ✅ Material 3 design system

## Remember
Every feature, bug fix, or UI change must work consistently across Android, iOS, and Web platforms to maintain the multiplatform promise.