package com.assignment.gabchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.Interface.ChannelClickedListener
import com.assignment.gabchat.adapter.MembersChannelAdapter
import com.assignment.gabchat.dataclass.ChannelModel
import com.sendbird.android.GroupChannel


class ChatFragment : Fragment(),ChannelClickedListener {
    private lateinit var viewOfLayout: View
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     //   return inflater.inflate(R.layout.fragment_chat, container, false)

        viewOfLayout =inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = viewOfLayout.findViewById<RecyclerView>(R.id.recycler_group_channels)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val channelList = GroupChannel.createMyGroupChannelListQuery()
        channelList.next { list, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (chatList):", it) }
            }
            var data = ArrayList<ChannelModel>()

            if(!list.isNullOrEmpty())
            {
                for (s in list) {
                    data.add(ChannelModel(s.members[0].nickname.toString(),s.memberCount.toString(),s.url))
                }
            }


            val adapter = MembersChannelAdapter(this,data)

            recyclerView.adapter = adapter

        }



        return viewOfLayout
    }


    override fun onChannelListener(channel: ChannelModel) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra(EXTRA_CHANNEL_URL, channel.channelUrl)
        intent.putExtra("calleeId", channel.userName)
        startActivity(intent)
    }



}