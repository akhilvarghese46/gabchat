package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var btnChat: Button
    private lateinit var btnContact: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        btnChat = findViewById<Button>(R.id.btnChatMenu)
       // btnContact = findViewById<Button>(R.id.btnContactMenu)

        btnChat.setOnClickListener(){

            loadFragment( ChatFragment())

        }

       /* btnContact.setOnClickListener(){
            loadFragment( ContactFragment())
        }*/

        fab_createUser.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }
        initializeData()





    }



    override fun onCreateOptionsMenu(settingsMenu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settingsmenu, settingsMenu)
        return super.onCreateOptionsMenu(settingsMenu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.settings -> {

                return true
            }
            R.id.logOut -> {

                return true
            }
        }
// it is a requirement to call this at the end of this function if the event has not been
// handled
        return super.onOptionsItemSelected(item)
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

    fun connectUserToServer(userName: String, nickName: String) {
        val auth = ServerAuthManager()
        val returnauth = auth.aunthenticate(userName)
        //Toast.makeText(this, "return auth :" + returnauth, Toast.LENGTH_LONG).show()
        if (returnauth) {
            SendBird.connect(userName) { username, e ->
                if (e != null) {
                    Toast.makeText(this, "error connect" +e.message, Toast.LENGTH_LONG).show()
                } else {
                    SendBird.updateCurrentUserInfo(nickName, null) { e ->
                        if (e != null) {
                            Toast.makeText(this, "error update" + e.message, Toast.LENGTH_LONG).show()
                        }
                        loadFragment(ChatFragment())
                        /*val intent = Intent(this, UserslistActivity::class.java)
                    startActivity(intent)
                    finish()*/
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getCallActivity() {
        val intent = Intent(this, CallActivity::class.java)
        startActivity(intent)
    }
}

