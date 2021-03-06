package com.assignment.gabchat


import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.sendbird.calls.AuthenticateParams
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.User
import com.sendbird.calls.handler.AuthenticateHandler
import com.sendbird.calls.handler.CompletionHandler


class ServerAuthManager : AppCompatActivity() {

    //Authenticate user details with SendBird
    fun aunthenticate(userID: String): Boolean {
        var returnvalue: Boolean = true

        SendBirdCall.authenticate(AuthenticateParams(userID), object : AuthenticateHandler {
            override fun onResult(user: User?, e: SendBirdException?) {
                if (e != null) {
                    e.printStackTrace()
                    e.message?.let { Log.e("GABCHAT error (Authentication):", it) }
                    returnvalue = false
                } else {
                    getFCMToken()
                }
            }
        })

        return returnvalue
    }

    //get FCM token 
    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { token ->
            if (token.isSuccessful) {
                setFcmTokenToSB(token.result)
            } else {
                Log.e(
                    "GABCHAT error (getFCMToken):", token.exception?.message.toString()
                )
            }
        }
    }

    // set fcm token to sendbird
    fun setFcmTokenToSB(fcmToken: String) {
        if (SendBirdCall.currentUser != null) {
            SendBirdCall.registerPushToken(fcmToken, false, object : CompletionHandler {
                override fun onResult(e: SendBirdException?) {
                    if (e != null) {
                        e.printStackTrace()
                        e.message?.let { Log.e("GABCHAT error (Authentication):", it) }
                    }
                }
            })
        }

    }


}