package com.assignment.gabchat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.assignment.gabchat.dataclass.FireBaseMessageData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sendbird.android.SendBird
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.CompletionHandler
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var btnChat: Button
    private lateinit var btnCall: Button
    lateinit  var ssData:ArrayList<FireBaseMessageData>

    val REQUEST_CODE_PERMISSION = 0
    private val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setToolBar()


        getMessage(SharedPreferanceObject.SBUserId.toString())

        btnChat = findViewById<Button>(R.id.btnChatMenu)
        btnCall = findViewById<Button>(R.id.btnCallMenu)
        btnChat.setOnClickListener(){
            loadFragment( ChatFragment())
        }

        btnCall.setOnClickListener(){
            loadFragment( CallHistoryFragment())
        }

        fab_createUser.setOnClickListener{
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }
        initializeData()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var permissions = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if(permissions.size != 0) {
                requestPermissions(permissions, REQUEST_CODE_PERMISSION)
            }
        }



    }

    @SuppressLint("RestrictedApi")
    private fun setToolBar() {

        var actionBar: ActionBar = getSupportActionBar()!!
        if(actionBar != null) {
            actionBar.setTitle("GabChat")
        }

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

    }


    override fun onCreateOptionsMenu(settingsMenu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settingsmenu, settingsMenu)
        return super.onCreateOptionsMenu(settingsMenu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.settings -> {
                val intent = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.logOut -> {
                logoutUser()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        SharedPreferanceObject.SBUserId = null
        SharedPreferanceObject.SB_IS_DECLINED_FCM = false
        SharedPreferanceObject.SB_IS_ACCEPTED_FCM = false
        SharedPreferanceObject.SB_CALL_ID_FCM = null
        SharedPreferanceObject.FCMToken = null

        SendBirdCall.deauthenticate(object : CompletionHandler {
            override fun onResult(e: SendBirdException?) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                //this@MainActivity.finish()
            }
        })
    }


    private fun loadFragment(fragment: Fragment) {
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mainDataFragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    private fun initializeData() {

        var userName = intent.getStringExtra("userName")
        Log.e("Main Activity username",userName.toString())
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

    fun getMessage( currentUserName: String){
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef= FirebaseDatabase.getInstance().getReference("screenShortMessage")
        dbRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        var data = userSnapshot.getValue(FireBaseMessageData::class.java)!!
                        if(currentUserName == data.ReciverUserName) {
                            val serviceIntent = Intent(this@MainActivity, UserDefinedNotificationServices::class.java)
                            serviceIntent.putExtra("SenderName",data.SenderUserName)
                            serviceIntent.putExtra("notificationMsg",data.Message)
                            ContextCompat.startForegroundService(this@MainActivity, serviceIntent)
                            userSnapshot.getRef().removeValue();

                        }

                    }
                }else{
                    Log.e("screenshort error","not found data")
                }
                return

            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Log.d("Registration Activity", "[${permissions.joinToString()}] is granted.")
        }
    }
}

