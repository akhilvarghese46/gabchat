package com.assignment.gabchat

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnChat: Button
    private lateinit var btnContact: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChat = findViewById<Button>(R.id.btnChatMenu)
        btnContact = findViewById<Button>(R.id.btnContactMenu)

        btnChat.setOnClickListener(){

        }

        btnContact.setOnClickListener(){

        }
        initializeData()
    }

    private fun initializeData() {
        var userName = intent.getStringExtra("userName")
        var userNickname = intent.getStringExtra("userNickName")
    }
}

