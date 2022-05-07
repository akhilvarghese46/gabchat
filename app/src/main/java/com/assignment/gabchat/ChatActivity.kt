package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.adapter.MessageAdapter
import com.sendbird.android.*
import com.sendbird.calls.DialParams
import com.sendbird.calls.DirectCall
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.DialHandler
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    private val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"

    private lateinit var adapter: MessageAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatChannel: GroupChannel
    private lateinit var channelUrl: String
    private lateinit var calleeID:String
    private var isVideoCall:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        InitializeValues()
        calleeID = this.intent.getStringExtra("calleeId").toString()



        /*val back = button_gchat_back
   back.setOnClickListener {
       val intent = Intent(this, ChannelListActivity::class.java)
       startActivity(intent)
   }
*/
      /*  btn_voicecall.setOnClickListener {
            isVideoCall= false
            authenticateUser()

        }
        btn_videocall.setOnClickListener {

        }*/
        button_gchat_send.setOnClickListener {
            sendMessage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)


        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.audioCall -> {
                isVideoCall= false
               dialPhoneCall()
                //getCallActivity(null)
                return true
            }
            R.id.videoCall -> {

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        channelUrl = getChannelURl()

        GroupChannel.getChannel(channelUrl, GroupChannel.GroupChannelGetHandler { groupChannel, e ->
                if (e != null) {
                    e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }

                }
                this.chatChannel = groupChannel
                getPrevMessages()
            })

       SendBird.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : SendBird.ChannelHandler() {
                override fun onMessageReceived(
                    baseChannel: BaseChannel,
                    baseMessage: BaseMessage
                ){
                    if (baseChannel.url == channelUrl) {
                        // Add new message to view
                        adapter.addFirst(baseMessage)
                        chatChannel.markAsRead()
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
    }

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
        val params = UserMessageParams()
            .setMessage(edit_gchat_message.text.toString())
        chatChannel.sendUserMessage(params,
            BaseChannel.SendUserMessageHandler { userMessage, e ->
                if (e != null) {    // Error.
                    return@SendUserMessageHandler
                }
                adapter.addFirst(userMessage)
                edit_gchat_message.text.clear()
            })
    }



    private fun getPrevMessages() {

        val prevMsgList = chatChannel.createPreviousMessageListQuery()

        prevMsgList.load(100, true) { messages, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }
            }
            adapter.loadMessages(messages!!)
        }

    }


    private fun InitializeValues() {
        adapter = MessageAdapter(this)
        chatRecyclerView = recycler_gchat
        chatRecyclerView.adapter = adapter
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.scrollToPosition(0)

    }


    private fun getChannelURl(): String {
        val intent = this.intent
        return intent.getStringExtra(EXTRA_CHANNEL_URL).toString()
    }


    //Call initialization and dialing

    /*
    private fun authenticateUser() {
        //dialPhoneCall()

      /*  val USER_ID = "pixel"
        val ACCESS_TOKEN: String? = null

        val params = AuthenticateParams(USER_ID)
            .setAccessToken(ACCESS_TOKEN)

        SendBirdCall.authenticate(params, AuthenticateHandler { user, e ->
            if (e != null) {
                e.printStackTrace()
                e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }

            } else {

                dialPhoneCall()

            }
        })*/

       /* SendBirdCall.authenticate(AuthenticateParams(userId)
        ) { user, e ->
            if (e == null) {
                dialPhoneCall()
            } else {
                e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }
            }
        }*/




    }
*/
    private fun dialPhoneCall() {
        val callData = SendBirdCall.dial(DialParams(calleeID).setVideoCall(isVideoCall), object : DialHandler {
            override fun onResult(call: DirectCall?, e: SendBirdException?) {
                if (e == null) {
                    Log.e("GABCHAT error (diall CollerID):", call!!.callId.toString())
                    // The call has been created successfully.
                    getCallActivity(call!!.callId)
                }
                else{
                    e.printStackTrace()
                    e.message?.let { Log.e("GABCHAT error (diall error):", it) }
                }

            }
        })
        /*
        callData!!.setListener(object : DirectCallListener() {
            override fun onEstablished(call: DirectCall) {

            }

            override fun onConnected(call: DirectCall) {

            }

            override fun onEnded(call: DirectCall) {


            }
        })*/
    }

    private fun getCallActivity(callId: String?) {
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("SB_CALLID", callId)
        startActivity(intent)
    }

}