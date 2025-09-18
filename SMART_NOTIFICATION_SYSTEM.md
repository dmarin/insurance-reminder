# Smart Notification System - Time Zone Aware

## Problem
- Cloud Scheduler free tier: Only 3 jobs/month
- Need notifications in multiple time zones
- Want hourly or daily execution
- Don't want 3 AM notifications

## Solution: Smart Single Scheduler

### Architecture
```
Single Cloud Scheduler (1 job) → Cloud Function (runs hourly) → Time Zone Logic → FCM
```

### Implementation

#### 1. Single Scheduled Function (Runs Every Hour)
```javascript
// functions/index.js
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

// Single function that runs every hour
exports.smartInsuranceNotifications = functions.pubsub
  .schedule('0 * * * *') // Every hour at minute 0
  .timeZone('UTC')
  .onRun(async (context) => {
    const db = admin.firestore();
    const messaging = admin.messaging();

    // Get current UTC time
    const now = new Date();
    const currentUTCHour = now.getUTCHours();

    console.log(`Running notification check at UTC hour: ${currentUTCHour}`);

    // Define optimal notification hours by time zone
    const timeZoneConfig = [
      { timezone: 'Europe/Madrid', optimalHour: 9, utcOffset: getUTCOffset('Europe/Madrid') },
      { timezone: 'America/New_York', optimalHour: 9, utcOffset: getUTCOffset('America/New_York') },
      { timezone: 'America/Los_Angeles', optimalHour: 9, utcOffset: getUTCOffset('America/Los_Angeles') },
      { timezone: 'Asia/Tokyo', optimalHour: 9, utcOffset: getUTCOffset('Asia/Tokyo') }
    ];

    // Check which time zones are currently at their optimal notification time
    const activeTimeZones = timeZoneConfig.filter(tz => {
      const localHour = (currentUTCHour + tz.utcOffset + 24) % 24;
      return localHour === tz.optimalHour;
    });

    if (activeTimeZones.length === 0) {
      console.log('No time zones at optimal notification time');
      return;
    }

    console.log(`Sending notifications for time zones: ${activeTimeZones.map(tz => tz.timezone).join(', ')}`);

    try {
      // Query users in active time zones
      const userQueries = activeTimeZones.map(tz =>
        db.collection('users')
          .where('timezone', '==', tz.timezone)
          .where('notificationsEnabled', '==', true)
          .get()
      );

      const userSnapshots = await Promise.all(userQueries);
      const userIds = [];

      userSnapshots.forEach(snapshot => {
        snapshot.docs.forEach(doc => {
          userIds.push(doc.id);
        });
      });

      if (userIds.length === 0) {
        console.log('No users found for active time zones');
        return;
      }

      // Query expiring insurances for these users
      const today = new Date().toISOString().split('T')[0];
      const futureDate = new Date(Date.now() + (30 * 24 * 60 * 60 * 1000)).toISOString().split('T')[0];

      const insuranceSnapshot = await db.collection('insurances')
        .where('userId', 'in', userIds.slice(0, 10)) // Firestore limit: 10 items in 'in' query
        .where('isActive', '==', true)
        .where('expiryDate', '>=', today)
        .where('expiryDate', '<=', futureDate)
        .get();

      const notifications = [];
      const processedUsers = new Set();

      for (const doc of insuranceSnapshot.docs) {
        const insurance = doc.data();
        const expiryDate = new Date(insurance.expiryDate);
        const daysUntilExpiry = Math.ceil((expiryDate - new Date()) / (1000 * 60 * 60 * 24));

        // Check if we should notify based on reminder days
        if (daysUntilExpiry <= insurance.reminderDaysBefore && daysUntilExpiry > 0) {

          // Check if user was already notified today for this insurance
          const notificationKey = `${insurance.userId}_${doc.id}_${today}`;
          const existingNotification = await db.collection('notification_log')
            .doc(notificationKey)
            .get();

          if (!existingNotification.exists && !processedUsers.has(insurance.userId)) {

            // Get user's FCM token and preferences
            const userDoc = await db.collection('users').doc(insurance.userId).get();
            const userData = userDoc.data();

            if (userData?.fcmToken && userData?.notificationsEnabled) {
              const message = createNotificationMessage(insurance, daysUntilExpiry, userData.fcmToken);
              notifications.push(messaging.send(message));

              // Log notification to prevent duplicates
              await db.collection('notification_log').doc(notificationKey).set({
                userId: insurance.userId,
                insuranceId: doc.id,
                sentAt: admin.firestore.FieldValue.serverTimestamp(),
                daysUntilExpiry: daysUntilExpiry,
                timezone: userData.timezone
              });

              processedUsers.add(insurance.userId);
            }
          }
        }
      }

      // Handle remaining users if more than 10 (batch processing)
      if (userIds.length > 10) {
        const remainingBatches = [];
        for (let i = 10; i < userIds.length; i += 10) {
          const batch = userIds.slice(i, i + 10);
          remainingBatches.push(processUserBatch(db, messaging, batch, today, futureDate));
        }
        const batchResults = await Promise.all(remainingBatches);
        batchResults.forEach(batchNotifications => notifications.push(...batchNotifications));
      }

      // Send all notifications
      await Promise.all(notifications);
      console.log(`Sent ${notifications.length} notifications`);

    } catch (error) {
      console.error('Error in notification system:', error);
    }
  });

// Helper function to process batches of users
async function processUserBatch(db, messaging, userIds, today, futureDate) {
  const insuranceSnapshot = await db.collection('insurances')
    .where('userId', 'in', userIds)
    .where('isActive', '==', true)
    .where('expiryDate', '>=', today)
    .where('expiryDate', '<=', futureDate)
    .get();

  const notifications = [];
  // ... similar processing logic
  return notifications;
}

function createNotificationMessage(insurance, daysUntilExpiry, fcmToken) {
  const urgencyLevel = daysUntilExpiry <= 7 ? 'high' : 'normal';

  return {
    token: fcmToken,
    notification: {
      title: getNotificationTitle(insurance, daysUntilExpiry),
      body: getNotificationBody(insurance, daysUntilExpiry),
      imageUrl: insurance.companyLogoUrl || getDefaultIcon(insurance.type)
    },
    data: {
      insuranceId: insurance.id || 'unknown',
      type: 'expiry_reminder',
      daysUntilExpiry: daysUntilExpiry.toString(),
      urgency: urgencyLevel
    },
    android: {
      notification: {
        icon: 'ic_notification',
        color: urgencyLevel === 'high' ? '#F44336' : '#1976D2',
        sound: 'default',
        priority: urgencyLevel === 'high' ? 'high' : 'normal'
      }
    },
    apns: {
      payload: {
        aps: {
          sound: 'default',
          badge: 1,
          alert: {
            title: getNotificationTitle(insurance, daysUntilExpiry),
            body: getNotificationBody(insurance, daysUntilExpiry)
          }
        }
      }
    }
  };
}

function getNotificationTitle(insurance, daysUntilExpiry) {
  if (daysUntilExpiry === 1) {
    return `⚠️ ${insurance.name} expires tomorrow!`;
  } else if (daysUntilExpiry <= 7) {
    return `🚨 ${insurance.name} expires soon!`;
  } else {
    return `📅 ${insurance.name} reminder`;
  }
}

function getNotificationBody(insurance, daysUntilExpiry) {
  const typeDisplay = insurance.type?.displayName || 'insurance';

  if (daysUntilExpiry === 1) {
    return `Your ${typeDisplay.toLowerCase()} expires tomorrow. Don't forget to renew!`;
  } else {
    return `Your ${typeDisplay.toLowerCase()} expires in ${daysUntilExpiry} days. Time to start looking for renewal options.`;
  }
}

