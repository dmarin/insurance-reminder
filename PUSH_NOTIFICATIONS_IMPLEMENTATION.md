# Push Notifications Implementation Guide

## Overview
Send automatic push notifications when insurance policies are about to expire using Firebase's free tier.

## Architecture Options

### Option 1: Firebase Cloud Functions + FCM (Recommended)
**Cost**: Free for most apps (2M function calls/month)

```
Cloud Scheduler → Cloud Function → Firestore Query → FCM → User Devices
```

#### Setup Steps:

1. **Enable Firebase Services**
```bash
# In your Firebase project
- Cloud Functions
- Cloud Messaging (FCM)
- Cloud Scheduler
```

2. **Install Firebase CLI and Functions**
```bash
npm install -g firebase-tools
firebase login
firebase init functions
```

3. **Cloud Function Implementation**
```javascript
// functions/index.js
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

// Scheduled function - runs daily at 9 AM
exports.checkExpiringInsurances = functions.pubsub
  .schedule('0 9 * * *')
  .timeZone('Europe/Madrid')
  .onRun(async (context) => {
    const db = admin.firestore();
    const messaging = admin.messaging();

    // Calculate date ranges
    const today = new Date();
    const futureDate = new Date(today.getTime() + (30 * 24 * 60 * 60 * 1000)); // 30 days from now

    try {
      // Query insurances expiring soon
      const snapshot = await db.collection('insurances')
        .where('isActive', '==', true)
        .where('expiryDate', '>=', today.toISOString().split('T')[0])
        .where('expiryDate', '<=', futureDate.toISOString().split('T')[0])
        .get();

      const notifications = [];

      for (const doc of snapshot.docs) {
        const insurance = doc.data();
        const expiryDate = new Date(insurance.expiryDate);
        const daysUntilExpiry = Math.ceil((expiryDate - today) / (1000 * 60 * 60 * 24));

        // Check if we should send notification based on reminderDaysBefore
        if (daysUntilExpiry <= insurance.reminderDaysBefore) {
          // Get user's FCM token
          const userDoc = await db.collection('users').doc(insurance.userId).get();
          const fcmToken = userDoc.data()?.fcmToken;

          if (fcmToken) {
            const message = {
              token: fcmToken,
              notification: {
                title: `${insurance.name} Expires Soon!`,
                body: `Your ${insurance.type.displayName.toLowerCase()} expires in ${daysUntilExpiry} day${daysUntilExpiry !== 1 ? 's' : ''}`,
                imageUrl: insurance.companyLogoUrl || getDefaultInsuranceIcon(insurance.type)
              },
              data: {
                insuranceId: doc.id,
                type: 'expiry_reminder',
                daysUntilExpiry: daysUntilExpiry.toString()
              },
              android: {
                notification: {
                  icon: 'ic_notification',
                  color: '#1976D2',
                  sound: 'default',
                  clickAction: 'FLUTTER_NOTIFICATION_CLICK'
                }
              },
              apns: {
                payload: {
                  aps: {
                    sound: 'default',
                    badge: 1
                  }
                }
              }
            };

            notifications.push(messaging.send(message));
          }
        }
      }

      // Send all notifications
      await Promise.all(notifications);
      console.log(`Sent ${notifications.length} expiry notifications`);

    } catch (error) {
      console.error('Error checking expiring insurances:', error);
    }
  });

function getDefaultInsuranceIcon(insuranceType) {
  const iconMap = {
    'AUTO': 'https://your-cdn.com/icons/car.png',
    'MOTORCYCLE': 'https://your-cdn.com/icons/motorcycle.png',
    'HOME': 'https://your-cdn.com/icons/home.png',
    'HEALTH': 'https://your-cdn.com/icons/health.png',
    'DENTAL': 'https://your-cdn.com/icons/dental.png',
    'LIFE': 'https://your-cdn.com/icons/life.png',
    'PET': 'https://your-cdn.com/icons/pet.png',
    'TRAVEL': 'https://your-cdn.com/icons/travel.png'
  };

  return iconMap[insuranceType] || 'https://your-cdn.com/icons/default.png';
}

// Function to register FCM token when user logs in
exports.registerFCMToken = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }

  const { fcmToken } = data;
  const userId = context.auth.uid;

  await admin.firestore().collection('users').doc(userId).update({
    fcmToken: fcmToken,
    lastTokenUpdate: admin.firestore.FieldValue.serverTimestamp()
  });

  return { success: true };
});
```

