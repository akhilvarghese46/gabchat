package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assignment.gabchat.ConstantValues.SendBirdConstantValues
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.sendbird.calls.AcceptParams
import com.sendbird.calls.CallOptions
import com.sendbird.calls.DirectCall
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.handler.DirectCallListener
import java.lang.Boolean.getBoolean
import java.util.*

class CallActivity : AppCompatActivity() {

    private var sbCallId: String? =  null
    private var isCallAccept:Boolean =  getBoolean(SendBirdConstantValues.CALL_IS_ACCEPTED)
    private var isCallDecline:Boolean =  getBoolean(SendBirdConstantValues.CALL_IS_DECLINED)
    lateinit var callStatus: TextView
    lateinit var endButton: Button
    lateinit var callData: DirectCall



    var callId:String?  =  null
    var isAccepted:Boolean = false
    var isDeclined:Boolean = false
    lateinit var fcmCallAccepted: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        Log.e("CALL_ID Gab insidr sp", "CALL_ID :" +  SharedPreferanceObject.SB_CALL_ID_FCM)

        callStatus = findViewById(R.id.text_Status)
        endButton = findViewById(R.id.btn_Call_end)
        sbCallId =this?.intent?.getStringExtra("SB_CALLID")

        callId = this?.intent?.getStringExtra("CALL_ID")
        fcmCallAccepted= this.intent.getStringExtra("isfcmAccepted").toString()
        Log.e("CALL_ID Gab sbCallId ", "fcmCallAccepted :" +  fcmCallAccepted)


        Log.e("CALL_ID Gab sbCallId ", "sbCallId :" +  sbCallId)
        if(sbCallId == null && SharedPreferanceObject.SB_CALL_ID_FCM != null) {
            sbCallId = SharedPreferanceObject.SB_CALL_ID_FCM
        }
        if(sbCallId != null ) {
            val call = SendBirdCall.getCall(sbCallId!!)!!

            callData = call.apply { setListener(directCallListener) }
            //setDirectCallListener(callData)

        }
        else{

            Toast.makeText(this, "Call is Failed", Toast.LENGTH_LONG).show()
            getMainActivity()
        }


        Log.e("CALL_ID Gab isDeclined FCM ", "isDeclined :" +  isCallDecline)


        if( fcmCallAccepted == "false") {
            isCallAccept = false
            isCallDecline = true
        }
        else if(fcmCallAccepted == "true"){
            isCallAccept = true
            isCallDecline = false
        }



        if (isCallAccept) {
            callData.accept(
                AcceptParams()
                    .setCallOptions(
                        CallOptions()
                    )
            )
        } else if (isCallDecline) {
            callData.end()
        }
    }

    //Call Listener handle the direct calls
   /* private fun setDirectCallListener(callData: DirectCall?) {
        callData!!.setListener(object : DirectCallListener() {
            override fun onEstablished(call: DirectCall) {
                callStatus.text  = "connectingkk"

            }

            override fun onConnected(call: DirectCall) {

            }

            override fun onEnded(call: DirectCall) {
               /* SharedPreferanceObject.SB_CALL_ID_FCM = null
                SharedPreferanceObject.SB_IS_ACCEPTED_FCM = false
                SharedPreferanceObject.SB_IS_DECLINED_FCM = false
                getMainActivity()*/

            }
            override fun onReconnected(call: DirectCall) {

            }

            override fun onReconnecting(call: DirectCall) {

            }

        })
    }
*/

    private val directCallListener: DirectCallListener = object : DirectCallListener() {
        var durationTimer: Timer? = null
        override fun onEstablished(call: DirectCall) {
           /* statusTextView.text = CallStatus.CONNECTING.toString()
            durationTimer = Timer()
            durationTimer?.schedule(object : TimerTask() {
                override fun run() {
                    durationTextView.text = "${directCall.duration.div(1000)}s"
                }
            }, 1000, 1000)*/
        }

        override fun onReconnected(call: DirectCall) {
           // statusTextView.text = CallStatus.CONNECTED.toString()
        }

        override fun onReconnecting(call: DirectCall) {
            //statusTextView.text = CallStatus.RECONNECTING.toString()
        }

        override fun onConnected(call: DirectCall) {
            //statusTextView.text = CallStatus.CONNECTED.toString()
        }

        override fun onEnded(call: DirectCall) {
          //  statusTextView.text = CallStatus.ENDED.toString()
            durationTimer?.cancel()
            durationTimer = null
            Handler(Looper.getMainLooper()).postDelayed({
                //findNavController().navigateUp()
            }, 1000)
        }
    }


    private fun getMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}