package com.assignment.gabchat

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sendbird.android.SendBird
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class SettingActivity : AppCompatActivity() {

    private lateinit var userImage: View
    private val pickImage = 100
    private var userImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        userImage = findViewById<View>(R.id.edtUserProfileImage)

        //+ ProfileUrl
        val profileUrl = if (SendBird.getCurrentUser() != null) SendBird.getCurrentUser().profileUrl else ""
        if (profileUrl.length > 0) {
            Glide.with(this)
                .load(profileUrl)
                .circleCrop()
                .into(userImage as ImageView)

            Log.e("image url", profileUrl)

        }
        userImage.setOnClickListener(){
            uploadImage()
        }
    }

    private fun uploadImage() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
      /*  val options = arrayOf("Upload a photo", "Take a photo")

        val builder = AlertDialog.Builder(this@SettingActivity)
        builder.setTitle("Set profile image")
            .setItems(options) { dialog, which ->
                if (which == 0) {
                    checkPermissions(
                        com.sendbird.android.sample.main.SettingsActivity.MEDIA_MANDATORY_PERMISSIONS,
                        com.sendbird.android.sample.main.SettingsActivity.MEDIA_REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                } else if (which == 1) {
                    checkPermissions(
                        com.sendbird.android.sample.main.SettingsActivity.CAMERA_MANDATORY_PERMISSIONS,
                        com.sendbird.android.sample.main.SettingsActivity.CAMERA_REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }
            }
        builder.create().show()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            userImageUri = data?.data

            val uri: Uri? = data!!.data
            this.contentResolver.query(uri!!, null, null, null, null).use { cursor ->
                val mime = this.contentResolver.getType(uri)
                if (cursor != null) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val value =
                        Hashtable<String, Any?>()
                    if (cursor.moveToFirst()) {
                        var name = cursor.getString(nameIndex)
                        val size = cursor.getLong(sizeIndex).toInt()

                        Log.e("image url error n-----name----------", name)
                    }}}

            val sd = cacheDir
            val folder = File(sd, "/myfolder/")
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    Log.e("ERROR", "Cannot create a directory!")
                } else {
                    folder.mkdirs()
                }
            }

            val fileName = File(folder, "mypic.jpg")


                val outputStream = FileOutputStream(fileName.toString())

                outputStream.close()

            Log.e("image url error ---------------",fileName.path.toString() )

            Log.e("image url error ---------------",userImageUri!!.path.toString() )

           /* val nickname =
                if (SendBird.getCurrentUser() != null) SendBird.getCurrentUser().nickname else ""




            val uri = data!!.data
            //var userImage: File = File(uri!!.path)



            val info: Hashtable<String, Any?>? = getFileInfo(this@SettingActivity, uri!!)
            if (info != null) {
                val path = info["path"] as String?
                if (path != null) {
                    val profileImage = File(path)
                    if (profileImage.exists()) {






            val nickname =
                if (SendBird.getCurrentUser() != null) SendBird.getCurrentUser().nickname else ""
            SendBird.updateCurrentUserInfoWithProfileImage(nickname, profileImage,
                SendBird.UserInfoUpdateHandler { e ->
                    if (e != null) {
                        // Error!
                        Log.e("image url error", e.message.toString())
                    }
                    try {
                        Log.e("image url ssss", "jjj")
                        Glide.with(this)
                            .load(userImage)
                            .circleCrop()
                            .into(userImage as ImageView)

                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                })


                    }
                }
            }*/
        }
    }

    fun getFileInfo(context: Context, uri: Uri): Hashtable<String, Any?>? {
        try {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                val mime = context.contentResolver.getType(uri)
                if (cursor != null) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    val value =
                        Hashtable<String, Any?>()
                    if (cursor.moveToFirst()) {
                        var name = cursor.getString(nameIndex)
                        val size = cursor.getLong(sizeIndex).toInt()

                            name =
                                "Temp_" + uri.hashCode() + "." + extractExtension(context, uri)

                        val file = File(context.cacheDir, name)
                        val inputPFD =
                            context.contentResolver.openFileDescriptor(uri, "r")
                        var fd: FileDescriptor? = null
                        if (inputPFD != null) {
                            fd = inputPFD.fileDescriptor
                        }
                        val inputStream = FileInputStream(fd)
                        val outputStream = FileOutputStream(file)
                        var read: Int
                        val bytes = ByteArray(1024)
                        while (inputStream.read(bytes).also { read = it } != -1) {
                            outputStream.write(bytes, 0, read)
                        }
                        value["path"] = file.absolutePath
                        value["size"] = size
                        value["mime"] = mime
                        value["name"] = name
                    }
                    return value
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e(e.localizedMessage, "File not found.")
            return null
        }
        return null
    }

    fun extractExtension(context: Context, uri: Uri): String? {
        val extension: String
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) ({
            context.contentResolver.getType(uri)?.let { extractExtension(it) }
        }).toString() else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }
    fun extractExtension(mimeType: String): String? {
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(mimeType)
    }
}