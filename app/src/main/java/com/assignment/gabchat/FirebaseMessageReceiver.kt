package com.assignment.gabchat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.calls.AuthenticateParams
import com.sendbird.calls.DirectCall
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.handler.DirectCallListener
import com.sendbird.calls.handler.SendBirdCallListener


val channel_id = "notification_channel"
val channelName = "com.assignment.fcmnotificationone"
val TAG = FirebaseMessageReceiver::class.java.simpleName
private const val CHANNEL_ID = "GabChat Ringing"
private const val NOTIFICATION_ID = 0

class FirebaseMessageReceiver : FirebaseMessagingService() {
/*
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {

            showNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    fun showNotification(title: String, message: String) {

        val intent = Intent(this, MainActivity::class.java)


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channel_id
        )
            .setSmallIcon(R.drawable.gabchat)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getCustomDesign(title, message))
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channel_id, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }
        notificationManager.notify(0, builder.build())
    }

    private fun getCustomDesign(title: String, message: String): RemoteViews{
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.icon,
            R.drawable.gabchat
        )
        return remoteViews
    }*/

    init {
        SendBirdCall.addListener(TAG, object : SendBirdCallListener() {
            private val callListener = object : DirectCallListener() {
                override fun onConnected(call: DirectCall) {}

                override fun onEnded(call: DirectCall) {
                    val notificationManager =
                        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(NOTIFICATION_ID)
                }
            }

            override fun onRinging(call: DirectCall) {
                val userId = SharedPreferanceObject.SBUserId ?: return
                if (SendBirdCall.currentUser == null) {
                    SendBirdCall.authenticate(
                        AuthenticateParams(userId)
                    ) { user, e -> showNotification(call) }
                } else {
                    showNotification(call)
                }
                call.setListener(callListener)
            }
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val auth = ServerAuthManager()
        auth.setFcmTokenToSB(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived(remoteMessage: ${remoteMessage.data})")
        if (SendBirdCall.handleFirebaseMessageData(remoteMessage.data)) {
            Log.d(TAG, "handleFirebaseMessageData succeeded.")
        } else {
            Log.d(TAG, "handleFirebaseMessageData failed.")
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GabChat Calling"
            val descriptionText = "Notification for the incoming calls."

            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(call: DirectCall) {
        createNotificationChannel()
        val acceptIntent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.e("CALL_ID Gab accept", "CALL_ID :" + call.callId)
            SharedPreferanceObject.SB_CALL_ID_FCM = call.callId
            SharedPreferanceObject.SB_IS_ACCEPTED_FCM = true
            SharedPreferanceObject.SB_IS_DECLINED_FCM = false
            putExtra("CALL_ID", call.callId)
            putExtra("SBCALL_IS_ACCEPTED", true)
            putExtra("isfcmAccepted", "true")
        }

        val declineIntent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            Log.e("CALL_ID Gab declend", "CALL_ID :" + call.callId)
            SharedPreferanceObject.SB_CALL_ID_FCM = call.callId
            SharedPreferanceObject.SB_IS_ACCEPTED_FCM = false
            SharedPreferanceObject.SB_IS_DECLINED_FCM = true
            putExtra("CALL_ID", call.callId)
            putExtra("SBCALL_IS_DECLINED", true)
            putExtra("isfcmAccepted", "false")
        }

        val randomRequestCode = (Int.MIN_VALUE..Int.MAX_VALUE).random()

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.gabchat)
            .setContentTitle("Ringing")
            .setContentText("${call.remoteUser?.userId} is calling")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                NotificationCompat.Action(
                    0,
                    "Accept",

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getActivity(
                            this,
                            randomRequestCode,
                            acceptIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            this,
                            randomRequestCode,
                            acceptIntent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    }
                )
            )
            .addAction(
                NotificationCompat.Action(
                    0,
                    "Decline",
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getActivity(
                            this,
                            randomRequestCode + 1,
                            declineIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            this,
                            randomRequestCode + 1,
                            declineIntent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                    }

                )
            )

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

}