4. **Android Implementation**
```kotlin
// Add to build.gradle
implementation platform('com.google.firebase:firebase-bom:32.7.0')
implementation 'com.google.firebase:firebase-messaging'

// FirebaseMessagingService
class InsuranceNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle notification when app is in foreground
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "Insurance Reminder",
                body = notification.body ?: "Check your insurance",
                data = remoteMessage.data
            )
        }
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "insurance_reminders",
                "Insurance Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for insurance expiry reminders"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open specific insurance
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("insuranceId", data["insuranceId"])
            putExtra("openInsurance", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, "insurance_reminders")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        notificationManager.notify(data["insuranceId"].hashCode(), notification)
    }

    private fun sendTokenToServer(token: String) {
        // Call Firebase Cloud Function to register token
        val functions = Firebase.functions
        val data = hashMapOf("fcmToken" to token)

        functions.getHttpsCallable("registerFCMToken")
            .call(data)
            .addOnSuccessListener {
                Log.d("FCM", "Token registered successfully")
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Failed to register token", e)
            }
    }
}
```

5. **iOS Implementation**
```swift
// AppDelegate.swift
import FirebaseMessaging

class AppDelegate: UIResponder, UIApplicationDelegate, MessagingDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        // Configure Firebase
        FirebaseApp.configure()

        // Set messaging delegate
        Messaging.messaging().delegate = self

        // Request notification permissions
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
        }

        return true
    }

    // FCM Token received
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }

        // Send token to server
        sendTokenToServer(token: token)
    }

    private func sendTokenToServer(token: String) {
        // Call Firebase Cloud Function
        let functions = Functions.functions()

        functions.httpsCallable("registerFCMToken").call(["fcmToken": token]) { result, error in
            if let error = error {
                print("Error registering FCM token: \(error)")
            } else {
                print("FCM token registered successfully")
            }
        }
    }
}
```

### Option 2: Third-Party Services (Alternative)

#### OneSignal (Free tier: 10K subscribers)
```javascript
// Very simple setup
const OneSignal = require('onesignal-node');

const client = new OneSignal.Client('your-app-id', 'your-api-key');

const notification = {
    contents: { en: 'Your insurance expires in 7 days!' },
    included_segments: ['All'],
    filters: [
        { field: 'tag', key: 'user_id', relation: '=', value: userId }
    ]
};

await client.createNotification(notification);
```

#### Pusher Beams (Free tier: 1K devices)
```javascript
const PushNotifications = require('@pusher/push-notifications-server');

const beamsClient = new PushNotifications({
    instanceId: 'your-instance-id',
    secretKey: 'your-secret-key'
});

await beamsClient.publishToUsers([userId], {
    fcm: {
        notification: {
            title: 'Insurance Expiring',
            body: 'Your auto insurance expires in 7 days'
        }
    },
    apns: {
        aps: {
            alert: {
                title: 'Insurance Expiring',
                body: 'Your auto insurance expires in 7 days'
            }
        }
    }
});
```

## Cost Comparison

| Solution | Free Tier | Paid Tier | Best For |
|----------|-----------|-----------|----------|
| **Firebase FCM + Functions** | 2M calls/month | $0.40/M calls | Full control, integrated |
| **OneSignal** | 10K subscribers | $9/month unlimited | Simple setup |
| **Pusher Beams** | 1K devices | $10/month 10K devices | Real-time features |
| **AWS SNS** | 1M pushes/month | $0.50/M pushes | AWS ecosystem |

## Recommended Implementation Plan

### Phase 1: Basic Setup (1-2 days)
1. Enable FCM in Firebase project
2. Add FCM dependencies to Android/iOS
3. Implement token registration
4. Test manual notifications

### Phase 2: Scheduled Notifications (2-3 days)
1. Create Cloud Function for expiry checking
2. Set up Cloud Scheduler
3. Implement notification logic
4. Test with sample data

### Phase 3: Advanced Features (1-2 days)
1. Deep linking to specific insurance
2. Notification preferences
3. Batch processing optimization
4. Analytics and monitoring

## Total Cost Estimate

For a typical insurance app:
- **Users**: 1K-10K
- **Notifications**: ~100-1000/day
- **Monthly function calls**: ~30K
- **Result**: **Completely FREE** on Firebase

## Security Considerations

1. **Token Security**: Store FCM tokens securely in Firestore
2. **User Permissions**: Respect notification preferences
3. **Data Privacy**: Don't include sensitive info in notifications
4. **Rate Limiting**: Prevent spam notifications

Firebase Cloud Functions + FCM is the **recommended approach** because:
- ✅ **Free** for most use cases
- ✅ **Integrated** with your existing Firebase setup
- ✅ **Scalable** automatically
- ✅ **Cross-platform** (Android, iOS, Web)
- ✅ **No additional backend** required
- ✅ **Rich notification features** (images, actions, deep linking)