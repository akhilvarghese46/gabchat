package com.assignment.gabchat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder


class ScreenShareService_old : Service() {
    val CHANNEL_ID = "ScreenShareChannel"
    val NOTIFICATION_ID: Int = 1
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
       // createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Sendbird Calls ScreenShare Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }

        val notif: Notification = builder
            .setContentTitle("Sendbird Calls")
            .setContentText("Screen sharing...")
            .setSmallIcon(R.drawable.gabchat)
            .build()

        startForeground(NOTIFICATION_ID, notif)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}