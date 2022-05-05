package com.assignment.gabchat

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
            return spPre.getString("SBUserId", null)
        }
        set(value) {

            field = spPre.edit().putString("SBUserId", value).apply().toString()
        }

    fun createSP(context: Context) {
        spPre = context.getSharedPreferences("GabChatSpData", Context.MODE_PRIVATE)
    }


}