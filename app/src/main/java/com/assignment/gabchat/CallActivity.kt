package com.assignment.gabchat

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.sendbird.calls.*
import com.sendbird.calls.handler.DirectCallListener
import org.webrtc.RendererCommon
import java.util.*


@Suppress("DEPRECATION")
class CallActivity : AppCompatActivity() {

    private var sbCallId: String? = null
    private var isCallAccept: Boolean  = false
    private var isCallDecline: Boolean = false

    lateinit var callStatus: TextView
    lateinit var callTime: TextView
    lateinit var otherUserName: TextView

    lateinit var btnEnd: View
    lateinit var callData: DirectCall
    lateinit var smallVideoUser: SendBirdVideoView
    lateinit var largeVideoOtherUser: SendBirdVideoView

    //Screen share
    private val SCREEN_CAPTURE_PERMISSION_REQUEST_CODE: Int = 1
    lateinit var btnScreenShare: View
    lateinit var btnScreenShareEnd: View
    lateinit var mediaManager: MediaProjectionManager

    private lateinit var ssDetect: ScreenShortDetectionManager

    var isVideoCall: Boolean = false
    var callId: String? = null
    lateinit var fcmCallAccepted: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isVideoCall = this.intent.getBooleanExtra("isVideoCall", false)
        setContentView(R.layout.activity_call)

        ssDetect = ScreenShortDetectionManager(baseContext)
        var calleeID = this.intent?.getStringExtra("calleeID")
        ssDetect.calleeId = calleeID.toString()

        callStatus = findViewById(R.id.text_Status)
        callTime = findViewById(R.id.txt_call_time)
        btnEnd = findViewById(R.id.btn_Call_end)
        otherUserName = findViewById(R.id.txt_userId)
        smallVideoUser = findViewById(R.id.videocall_userown)
        largeVideoOtherUser = findViewById(R.id.videocall_otheruser)
        btnScreenShare = findViewById(R.id.btn_Calbtn_screenshare)
        btnScreenShareEnd = findViewById(R.id.btn_Calbtn_screenshare_end)

        largeVideoOtherUser.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        largeVideoOtherUser.setZOrderMediaOverlay(false)
        largeVideoOtherUser.setEnableHardwareScaler(true)

        smallVideoUser.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
        smallVideoUser.setZOrderMediaOverlay(true)
        smallVideoUser.setEnableHardwareScaler(true)

        sbCallId = this.intent?.getStringExtra("SB_CALLID")
        callId = this.intent?.getStringExtra("CALL_ID")


        fcmCallAccepted = this.intent.getStringExtra("isfcmAccepted").toString()

        if (sbCallId == null && SharedPreferanceObject.SB_CALL_ID_FCM != null) {
            sbCallId = SharedPreferanceObject.SB_CALL_ID_FCM
        }
        if (sbCallId != null) {
            stopFcmCallServices()
            val call = SendBirdCall.getCall(sbCallId!!)!!
           // callData = call.apply { setListener(directCallListener) }
            callData = setDirectCallListener(call)
        } else {
            Toast.makeText(this, "Call is Failed...", Toast.LENGTH_LONG).show()
            this.finish()
            return

        }
        mediaManager = this.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager


        if(!isVideoCall) {
            isVideoCall = callData.isVideoCall
        }

        if (callData.myRole == DirectCallUserRole.CALLER) {
            callStatus.text = "CALLING"
        } else {
            callStatus.text = "RINGING"
        }
        otherUserName.text = callData.remoteUser!!.userId

        if (!isVideoCall) {
            smallVideoUser.visibility = View.INVISIBLE
            largeVideoOtherUser.visibility = View.INVISIBLE
        } else {
            btnScreenShare.visibility =View.VISIBLE
            callData.setLocalVideoView(smallVideoUser)
            callData.setRemoteVideoView(largeVideoOtherUser)
        }

        if (fcmCallAccepted == "false") {
            isCallAccept = false
            isCallDecline = true
        } else if (fcmCallAccepted == "true") {
            isCallAccept = true
            isCallDecline = false
        }

        if (isCallAccept) {
            val callOptions = CallOptions()
                .setLocalVideoView(smallVideoUser)
                .setRemoteVideoView(largeVideoOtherUser)
                .setVideoEnabled(true)
                .setAudioEnabled(true)
            callData.accept(AcceptParams().setCallOptions(callOptions))

        } else if (isCallDecline) {
            callData.end()
        }

        btnEnd.setOnClickListener {
            callData.end()
            this.finish()
            return@setOnClickListener
        }

