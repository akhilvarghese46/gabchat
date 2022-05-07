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
import com.assignment.gabchat.Interface.AddContactClickedListener
import com.assignment.gabchat.adapter.AddContactAdapter
import com.assignment.gabchat.dataclass.ChannelModel
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import com.sendbird.android.SendBird


class ContactFragment : Fragment() , AddContactClickedListener {
    private lateinit var viewOfLayout: View
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_contact, container, false)


        viewOfLayout =inflater.inflate(R.layout.fragment_contact, container, false)
        recyclerView = viewOfLayout.findViewById<RecyclerView>(R.id.recycler_group_channels)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val userListQuery = SendBird.createApplicationUserListQuery()

        userListQuery.next() { list, e ->
            if (e != null) {
                e.message?.let { Log.e("GABCHAT error (contact list):", it) }
            } else {
                var data = ArrayList<ChannelModel>()

                if(!list.isNullOrEmpty())
                {
                    for (s in list) {
                        data.add(ChannelModel(s.userId,s.nickname,"url"))
                    }
                }

                val adapter = AddContactAdapter(this,data)

                recyclerView.adapter = adapter
            }
        }




        return viewOfLayout
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
                e.message?.let { Log.e("GABCHAT error (contact create frg):", it) }
            } else {
                val intent = Intent(activity, ChatActivity::class.java)
                intent.putExtra(EXTRA_CHANNEL_URL, groupChannel.url)
                startActivity(intent)
            }
        }
    }

}