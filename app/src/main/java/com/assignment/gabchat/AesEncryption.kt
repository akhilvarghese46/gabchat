package com.assignment.gabchat

import android.util.Log
import com.assignment.gabchat.baseapplication.BaseApplication
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private var secretKey: SecretKeySpec? = null
private lateinit var key: ByteArray

class AesEncryption {

    // set Key for the messaging
    fun setSecertKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            Log.e("GABCHAT error (set secert key):", e.message.toString())
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.e("GABCHAT error (set secert key catch):", e.message.toString())
        }
    }

    //it encrypt the message
    fun encryption(strToEncrypt: String): String? {
        val msgKey = BaseApplication.MESSAGE_SECERT_KEY
        try {
            setSecertKey(msgKey)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder()
                .encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {
            Log.e("GABCHAT error (Error while encrypting msg:):", e.toString())
        }
        return null
    }

    //it decrypt the message the message
    fun decryption(strToDecrypt: String?): String? {
        val msgKey = BaseApplication.MESSAGE_SECERT_KEY
        try {
            setSecertKey(msgKey)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: Exception) {
            Log.e("GABCHAT error (Error while Decrypting msg:):", e.toString())
        }
        return null
    }
}