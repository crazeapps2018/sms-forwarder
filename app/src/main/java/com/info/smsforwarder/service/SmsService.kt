package com.info.smsforwarder.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.info.smsforwarder.utils.NotificationHelper

class SmsService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        NotificationHelper.createChannel(this)
        val notif = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle("SMS Forwarder")
            .setContentText("Service Running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        startForeground(1, notif)
        val work = PeriodicWorkRequestBuilder<ApiPollWorker>(15, java.util.concurrent.TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("pollWork", ExistingPeriodicWorkPolicy.KEEP, work)
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
