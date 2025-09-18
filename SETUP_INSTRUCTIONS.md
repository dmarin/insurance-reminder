# Insurance Reminder App - Setup Instructions

## Prerequisites

1. **Android Studio** (Latest version with Kotlin Multiplatform support)
2. **JDK 17 or higher**
3. **Firebase Project** (requires your input)
4. **Android SDK** (API level 24 or higher)

## Firebase Setup (REQUIRES YOUR INPUT)

### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name: `insurance-reminder-app` (or your preferred name)
4. Enable Google Analytics (optional)
5. Complete project creation

### Step 2: Add Android App to Firebase Project
1. In Firebase Console, click "Add app" → Android icon
2. Use these details:
   - **Android package name**: `com.insurancereminder.android`
   - **App nickname**: `Insurance Reminder Android`
   - **Debug signing certificate SHA-1**: (optional for development)
3. Download `google-services.json`
4. **IMPORTANT**: Place `google-services.json` in `androidApp/` directory

### Step 3: Enable Firebase Services
1. **Firestore Database**:
   - In Firebase Console → Build → Firestore Database
   - Click "Create database"
   - Choose "Start in test mode" for development
   - Select a location (choose closest to your users)
   - Click "Done"

2. **Authentication**:
   - In Firebase Console → Build → Authentication
   - Click "Get started"
   - Go to "Sign-in method" tab
   - Enable "Email/Password" provider
   - Click "Save"

3. **Cloud Storage**:
   - In Firebase Console → Build → Storage
   - Click "Get started"
   - Choose "Start in test mode"
   - Select same location as Firestore
   - Click "Done"

### Step 4: Configure Security Rules

**Firestore Security Rules**:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Insurance policies - users can access their own or shared ones
    match /insurances/{document} {
      allow read, write: if request.auth != null &&
        (resource.data.userId == request.auth.uid ||
         resource.data.sharedWithUserId == request.auth.uid);
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
    }
  }
}
```

**Storage Security Rules**:
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /policies/{userId}/{insuranceId}/{fileName} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Project Setup

### Step 1: Open Project in IntelliJ/Android Studio
1. Open IntelliJ IDEA or Android Studio
2. Select "Open an existing project"
3. Navigate to this project directory
4. Select the root directory containing `build.gradle.kts`
5. Wait for Gradle sync to complete

### Step 2: Verify Configuration
1. Ensure `google-services.json` is in `androidApp/` directory
2. Sync Gradle files
3. Check that no build errors occur

### Step 3: Build and Run
1. Select "androidApp" configuration
2. Choose an Android device or emulator (API 24+)
3. Click "Run" or use `./gradlew :androidApp:assembleDebug`

⚠️ **Firebase Storage Note**: If Firebase Storage fails to enable (common on Spark plan), the app will still work perfectly! See `FIREBASE_STORAGE_SETUP.md` for details. All features work except file uploads, which use a mock implementation.

## Development Features

### Current Functionality

**Core Features**:
- ✅ Add/edit/delete insurance policies with expiry dates
- ✅ Upload PDF policy documents (Premium only)
- ✅ Price tracking and comparison service integration
- ✅ Company name and policy number tracking
- ✅ Color-coded warnings for expiring/expired policies
- ✅ Background notifications for expiring policies

**Authentication & Subscription System**:
- ✅ Email/password authentication
- ✅ Guest mode (local storage, max 5 policies)
- ✅ Premium subscription (unlimited cloud storage)
- ✅ Partner sharing for premium users

**Storage Options**:
- ✅ Local SQLite database (free tier)
- ✅ Firebase Firestore integration (premium tier)
- ✅ Cloud file storage for PDFs (premium tier)
- ✅ Data export/import functionality

**Insurance Comparison**:
- ✅ Integration with Comparator.es, Rastreator.com, Acierto.com, Kelisto.es
- ✅ Direct links to comparison websites based on insurance type

### Insurance Types Supported
- Auto Insurance
- Home Insurance
- Health Insurance
- Life Insurance
- Travel Insurance
- Business Insurance
- Pet Insurance
- Dental Insurance
- Vision Insurance
- Other

## File Structure
```
InsuranceReminder/
├── build.gradle.kts                 # Root build configuration
├── settings.gradle.kts              # Project settings
├── shared/                          # Shared Kotlin Multiplatform module
│   ├── src/commonMain/kotlin/
│   │   └── com/insurancereminder/shared/
│   │       ├── model/               # Data models
│   │       ├── repository/          # Firebase repository
│   │       └── utils/               # Utility functions
│   └── build.gradle.kts
├── androidApp/                      # Android application
│   ├── src/main/
│   │   ├── kotlin/                  # Android-specific code
│   │   ├── res/                     # Android resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── google-services.json         # ⚠️  REQUIRED: Firebase config
└── SETUP_INSTRUCTIONS.md           # This file
```

## Platform-Specific Setup

### iOS Setup (Xcode Required)

#### Prerequisites
1. **macOS** with Xcode 14+ installed
2. **iOS Simulator** or physical iOS device
3. **Apple Developer Account** (for device testing/distribution)

#### Setup Steps
1. **Generate iOS Framework**:
   ```bash
   ./gradlew :shared:linkDebugFrameworkIosX64
   ./gradlew :shared:linkDebugFrameworkIosArm64
   ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
   ```

2. **Open iOS Project**:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```

