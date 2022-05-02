package com.assignment.gabchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.Interface.ChannelClickedListener
import com.assignment.gabchat.R
import com.assignment.gabchat.dataclass.ChannelModel


class MembersChannelAdapter(private val channelClickListener: ChannelClickedListener, private val mList: List<ChannelModel>) : RecyclerView.Adapter<MembersChannelAdapter.ViewHolder>() {



    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_list_view, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.membersName.text = ItemsViewModel.userName

        holder.itemView.setOnClickListener {
            channelClickListener.onChannelListener(ItemsViewModel)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
       // val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val membersName: TextView = itemView.findViewById(R.id.txt_membername)


    }
}