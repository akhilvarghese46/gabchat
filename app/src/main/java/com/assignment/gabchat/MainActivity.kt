package com.assignment.gabchat

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sendbird.android.SendBird


class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var btnChat: Button
    private lateinit var btnContact: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        SendBird.init("7CC919C1-9EE4-46A7-86FD-42BB871F4297", this)


        btnChat = findViewById<Button>(R.id.btnChatMenu)
        btnContact = findViewById<Button>(R.id.btnContactMenu)

        btnChat.setOnClickListener(){

            loadFragment( ChatFragment())

        }

        btnContact.setOnClickListener(){
            loadFragment( ContactFragment())
        }
        initializeData()

    }

    private fun loadFragment(fragment: Fragment) {
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainDataFragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    private fun initializeData() {
        var userName = intent.getStringExtra("userName")
        var userNickname = intent.getStringExtra("userNickName")
        connectUserToServer(userName.toString(), userNickname.toString() )
    }

    fun connectUserToServer(userName: String, nickName: String)  {

        SendBird.connect(userName) { username, e ->
            if (e != null) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            } else {
                SendBird.updateCurrentUserInfo(nickName, null) { e ->
                    if (e != null) {
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                    loadFragment( ChatFragment())
                    /*val intent = Intent(this, UserslistActivity::class.java)
                    startActivity(intent)
                    finish()*/
                }
            }
        }
    }
}