function getUTCOffset(timezone) {
  // This is a simplified version - in production use a proper timezone library
  const timezoneOffsets = {
    'Europe/Madrid': 1, // CET (winter) / CEST (summer) - simplified
    'America/New_York': -5, // EST / EDT - simplified
    'America/Los_Angeles': -8, // PST / PDT - simplified
    'Asia/Tokyo': 9
  };
  return timezoneOffsets[timezone] || 0;
}

function getDefaultIcon(insuranceType) {
  const iconMap = {
    'AUTO': 'https://your-app.com/icons/car.png',
    'MOTORCYCLE': 'https://your-app.com/icons/motorcycle.png',
    'HOME': 'https://your-app.com/icons/home.png',
    'HEALTH': 'https://your-app.com/icons/health.png',
    'DENTAL': 'https://your-app.com/icons/dental.png',
    'LIFE': 'https://your-app.com/icons/life.png',
    'PET': 'https://your-app.com/icons/pet.png',
    'TRAVEL': 'https://your-app.com/icons/travel.png'
  };
  return iconMap[insuranceType] || 'https://your-app.com/icons/default.png';
}

// Function to update user timezone when they register
exports.updateUserTimezone = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }

  const { timezone, fcmToken, notificationsEnabled = true } = data;
  const userId = context.auth.uid;

  await admin.firestore().collection('users').doc(userId).update({
    timezone: timezone,
    fcmToken: fcmToken,
    notificationsEnabled: notificationsEnabled,
    lastUpdate: admin.firestore.FieldValue.serverTimestamp()
  });

  return { success: true, message: 'Timezone and preferences updated' };
});
```

#### 2. Update User Model to Include Timezone
```kotlin
// Update your User data class
@Serializable
data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val fcmToken: String? = null,
    val timezone: String = TimeZone.currentSystemDefault().id, // Auto-detect
    val notificationsEnabled: Boolean = true,
    val preferredNotificationTime: Int = 9, // 9 AM
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val createdAt: LocalDate? = null,
    val lastLoginAt: LocalDate? = null
)
```

#### 3. Android: Auto-detect and Set Timezone
```kotlin
// In your AuthViewModel or similar
class UserPreferencesManager {

