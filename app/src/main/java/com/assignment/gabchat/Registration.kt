package com.assignment.gabchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.google.firebase.auth.FirebaseAuth

class Registration : AppCompatActivity() {
    private lateinit var firAuth: FirebaseAuth
    private lateinit var btnSubmitUserDetails: Button
    private lateinit  var userName: EditText
    private lateinit  var userNickName: EditText
    private lateinit  var userPhone: TextView

    val REQUEST_CODE_PERMISSION = 0

    private val dangerousPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = dangerousPermissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()
            requestPermissions(permissions, REQUEST_CODE_PERMISSION)
        }
        firAuth = FirebaseAuth.getInstance()
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




