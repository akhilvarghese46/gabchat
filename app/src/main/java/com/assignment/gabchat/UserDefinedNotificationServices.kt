package com.assignment.gabchat

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class UserDefinedNotificationServices : Service() {
    private val channelId = "Notification from GabChat Services "
    private val channelName = "com.assignment.gabchat"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            } else { TODO("VERSION.SDK_INT < O") }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val senderName: String? = intent.getStringExtra("SenderName")
        val msg: String? = intent.getStringExtra("notificationMsg")

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("GabChat")
            .setContentText(msg)
            .setSmallIcon(R.drawable.gabchat)
            .setContentIntent(
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
            })
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

