package com.assignment.gabchat


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sendbird.android.SendBird


 class InitializeServerApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }



      fun connectUserToServer(userName: String, nickName: String)  {
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
     }

}