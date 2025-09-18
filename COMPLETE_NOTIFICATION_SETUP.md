# Complete Push Notification Setup Guide

## Overview
This guide provides everything you need to implement smart push notifications for insurance expiry reminders using Firebase's free tier.

## 📋 Step-by-Step Implementation

### Phase 1: Firebase Setup (15 minutes)

1. **Enable Required Services in Firebase Console**
   ```
   - Go to Firebase Console → Your Project
   - Enable Cloud Functions
   - Enable Cloud Messaging (FCM)
   - Enable Cloud Scheduler
   ```

2. **Install Firebase CLI**
   ```bash
   npm install -g firebase-tools
   firebase login
   cd /path/to/your/project
   firebase init functions
   ```

3. **Choose Options**
   ```
   - Language: JavaScript
   - ESLint: Yes
   - Install dependencies: Yes
   ```

### Phase 2: Cloud Function Implementation (30 minutes)

1. **Replace functions/index.js with the smart notification code** (from SMART_NOTIFICATION_SYSTEM.md)

2. **Install additional dependencies**
   ```bash
   cd functions
   npm install firebase-admin firebase-functions
   ```

3. **Deploy the function**
   ```bash
   firebase deploy --only functions
   ```

### Phase 3: Android Integration (45 minutes)

1. **Add FCM dependencies to app/build.gradle**
   ```kotlin
   implementation platform('com.google.firebase:firebase-bom:32.7.0')
   implementation 'com.google.firebase:firebase-messaging'
   ```

2. **Create FirebaseMessagingService** (see SMART_NOTIFICATION_SYSTEM.md for complete code)

3. **Add to AndroidManifest.xml**
   ```xml
   <service
       android:name=".InsuranceNotificationService"
       android:exported="false">
       <intent-filter>
           <action android:name="com.google.firebase.MESSAGING_EVENT" />
       </intent-filter>
   </service>
   ```

4. **Update User Registration** to include timezone and FCM token

### Phase 4: iOS Integration (45 minutes)

1. **Add Firebase to iOS project**
   ```swift
   // In Xcode: File → Add Package Dependencies
   // Add: https://github.com/firebase/firebase-ios-sdk
   ```

2. **Update AppDelegate** (see SMART_NOTIFICATION_SYSTEM.md for complete code)

3. **Request notification permissions**

4. **Handle FCM token registration**

### Phase 5: Database Schema Updates (15 minutes)

1. **Update User model** to include:
   ```kotlin
   data class User(
       // existing fields...
       val fcmToken: String? = null,
       val timezone: String = TimeZone.currentSystemDefault().id,
       val notificationsEnabled: Boolean = true,
       val preferredNotificationTime: Int = 9
   )
   ```

2. **Update Firestore rules** to allow notification_log collection:
   ```javascript
   match /notification_log/{notificationId} {
     allow read, write: if false; // Only functions can write
   }
   ```

### Phase 6: Testing (30 minutes)

1. **Test manual notifications**
   ```bash
   # Use Firebase Console → Cloud Messaging → Send test message
   ```

2. **Test scheduled function**
   ```bash
   # Use Firebase Console → Functions → Logs
   # Verify function runs hourly
   ```

3. **Test with sample data**
   ```kotlin
   // Add test insurance with expiry date = tomorrow
   // Verify notification arrives at correct local time
   ```

## 🔧 Configuration Files

### Firebase Functions Package.json
```json
{
  "name": "functions",
  "description": "Cloud Functions for Firebase",
  "scripts": {
    "serve": "firebase emulators:start --only functions",
    "shell": "firebase functions:shell",
    "start": "npm run shell",
    "deploy": "firebase deploy --only functions",
    "logs": "firebase functions:log"
  },
  "engines": {
    "node": "18"
  },
  "main": "index.js",
  "dependencies": {
    "firebase-admin": "^11.8.0",
    "firebase-functions": "^4.3.1"
  }
}
```

### Notification Icons (Android)
Create these icons in `app/src/main/res/drawable/`:
- `ic_notification.xml` - Default notification icon
- `ic_insurance_auto.xml` - Car insurance icon
- `ic_insurance_home.xml` - Home insurance icon
- etc.

## 🎯 Smart Features Included

### Time Zone Intelligence
- Automatically detects user's timezone
- Sends notifications at 9 AM local time
- Supports multiple countries simultaneously

### Duplicate Prevention
- Logs sent notifications to prevent spam
- One notification per insurance per day maximum
- Smart batching for large user bases

### Urgency Levels
- **Tomorrow**: Red urgent notification
- **1-7 days**: Orange warning notification
- **8-30 days**: Blue reminder notification

### Deep Linking
- Notifications open specific insurance details
- Includes insurance ID in notification data
- Handles app state (foreground/background/closed)

## 📊 Monitoring & Analytics

### View Function Logs
```bash
firebase functions:log --only smartInsuranceNotifications
```

### Monitor Notification Delivery
```bash
# Firebase Console → Cloud Messaging → Reports
# Shows delivery rates, open rates, etc.
```

### Debug Common Issues
1. **No notifications received**:
   - Check FCM token registration
   - Verify user timezone is set correctly
   - Check notification permissions

2. **Function not running**:
   - Verify Cloud Scheduler is enabled
   - Check function deployment status
   - Review function logs for errors

3. **Wrong timing**:
   - Verify timezone detection logic
   - Check UTC offset calculations
   - Test with different timezones

## 🚀 Deployment Checklist

### Before Production
- [ ] Test notifications on multiple devices
- [ ] Verify timezone handling for your target countries
- [ ] Test notification appearance on Android/iOS
- [ ] Set up monitoring and alerting
- [ ] Configure notification icons and branding

### Production Settings
- [ ] Set production Firebase project
- [ ] Configure proper notification channels
- [ ] Set up analytics tracking
- [ ] Test with real user data
- [ ] Monitor function performance

## 💰 Cost Monitoring

### Stay in Free Tier
- Monitor function invocations: < 2M/month
- Monitor notification sends: unlimited (FCM is free)
- Monitor Cloud Scheduler: 1 job only

### Scaling Considerations
- 10K users = ~50K function calls/month (well within free tier)
- 100K users = ~500K function calls/month (still free)
- 1M+ users = Consider upgrading to paid tier

## 🔐 Security Best Practices

### Token Management
- Store FCM tokens securely in Firestore
- Refresh tokens periodically
- Remove tokens for uninstalled apps

### Privacy Compliance
- Respect user notification preferences
- Don't include sensitive data in notifications
- Provide easy opt-out mechanism

### Rate Limiting
- Max 1 notification per insurance per day
- Batch processing for large user bases
- Graceful error handling

## 📱 User Experience

### Notification Content
- Clear, actionable messages
- Company logos when available
- Appropriate urgency indicators
- Deep links to relevant screens

### User Controls
- Settings to enable/disable notifications
- Customize notification timing
- Choose which insurance types to notify about

This complete setup will give you a professional-grade notification system that scales from 10 to 100,000+ users while staying completely free!