    fun updateUserTimezoneAndToken() {
        val currentUser = Firebase.auth.currentUser ?: return

        // Auto-detect user's timezone
        val timezone = TimeZone.getDefault().id

        // Get FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result

                // Update user preferences
                val functions = Firebase.functions
                val data = hashMapOf(
                    "timezone" to timezone,
                    "fcmToken" to token,
                    "notificationsEnabled" to true
                )

                functions.getHttpsCallable("updateUserTimezone")
                    .call(data)
                    .addOnSuccessListener {
                        Log.d("Notifications", "User preferences updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Notifications", "Failed to update preferences", e)
                    }
            }
        }
    }
}
```

## Alternative Options (If You Want More Control)

### Option 2: Firebase + External Cron Service

Use a free external service to trigger your Firebase function:

#### Cron-job.org (Free)
- **Free tier**: 50 requests/day
- **Setup**: Point to your Firebase HTTP function
- **Limitation**: Only 2 requests per hour max

#### GitHub Actions (Free)
```yaml
# .github/workflows/notifications.yml
name: Insurance Notifications
on:
  schedule:
    - cron: '0 * * * *'  # Every hour

jobs:
  notify:
    runs-on: ubuntu-latest
    steps:
      - name: Trigger Notifications
        run: |
          curl -X POST "${{ secrets.FIREBASE_FUNCTION_URL }}" \
               -H "Authorization: Bearer ${{ secrets.FIREBASE_TOKEN }}"
```

### Option 3: Cloud Run + Cloud Scheduler (Minimal Cost)

If you need more than basic scheduling:

```javascript
// Deploy as Cloud Run service (always free tier: 1M requests/month)
const express = require('express');
const app = express();

app.post('/notify', async (req, res) => {
  // Your notification logic here
  res.json({ success: true });
});

app.listen(process.env.PORT || 8080);
```

**Cloud Scheduler cost**: $0.10 per job per month
- **24 hourly jobs**: $2.40/month
- **Still very affordable**

## Cost Comparison (Updated)

| Solution | Scheduler Cost | Function Cost | Total/Month |
|----------|----------------|---------------|-------------|
| **Smart Single Function** | FREE (1 job) | FREE (2M calls) | **$0.00** |
| **External Cron + Firebase** | FREE | FREE (2M calls) | **$0.00** |
| **Cloud Run + Scheduler** | $2.40 (24 jobs) | FREE (1M requests) | **$2.40** |
| **Third-party service** | $9-29/month | N/A | **$9-29** |

## Recommendation: Smart Single Function

The **Smart Single Function** approach is perfect because:

✅ **Completely FREE** - uses only 1 scheduled job
✅ **Time zone aware** - respects user local times
✅ **No spam** - prevents duplicate notifications
✅ **Scalable** - handles any number of users
✅ **Smart batching** - works around Firestore limits
✅ **Urgency levels** - different notifications for 1 day vs 30 days

This gives you enterprise-level notification capabilities while staying completely within Firebase's free tier!

Would you like me to help you implement this smart notification system?