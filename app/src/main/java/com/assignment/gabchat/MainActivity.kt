package com.assignment.gabchat

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var btnChat: Button
    private lateinit var btnContact: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }
}

