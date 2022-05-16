package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject


class Registration : AppCompatActivity() {
    //private lateinit var firAuth: FirebaseAuth
    private lateinit var btnSubmitUserDetails: Button
    private lateinit  var userName: EditText
    private lateinit  var userNickName: EditText
    private lateinit  var userPhone: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        //firAuth = FirebaseAuth.getInstance()
        btnSubmitUserDetails = findViewById<Button>(R.id.btnAddUserDetails)
        userName = findViewById<EditText>(R.id.edtUserName)
        userNickName = findViewById<EditText>(R.id.edtNickName)
        userPhone = findViewById<TextView>(R.id.edtPhoneNumber)

        btnSubmitUserDetails.setOnClickListener{
            if (TextUtils.isEmpty(userName.getText().toString())||TextUtils.isEmpty(userNickName.getText().toString())) {
                Toast.makeText(this, "Enter User Details.", Toast.LENGTH_SHORT).show()
            } else {

               // ServerAuthManager()
                 SharedPreferanceObject.createSP(applicationContext)
                SharedPreferanceObject.SBUserId = userName.getText().toString()
                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("userName", userName.text.toString())
                intent.putExtra("userNickName", userNickName.text.toString())
                startActivity(intent)
            }
        }
    }


}




