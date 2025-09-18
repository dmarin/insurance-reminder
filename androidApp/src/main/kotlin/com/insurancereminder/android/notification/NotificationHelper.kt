package com.insurancereminder.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.insurancereminder.android.MainActivity
import com.insurancereminder.android.R
import com.insurancereminder.shared.model.Insurance

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "insurance_reminders"
        private const val CHANNEL_NAME = "Insurance Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for insurance expiry reminders"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showExpiryNotification(insurance: Insurance, daysUntilExpiry: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, text) = when {
            daysUntilExpiry < 0 -> {
                "Insurance Expired!" to "${insurance.name} has expired ${-daysUntilExpiry} days ago"
            }
            daysUntilExpiry == 0 -> {
                "Insurance Expires Today!" to "${insurance.name} expires today"
            }
            else -> {
                "Insurance Expiring Soon" to "${insurance.name} expires in $daysUntilExpiry days"
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(insurance.id.hashCode(), builder.build())
            } catch (e: SecurityException) {
                // Handle missing notification permission
            }
        }
    }
}