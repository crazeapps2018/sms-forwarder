package com.info.smsforwarder.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.info.smsforwarder.api.ApiClient
import com.info.smsforwarder.model.SmsStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiPollWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val user = "YOUR_PHONE_NUMBER"
        val password = "YOUR_PASSWORD"

        return@withContext try {
            val response = ApiClient.instance.poll(
                mapOf("user" to user, "password" to password)
            )

            val smsReq = response.body()

            smsReq?.let {
                val smsManager = SmsManager.getDefault()
                val sentIntent = PendingIntent.getBroadcast(
                    applicationContext, 0, Intent("SMS_SENT").putExtra("sms_id", it.id),
                    PendingIntent.FLAG_IMMUTABLE
                )
                val deliveredIntent = PendingIntent.getBroadcast(
                    applicationContext, 0, Intent("SMS_DELIVERED").putExtra("sms_id", it.id),
                    PendingIntent.FLAG_IMMUTABLE
                )
                smsManager.sendTextMessage(it.msisdn, null, it.message, sentIntent, deliveredIntent)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
