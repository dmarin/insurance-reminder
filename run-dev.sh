#!/bin/bash

# Development script to run apps
echo "🔧 Insurance Reminder Development Tools"
echo ""
echo "Choose an option:"
echo "1) Run Android app"
echo "2) Run Web app (development server)"
echo "3) Run Desktop app"
echo "4) Check dependency updates"
echo "5) Update version catalog"
echo "6) Build all platforms"
echo ""
read -p "Enter choice [1-6]: " choice

case $choice in
    1)
        echo "📱 Starting Android app..."
        ./gradlew :androidApp:installDebug
        ;;
    2)
        echo "🌐 Starting Web development server..."
        ./gradlew :webApp:jsBrowserRun --continuous
        ;;
    3)
        echo "🖥️ Starting Desktop app..."
        ./gradlew :desktopApp:run
        ;;
    4)
        echo "🔍 Checking for dependency updates..."
        ./gradlew dependencyUpdates
        ;;
    5)
        echo "📦 Updating version catalog..."
        ./gradlew versionCatalogUpdate
        ;;
    6)
        echo "🚀 Building all platforms..."
        ./build-all.sh
        ;;
    *)
        echo "❌ Invalid option. Please choose 1-6."
        ;;
esac