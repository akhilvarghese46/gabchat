package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.assignment.gabchat.dataclass.UserDetails
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class Registration : AppCompatActivity() {
    //private lateinit var firAuth: FirebaseAuth
    private lateinit var btnSubmitUserDetails: Button
    private lateinit var userName: EditText

    // private lateinit  var userNickName: EditText
    private lateinit var userPhone: EditText
    private lateinit var edtUserPassword: EditText
    private lateinit var userImage: View

    private lateinit var userNameData: String

    var firebaseDatabase: FirebaseDatabase? = null
    var dbRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        userImage = findViewById<View>(R.id.edtUserProfileImage)

        var phoneNumber = this.intent.getStringExtra("phoneNumber").toString()
        var userPassword = this.intent.getStringExtra("userPassword").toString()
        userNameData = this.intent.getStringExtra("userName").toString()
        if (userNameData != null && userNameData != "null") {
            setProfileImage()
        }
        //firAuth = FirebaseAuth.getInstance()
        btnSubmitUserDetails = findViewById<Button>(R.id.btnAddUserDetails)
        userName = findViewById<EditText>(R.id.edtUserName)
        // userNickName = findViewById<EditText>(R.id.edtNickName)
        userPhone = findViewById<EditText>(R.id.edtPhoneNumber)
        userPhone.setEnabled(false)
        userPhone.setKeyListener(null)
        edtUserPassword = findViewById<EditText>(R.id.edtPassword)
        userPhone.setText(phoneNumber)

        if (phoneNumber != null && userPassword != null && userNameData != null && phoneNumber != "null" && userPassword != "null" && userNameData != "null") {
            userName.setText(userNameData)
            userName.setEnabled(false)
            userName.setKeyListener(null)
        }

        /*  if(SharedPreferanceObject.phoneNumber.toString() != null)
          {
              userPhone.setText(SharedPreferanceObject.phoneNumber.toString())
          }*/

        btnSubmitUserDetails.setOnClickListener {
            SharedPreferanceObject.phoneNumber = userPhone.getText().toString()

            if (TextUtils.isEmpty(userName.getText().toString()) || TextUtils.isEmpty(
                    edtUserPassword.getText().toString()
                ) || TextUtils.isEmpty(edtUserPassword.getText().toString())
            ) {
                Toast.makeText(this, "Enter User Details.", Toast.LENGTH_SHORT).show()
                if (TextUtils.isEmpty(edtUserPassword.getText().toString())) {
                    Toast.makeText(this, "Enter the password.", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (userPassword != null && userNameData != null && userPassword != "null" && userNameData != "null") {
                    if (edtUserPassword.getText().toString() != userPassword) {
                        Toast.makeText(this, "Password is incorrect!!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        getMainActivity()
                    }
                } else {
                    SharedPreferanceObject.phoneNumber = userPhone.getText().toString()
                    insertLoginUserDetails(
                        userName.getText().toString(),
                        userPhone.getText().toString(),
                        edtUserPassword.getText().toString()
                    )
                }
            }
        }
    }

    private fun setProfileImage() {
        var imageRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("profilePic/" + userNameData + ".jpg")
        if (imageRef != null) {
            val localFile = File.createTempFile("profilePic", "jpg")

            imageRef.getFile(localFile).addOnSuccessListener {
                Glide.with(this)
                    .load(localFile.absolutePath)
                    .circleCrop()
                    .into(userImage as ImageView)
            }.addOnFailureListener {
                Log.e("GabChat error:", "error when getting profile picture.")
            }
        }
    }

    fun insertLoginUserDetails(userName: String, phoneNumber: String, userPassword: String) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase!!.getReference("UserDetails")
        var msgInfo = UserDetails(userName.lowercase(), phoneNumber, userPassword)
        val id: String = dbRef!!.push().getKey().toString()
        dbRef!!.child(id).setValue(msgInfo)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Log.e("GabCaht- UserDetails:", "Data added to firebase")
                    getMainActivity()

                }
            })

    }

    fun getMainActivity() {
        // ServerAuthManager()
        SharedPreferanceObject.createSP(applicationContext)
        SharedPreferanceObject.SBUserId = userName.getText().toString().lowercase()
        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("userName", userName.text.toString().lowercase())
        intent.putExtra("userNickName", userName.text.toString().lowercase())
        startActivity(intent)
    }


}




