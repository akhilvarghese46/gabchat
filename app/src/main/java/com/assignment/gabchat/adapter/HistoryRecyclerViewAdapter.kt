package com.assignment.gabchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.R
import com.sendbird.calls.DirectCallLog
import com.sendbird.calls.DirectCallUser
import com.sendbird.calls.DirectCallUserRole
import java.text.SimpleDateFormat
import java.util.*


class HistoryRecyclerViewAdapter(private var context: Context) : RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryViewHolder?>() {

    private val mDirectCallLogs: MutableList<DirectCallLog> = ArrayList()

    fun setCallLogs(callLogs: List<DirectCallLog>?) {
        mDirectCallLogs.clear()
        mDirectCallLogs.addAll(callLogs!!)
    }


    fun getDateString(timeMs: Long): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd H:mm", Locale.getDefault())
        val dateString = simpleDateFormat.format(Date(timeMs))
        return dateString.toLowerCase()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_call_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val callLog = mDirectCallLogs[position]
        val myRole = callLog.myRole
        val user: DirectCallUser?
        if (myRole == DirectCallUserRole.CALLER) {
            user = callLog.callee
            if (callLog.isVideoCall) {
                holder.imageViewIncomingOrOutgoing.setBackgroundResource(R.drawable.incomming_calls)
                holder.imgVideoAndAudio.setBackgroundResource(R.drawable.video_call)
            } else {
                holder.imageViewIncomingOrOutgoing.setBackgroundResource(R.drawable.outgoing_calls)
                holder.imgVideoAndAudio.setBackgroundResource(R.drawable.phone_dial)
            }
        } else {
            user = callLog.caller
            if (callLog.isVideoCall) {
                holder.imageViewIncomingOrOutgoing.setBackgroundResource(R.drawable.incomming_calls)
                holder.imgVideoAndAudio.setBackgroundResource(R.drawable.video_call)
            } else {
                holder.imageViewIncomingOrOutgoing.setBackgroundResource(R.drawable.outgoing_calls)
                holder.imgVideoAndAudio.setBackgroundResource(R.drawable.phone_dial)
            }
        }

        holder.textViewUserId.text= user!!.userId.toString()
        holder.textViewStartAt.text = getDateString(callLog.startedAt)

    }

    override fun getItemCount(): Int {
        return mDirectCallLogs.size
    }

     class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIncomingOrOutgoing: ImageView
       // val imageViewProfile: ImageView
       // val textViewNickname: TextView
         val textViewUserId: TextView
        //val textViewEndResultAndDuration: TextView
        val textViewStartAt: TextView
        val imgVideoAndAudio: ImageView


        init {
            imageViewIncomingOrOutgoing = itemView.findViewById(R.id.img_call_way)
             textViewUserId = itemView.findViewById(R.id.txt_username)
           // textViewEndResultAndDuration = itemView.findViewById(R.id.)
            textViewStartAt = itemView.findViewById(R.id.txt_date)
            imgVideoAndAudio = itemView.findViewById(R.id.img_audio_video)

        }
    }


}
