package com.assignment.gabchat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.assignment.gabchat.Interface.ChannelClickedListener
import com.assignment.gabchat.adapter.MembersChannelAdapter
import com.assignment.gabchat.dataclass.ChannelModel
import com.sendbird.android.GroupChannel


class ChatFragment : Fragment(),ChannelClickedListener {
    private lateinit var viewOfLayout: View
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    lateinit var recyclerView: RecyclerView
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        viewOfLayout =inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = viewOfLayout.findViewById<RecyclerView>(R.id.recycler_group_channels)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val channelList = GroupChannel.createMyGroupChannelListQuery()
        channelList.next { list, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (chat List):", it) }
            }
            var data = ArrayList<ChannelModel>()

            if(!list.isNullOrEmpty())
            {

                for (s in list) {
                    var name = s.members[0].userId.toString()
                    Log.e("GABCHAT :", "user:"+s.members[0].userId.toString()+"sp:"+SharedPreferanceObject.SBUserId)
                    if(s.members[0].userId.toString() == SharedPreferanceObject.SBUserId )
                    {
                        name =s.name.replace("]","").replace("[","")

                    }

                    data.add(ChannelModel(name,s.memberCount.toString(),s.url))
                }
            }


            val adapter = MembersChannelAdapter(context!!,this,data)

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