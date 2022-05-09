package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.ConstantValues.SendBirdConstantValues
import com.assignment.gabchat.adapter.MessageAdapter
import com.sendbird.android.*
import com.sendbird.calls.DialParams
import com.sendbird.calls.SendBirdCall
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val EXTRA_CHANNEL_URL = SendBirdConstantValues.EXTRA_CHANNEL_URL
    private val CHANNEL_HANDLER_ID = SendBirdConstantValues.CHANNEL_HANDLER_ID

    private lateinit var adapterMsg: MessageAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatChannel: GroupChannel
    private lateinit var currentChannelUrl: String
    private lateinit var calleeID:String
    private var isVideoCall:Boolean = false

    private lateinit var txtMsg:EditText
    lateinit var btnSendMsg: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        txtMsg  = findViewById(R.id.txt_message)
        btnSendMsg = findViewById(R.id.btn_send)
        calleeID = this.intent.getStringExtra("calleeId").toString()
        InitializeValues()
        getChannelData()
        btnSendMsg.setOnClickListener {
            sendMessage()
        }
    }

    private fun getChannelData() {
        //get into chatting channel
        currentChannelUrl = getChannelUrl()
        GroupChannel.getChannel(currentChannelUrl) { channelData, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }
            }
            this.chatChannel = channelData
            getPrevMessages()
        }

        //channel handler works when gets msgs. it helps to recive msg on other side
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, object : SendBird.ChannelHandler() {
                override fun onMessageReceived(bs: BaseChannel, baseMessage: BaseMessage){
                    if (bs.url == currentChannelUrl) {
                        adapterMsg.addNewMessage(baseMessage)
                        chatChannel.markAsRead()
                    }
                }
            })
    }

    //Create option menu for video and audio call
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.audioCall -> {
                isVideoCall= false
                dialPhoneCall()
                return true
            }
            R.id.videoCall -> {
                isVideoCall= true
                dialPhoneCall()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



/*
    override fun onPause() {
        super.onPause()
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
    }
*/
   /* private fun setButtonListeners() {
        /*val back = button_gchat_back
        back.setOnClickListener {
            val intent = Intent(this, ChannelListActivity::class.java)
            startActivity(intent)
        }
*/
        val send = button_gchat_send
        send.setOnClickListener {
            sendMessage()
        }
    }

*/
    private fun sendMessage()
    {
        val msgParam = UserMessageParams().setMessage(txtMsg.text.toString())
        chatChannel.sendUserMessage(msgParam,
            BaseChannel.SendUserMessageHandler { msg, e ->
                if (e != null) {
                    e.message?.let { Log.e("GABCHAT error (sendmessage):", it) }
                    return@SendUserMessageHandler
                }
                adapterMsg.addNewMessage(msg)
                txtMsg.text.clear()
            })
    }



    private fun getPrevMessages() {
        val prevMsgList = chatChannel.createPreviousMessageListQuery()
        prevMsgList.load(100, true) { messages, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (previous message):", it) }
            }
            adapterMsg.loadPrevMsgs(messages!!)
        }

    }


    private fun InitializeValues() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        adapterMsg = MessageAdapter(this)
        chatRecyclerView = recycler_message_data
        chatRecyclerView.adapter = adapterMsg

        layoutManager.reverseLayout = true
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.scrollToPosition(0)

    }


    private fun getChannelUrl(): String {
        val intent = this.intent
        return intent.getStringExtra(EXTRA_CHANNEL_URL).toString()
    }



    private fun dialPhoneCall() {
        val callData = SendBirdCall.dial(DialParams(calleeID).setVideoCall(isVideoCall)
        ) { call, e ->
            if (e == null) {
                Log.e("GABCHAT The call has been created successfully.", call!!.callId.toString())
                getCallActivity(call!!.callId, isVideoCall)
            } else {
                e.printStackTrace()
                e.message?.let { Log.e("GABCHAT error (diall error):", it) }
            }
        }

    }

    private fun getCallActivity(callId: String?, isVideoCall: Boolean) {
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("SB_CALLID", callId)
        intent.putExtra("isVideoCall", isVideoCall)
        startActivity(intent)
    }

}