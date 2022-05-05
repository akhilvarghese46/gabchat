package com.assignment.gabchat


import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.calls.AuthenticateParams
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.User
import com.sendbird.calls.handler.AuthenticateHandler


class ServerAuthManager : AppCompatActivity() {

    fun aunthenticate(userID: String):Boolean
    {
        var returnvalue:Boolean =true

        SendBirdCall.authenticate(AuthenticateParams(userID), object : AuthenticateHandler {
            override fun onResult(user: User?, e: SendBirdException?) {
                if (e != null) {
                    e.printStackTrace()
                    e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }
                    returnvalue =false
                }
            }
        })

        return returnvalue
    }


     /* fun connectUserToServer(userName: String, nickName: String)  {
           val APP_ID = "7CC919C1-9EE4-46A7-86FD-42BB871F4297"
          SendBird.init(APP_ID, this)
         SendBird.connect(userName) { username, e ->
             if (e != null) {
                 Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
             } else {
                 SendBird.updateCurrentUserInfo(nickName, null) { e ->
                     if (e != null) {
                         Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                     }
                     /*val intent = Intent(this, UserslistActivityTest::class.java)
                     startActivity(intent)
                     finish()*/
                 }
             }
         }
     }*/

}