3. **Xcode Configuration**:
   - Select "iosApp" scheme
   - Choose target device (Simulator or physical device)
   - Build and run (⌘+R)

4. **Firebase iOS Setup**:
   - In Firebase Console, add iOS app
   - Bundle ID: `com.insurancereminder.ios`
   - Download `GoogleService-Info.plist`
   - Drag into Xcode project (iosApp target)
   - Enable required Firebase features

#### iOS Build Commands
```bash
# Build shared framework for iOS
./gradlew :shared:linkDebugFrameworkIosX64

# For Apple Silicon Macs (M1/M2)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# For physical devices
./gradlew :shared:linkDebugFrameworkIosArm64
```

### Web Setup (Progressive Web App)

#### Prerequisites
1. **Node.js** (optional, for development server)
2. **Modern web browser** (Chrome, Firefox, Safari, Edge)

#### Setup Steps
1. **Build Web Application**:
   ```bash
   ./gradlew :webApp:jsBrowserDevelopmentRun
   ```

2. **Development Server**:
   - Automatically opens at `http://localhost:8080`
   - Hot reload enabled for development
   - Console shows build progress

3. **Production Build**:
   ```bash
   ./gradlew :webApp:jsBrowserDistribution
   ```
   - Output in `webApp/build/distributions/`
   - Ready for web server deployment

4. **Firebase Web Setup**:
   - In Firebase Console, add Web app
   - App nickname: `Insurance Reminder Web`
   - Copy Firebase config object
   - Update `shared/src/jsMain/kotlin/Platform.kt` with config

#### Web Deployment
```bash
# Build for production
./gradlew :webApp:jsBrowserDistribution

# Serve locally (Python example)
cd webApp/build/distributions
python3 -m http.server 8000

# Access at http://localhost:8000
```

#### Progressive Web App Features
- **Offline Support**: Local caching with service workers
- **Install Prompt**: "Add to Home Screen" functionality
- **Responsive Design**: Adapts to mobile, tablet, desktop
- **Push Notifications**: Firebase messaging integration

### Desktop Setup (Optional)

#### Prerequisites
1. **JDK 17+** installed
2. **Gradle** (included via wrapper)

#### Setup Steps
```bash
# Run desktop application
./gradlew :desktopApp:run

# Create distributable
./gradlew :desktopApp:createDistributable
```

## Next Steps for Production

1. **Security**: Update Firestore security rules for production
2. **Authentication**: Add Firebase Authentication
3. **Testing**: Implement unit and integration tests
4. **CI/CD**: Set up automated builds and deployment
5. **Push Notifications**: Implement FCM for remote notifications
6. **App Store Distribution**: iOS App Store and Google Play Store

## Troubleshooting

### Common Issues

#### Android
1. **Missing google-services.json**: Ensure file is in `androidApp/` directory
2. **Build failures**: Run `./gradlew clean` then rebuild
3. **Firebase connection issues**: Verify package name matches Firebase configuration
4. **Notification permissions**: On Android 13+, manually grant notification permission

#### iOS
1. **Framework not found**: Run `./gradlew :shared:linkDebugFrameworkIosX64` first
2. **Missing GoogleService-Info.plist**: Download from Firebase Console for iOS app
3. **Xcode build errors**: Ensure bundle ID matches Firebase configuration
4. **Simulator issues**: Use correct architecture (x64 for Intel, arm64 for Apple Silicon)

#### Web
1. **CORS issues**: Use development server, not file:// protocol
2. **Build failures**: Clear browser cache and rebuild
3. **Firebase config**: Ensure web config is properly set in JavaScript
4. **Module not found**: Check that all imports are correct for web target

### Build Commands

#### All Platforms
```bash
# Clean all builds
./gradlew clean

# Build all targets
./gradlew build
```

#### Android
```bash
# Build Android app
./gradlew :androidApp:assembleDebug

# Install on device
./gradlew :androidApp:installDebug

# Run tests
./gradlew :androidApp:testDebugUnitTest
```

#### iOS
```bash
# Build shared framework for iOS
./gradlew :shared:linkDebugFrameworkIosX64
./gradlew :shared:linkDebugFrameworkIosArm64
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Then open Xcode project
open iosApp/iosApp.xcodeproj
```

#### Web
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
# Run desktop app
./gradlew :desktopApp:run

# Create distributable
./gradlew :desktopApp:createDistributable
```

## Support
For issues with Firebase setup, consult the [Firebase Documentation](https://firebase.google.com/docs).
For Kotlin Multiplatform issues, check [KMP Documentation](https://kotlinlang.org/docs/multiplatform.html).