package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.Interface.AddContactClickedListener
import com.assignment.gabchat.adapter.AddContactAdapter
import com.assignment.gabchat.dataclass.ChannelModel
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import com.sendbird.android.SendBird

class ContactActivity : AppCompatActivity() , AddContactClickedListener {


    private lateinit var menmbersRecyclerView: RecyclerView
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        menmbersRecyclerView = findViewById<RecyclerView>(R.id.recycler_group_channels)
        menmbersRecyclerView.layoutManager = LinearLayoutManager(this)

        val userListQuery = SendBird.createApplicationUserListQuery()

        userListQuery.next() { list, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (chatList):", it) }
            } else {
                var data = ArrayList<ChannelModel>()

                if (!list.isNullOrEmpty()) {
                    for (s in list) {
                        data.add(ChannelModel(s.userId, s.nickname, "url"))
                    }
                }

                val adapter = AddContactAdapter(this, data)

                menmbersRecyclerView.adapter = adapter
            }

        }
    }

    override fun onAddContactListener(channel: ChannelModel) {
        var userid = listOf(channel.userName)
        createChannel(userid)
    }
    private fun createChannel(users:List<String>) {
        val params = GroupChannelParams()

        val operatorId = ArrayList<String>()
        operatorId.add(SendBird.getCurrentUser().userId)

        params.addUserIds(users)
        params.setOperatorUserIds(operatorId)

        GroupChannel.createChannel(params) { groupChannel, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (chatList):", it) }
            } else {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra(EXTRA_CHANNEL_URL, groupChannel.url)
                startActivity(intent)
            }
        }
    }


}