package com.assignment.gabchat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class SettingActivity : AppCompatActivity() {

    private lateinit var userImage: View
    private val pickImage = 100
    private var userImageUri: Uri? = null
    lateinit var filePath: Uri
    private lateinit  var userName: TextView
    private lateinit  var userPhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        userName = findViewById<TextView>(R.id.edt_userName)
        userPhone = findViewById<TextView>(R.id.edt_PhoneNumber)
        userName.setText(SharedPreferanceObject.SBUserId)
        userPhone.setText(SharedPreferanceObject.phoneNumber)
        userImage = findViewById<View>(R.id.edtUserProfileImage)
        var imageRef:StorageReference = FirebaseStorage.getInstance().reference.child("profilePic/"+SharedPreferanceObject.SBUserId+".jpg")

        val localFile = File.createTempFile("profilePic", "jpg")

        imageRef.getFile(localFile).addOnSuccessListener {
            Glide.with(this)
                .load(localFile.absolutePath)
                .circleCrop()
                .into(userImage as ImageView)
        }.addOnFailureListener {
            Log.e("GabChat error:", "error when getting profile picture.")
        }


        userImage.setOnClickListener(){
            uploadImage()
        }
    }

    private fun uploadImage() {
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent,"Choose Picture"),111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data!= null) {
            filePath = data.data!!
            Glide.with(this)
                .load(filePath)
                .circleCrop()
                .into(userImage as ImageView)

            if(filePath != null){
                var pd = ProgressDialog(this)
                pd.setTitle("Uploading")
                pd.show()

                var imageRef:StorageReference = FirebaseStorage.getInstance().reference.child("profilePic/"+SharedPreferanceObject.SBUserId+".jpg")
                imageRef.putFile(filePath)
                    .addOnSuccessListener { p0->
                        pd.dismiss()
                        Toast.makeText(applicationContext,"File Uploaded", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener{ p0->
                        pd.dismiss()
                        Toast.makeText(applicationContext,p0.message, Toast.LENGTH_LONG).show()

                    }
                    .addOnProgressListener { p0->
                        var prograss:Double = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                        pd.setMessage("Uploading :"+ prograss.toInt() +"%")

                    }
            }

        }
    }
}