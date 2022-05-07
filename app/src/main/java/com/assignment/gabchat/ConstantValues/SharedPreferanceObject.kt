package com.assignment.gabchat.ConstantValues

import android.content.Context
import android.content.SharedPreferences

object SharedPreferanceObject {
    private lateinit var spPre: SharedPreferences

    var SBUserId: String? = null
        get() {
            return spPre.getString("SBUserId", null)
        }
        set(value) {

            field = spPre.edit().putString("SBUserId", value).apply().toString()
        }

    var FCMToken: String? = null
        get() {
            return spPre.getString("FCMToken", null)
        }
        set(value) {

            field = spPre.edit().putString("FCMToken", value).apply().toString()
        }

    var SB_CALL_ID_FCM: String? = null
        get() {
            return spPre.getString("SB_CALL_ID_FCM", null)
        }
        set(value) {

            field = spPre.edit().putString("SB_CALL_ID_FCM", value).apply().toString()
        }

    var SB_IS_ACCEPTED_FCM: Boolean = false
        get() {
            return spPre.getBoolean("SB_IS_ACCEPTED_FCM", false)
        }
        set(value) {
            spPre.edit().putBoolean("SB_IS_ACCEPTED_FCM", value).apply()
            field = value
        }

    var SB_IS_DECLINED_FCM: Boolean = false
        get() {
            return spPre.getBoolean("SB_IS_DECLINED_FCM", false)
        }
        set(value) {
            spPre.edit().putBoolean("SB_IS_DECLINED_FCM", value).apply()
            field = value
        }

    fun createSP(context: Context) {
        spPre = context.getSharedPreferences("GabChatSpData", Context.MODE_PRIVATE)
    }


}