package com.insurancereminder.android.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.insurancereminder.shared.repository.InsuranceRepository
import com.insurancereminder.shared.utils.DateUtils
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.first

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = InsuranceRepository(Firebase.firestore)
    private val notificationHelper = NotificationHelper(context)

    override suspend fun doWork(): Result {
        return try {
            val insurances = repository.getActiveInsurances().first()

            insurances.forEach { insurance ->
                val isExpiringSoon = DateUtils.isExpiringSoon(
                    insurance.expiryDate,
                    insurance.reminderDaysBefore
                )
                val isExpired = DateUtils.isExpired(insurance.expiryDate)

                if (isExpired || isExpiringSoon) {
                    val daysUntil = DateUtils.daysUntilExpiry(insurance.expiryDate)
                    notificationHelper.showExpiryNotification(
                        insurance = insurance,
                        daysUntilExpiry = daysUntil
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}