        btnScreenShare.setOnClickListener {
            startScreenSharing()
            btnScreenShare.visibility =View.INVISIBLE
            btnScreenShareEnd.visibility =View.VISIBLE

        }

        btnScreenShareEnd.setOnClickListener{
            stopScreenShare()
            stopScreenShareService()
        }


    }

    private fun startScreenSharing() {

        if (!callData.isLocalScreenShareEnabled) {
            startScreenShareService()
            mediaManager = this.application.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            if (mediaManager != null) {
                startActivityForResult(mediaManager.createScreenCaptureIntent(), SCREEN_CAPTURE_PERMISSION_REQUEST_CODE)
            }

        } else {
            callData.stopScreenShare { e ->
                if (e == null) {
                    Toast.makeText(this@CallActivity, "Start Screen Share...", Toast.LENGTH_SHORT).show()
                }
                stopScreenShareService()
            }

        }
    }

    private fun stopScreenShare() {
        callData.stopScreenShare {
            btnScreenShare.visibility =View.VISIBLE
            btnScreenShareEnd.visibility =View.INVISIBLE
        }
    }



    //ScreenShare
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCREEN_CAPTURE_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                shareMyScreenAfterAcceptingPermission(data!!)
            } else {
                stopScreenShareService()
            }
        }
    }

    private fun shareMyScreenAfterAcceptingPermission(screenCaptureIntent: Intent) {
        if (callData == null) {
            return
        }
        callData.startScreenShare(screenCaptureIntent) { e ->
            if (e != null) {
                e.printStackTrace()
                Toast.makeText(this, "Error starting screen share", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Screen sharing in progress", Toast.LENGTH_LONG).show()
            }
        }
    }


    //Call Listener handle the direct calls
    private fun setDirectCallListener(call: DirectCall): DirectCall {
        call.setListener(object : DirectCallListener() {
             var calltimer: Timer = Timer()
             override fun onEstablished(call: DirectCall) {
                 callStatus.text ="CONNECTING"
                 calltimer.schedule(object : TimerTask() {
                     override fun run() {
                         callTime.text = "${callData.duration.div(1000)}s"
                     }
                 }, 1000, 1000)
             }

             override fun onReconnected(call: DirectCall) {
                 callStatus.text ="CONNECTED"
             }

             override fun onReconnecting(call: DirectCall) {
                 callStatus.text ="RECONNECTING"
             }

             override fun onConnected(call: DirectCall) {
                 callStatus.text ="CONNECTED"
             }

             override fun onEnded(call: DirectCall) {

                 callStatus.text ="END"
                 calltimer.cancel()
                 stopFcmCallServices()
                 stopScreenShareService()
                 getMainActivity()
             }

         })
        return call
     }

/*
    private val directCallListener: DirectCallListener = object : DirectCallListener() {
        var calltimer: Timer = Timer()
        override fun onEstablished(call: DirectCall) {
            callStatus.text ="CONNECTING"
            calltimer.schedule(object : TimerTask() {
                override fun run() {
                    callTime.text = "${callData.duration.div(1000)}s"
                }
            }, 1000, 1000)
        }

        override fun onReconnected(call: DirectCall) {
            callStatus.text ="CONNECTED"
        }

        override fun onReconnecting(call: DirectCall) {
            callStatus.text ="RECONNECTING"
        }

        override fun onConnected(call: DirectCall) {
            callStatus.text ="CONNECTED"
        }

        override fun onEnded(call: DirectCall) {
            callStatus.text ="END"
            calltimer.cancel()
            stopScreenShareService()
            getMainActivity()
        }
    }
*/

    private fun startScreenShareService() {
        val serviceIntent = Intent(this, GabChatScreenShareService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        //this.let { it.startService(Intent(it, ExampleService::class.java)) }
    }

    private fun stopScreenShareService() {
        val serviceIntent = Intent(this, GabChatScreenShareService::class.java)
        stopService(serviceIntent)
        // this?.let { it.stopService(Intent(it, GabChatScreenShareService::class.java)) }
    }

    private fun stopFcmCallServices() {
        val serviceIntent = Intent(this, FirebaseMessageReceiver::class.java)
        stopService(serviceIntent)
        // this?.let { it.stopService(Intent(it, GabChatScreenShareService::class.java)) }
    }
    private fun getMainActivity() {
        stopScreenShareService()
        this.finish()
        return
        //val intent = Intent(this, MainActivity::class.java)
       // startActivity(intent)
    }


    //Screenshort detection
    override fun onStart() {
        super.onStart()
        ssDetect.start()
    }

    override fun onStop() {
        super.onStop()
        ssDetect.stop()
    }
}