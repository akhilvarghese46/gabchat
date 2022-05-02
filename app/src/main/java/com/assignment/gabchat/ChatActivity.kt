package com.assignment.gabchat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.adapter.MessageAdapter
import com.sendbird.android.BaseChannel
import com.sendbird.android.GroupChannel
import com.sendbird.android.UserMessageParams
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    private val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"

    private lateinit var adapter: MessageAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatChannel: GroupChannel
    private lateinit var channelUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        InitializeValues()


        /*val back = button_gchat_back
   back.setOnClickListener {
       val intent = Intent(this, ChannelListActivity::class.java)
       startActivity(intent)
   }
*/

        button_gchat_send.setOnClickListener {
            sendMessage()
        }
    }


    override fun onResume() {
        super.onResume()
        channelUrl = getChannelURl()

        GroupChannel.getChannel(channelUrl,
            GroupChannel.GroupChannelGetHandler { groupChannel, e ->
                if (e != null) {
                    e.message?.let { Log.e("GABCHAT error (ChatActivity):", it) }

                }
                this.chatChannel = groupChannel
                getPrevMessages()
            })

    /*    SendBird.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : SendBird.ChannelHandler() {
                override fun onMessageReceived(
                    baseChannel: BaseChannel,
                    baseMessage: BaseMessage
                ){
                    if (baseChannel.url == channelUrl) {
                        // Add new message to view
                        adapter.addFirst(baseMessage)
                        groupChannel.markAsRead()
                    }
                }
            })*/
    }

    /*override fun onPause() {
        super.onPause()
        //SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
    }*/

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


}