#!/bin/bash

# Build script for all platforms
echo "🚀 Building Insurance Reminder for all platforms..."

# Set script to exit on error
set -e

echo "📱 Building Android app..."
./gradlew :androidApp:assembleDebug

echo "🌐 Building Web app..."
./gradlew :webApp:jsBrowserDistribution

echo "🖥️ Building Desktop app..."
./gradlew :desktopApp:packageDistributionForCurrentOS

echo "📦 Building iOS framework..."
./gradlew :shared:linkDebugFrameworkIosX64

echo "✅ All builds completed successfully!"
echo ""
echo "📂 Build outputs:"
echo "   Android APK: androidApp/build/outputs/apk/debug/"
echo "   Web app: webApp/build/distributions/"
echo "   Desktop app: desktopApp/build/compose/binaries/main/app/"
echo "   iOS framework: shared/build/bin/iosX64/debugFramework/"
echo ""
echo "🔧 To run specific apps:"
echo "   Android: ./gradlew :androidApp:installDebug"
echo "   Web: ./gradlew :webApp:jsBrowserRun"
echo "   Desktop: ./gradlew :desktopApp:run"
echo "   iOS: Open iosApp/iosApp.xcodeproj in Xcode"