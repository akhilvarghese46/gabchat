package com.assignment.gabchat.baseapplication

import android.app.Application
import com.sendbird.android.SendBird
import com.sendbird.calls.SendBirdCall

//const val SENDBIRD_API_ID = "8A6041D3-0601-43F7-A01C-D20F4E0C6F8C"
//const val SENDBIRD_API_ID = "35F54875-BF9D-433D-8BD6-4923B035D649"
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SendBird.init(SENDBIRD_API_ID, applicationContext)
        SendBirdCall.init(applicationContext, SENDBIRD_API_ID)
        SendBirdCall.setLoggerLevel(SendBirdCall.LOGGER_INFO)
    }
    companion object {
        const val SENDBIRD_API_ID = "35F54875-BF9D-433D-8BD6-4923B035D649"
    }

}