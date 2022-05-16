package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {
    private var ccPicker: CountryCodePicker?=null
    private var countryCode:String?="353"
    private var countryName:String?=null

    private lateinit  var phoneNum: EditText
    private lateinit  var otpData:EditText
    private lateinit var btnOtpGen: Button

    private lateinit  var btnOtpVerify: Button
    private lateinit var firAuth: FirebaseAuth
    var firVerifyId: String? = null
    private lateinit var prgBar: ProgressBar

    private lateinit  var otpVerifyView: View
    private lateinit  var otpGenView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        ccPicker = findViewById(R.id.country_code_picker)
        ccPicker!!.setOnCountryChangeListener(this)
        ccPicker!!.setDefaultCountryUsingNameCode("IE")

        phoneNum = findViewById<EditText>(R.id.edt_phn_num)
        otpData = findViewById<EditText>(R.id.edt_otp)

        btnOtpGen = findViewById<Button>(R.id.btn_otpGen)
        btnOtpVerify = findViewById<Button>(R.id.btn_otpVerify)
        firAuth = FirebaseAuth.getInstance()
        prgBar = findViewById<ProgressBar>(R.id.progressBar)

        otpGenView = findViewById<View>(R.id.layout_otp_gen)
        otpVerifyView = findViewById<View>(R.id.layout_otp_verify)

        btnOtpGen.setOnClickListener{
            if (TextUtils.isEmpty(phoneNum.getText().toString())) {
                Toast.makeText(this@LoginActivity, "Enter Phone No.", Toast.LENGTH_SHORT).show()
            } else {
                val phonNumber: String = phoneNum.getText().toString()
                prgBar.setVisibility(View.VISIBLE)
                generateOTPVerification(phonNumber)
            }
        }

        btnOtpVerify.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(otpData.getText().toString())) {
                Toast.makeText(this@LoginActivity, "Wrong OTP Entered", Toast.LENGTH_SHORT).show()
            } else {
                verifycode(otpData.getText().toString())
            }
        })
    }

    override fun onCountrySelected() {
        countryCode=ccPicker!!.selectedCountryCode
        countryName=ccPicker!!.selectedCountryName
    }

    private fun generateOTPVerification(phonNumber: String) {
        Log.e("login activity phone numer:","+"+countryCode+phonNumber.toString())
        val options = PhoneAuthOptions.newBuilder(firAuth)
            .setPhoneNumber("+"+countryCode+phonNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private val callbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code = credential.smsCode
                code?.let { verifycode(it) }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("login activity error:","+"+e.toString())
                Toast.makeText(this@LoginActivity, "Verification Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                s: String,
                token: ForceResendingToken
            ) {
                super.onCodeSent(s, token)
                firVerifyId = s
                Toast.makeText(this@LoginActivity, "Code sent", Toast.LENGTH_SHORT).show()
                btnOtpVerify.setEnabled(true)
                otpVerifyView.setVisibility(View.VISIBLE)
                otpGenView.setVisibility(View.INVISIBLE)
                prgBar.setVisibility(View.INVISIBLE)
            }
        }

    private fun verifycode(code: String) {
        val credential = PhoneAuthProvider.getCredential(firVerifyId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(this@LoginActivity, "Login Successfull", Toast.LENGTH_SHORT).show()
                    startRegistrationActivity()
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null &&   SharedPreferanceObject.SBUserId != null ) {
            startMainActivity( SharedPreferanceObject.SBUserId.toString())
        }
    }

    private fun startMainActivity(userName: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("userName", userName)
        intent.putExtra("userNickName", userName)
        startActivity(intent)

    }

    fun startRegistrationActivity() {
        startActivity(Intent(this@LoginActivity, Registration::class.java))
        finish()
    }

}