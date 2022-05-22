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

class CallHistoryAdapter(private var context: Context) : RecyclerView.Adapter<CallHistoryAdapter.callHistoryHolder?>() {

    var callLog: MutableList<DirectCallLog> = ArrayList()
    fun getCallLogo(callLogs: List<DirectCallLog>?) {
        callLog.clear()
        callLog.addAll(callLogs!!)
    }

    fun getDateString(timeMs: Long): String? {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy H:mm", Locale.getDefault())
        val dateString = simpleDateFormat.format(Date(timeMs))
        return dateString.toLowerCase()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): callHistoryHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_call_history, parent, false)
        return callHistoryHolder(itemView)
    }

    override fun onBindViewHolder(holder: callHistoryHolder, pos: Int) {
        val callLog = callLog[pos]
        val userRole = callLog.myRole
        val user: DirectCallUser?
        if (userRole == DirectCallUserRole.CALLER) {
            user = callLog.callee
            if (callLog.isVideoCall) {
                holder.imgCallWay.setBackgroundResource(android.R.drawable.sym_call_incoming)
                holder.imgVideAudio.setBackgroundResource(R.drawable.video_call)
            } else {
                holder.imgCallWay.setBackgroundResource(android.R.drawable.sym_call_outgoing)
                holder.imgVideAudio.setBackgroundResource(R.drawable.phone_dial)
            }
        } else {
            user = callLog.caller
            if (callLog.isVideoCall) {
                holder.imgCallWay.setBackgroundResource(android.R.drawable.sym_call_incoming)
                holder.imgVideAudio.setBackgroundResource(R.drawable.video_call)
            } else {
                holder.imgCallWay.setBackgroundResource(android.R.drawable.sym_call_outgoing)
                holder.imgVideAudio.setBackgroundResource(R.drawable.phone_dial)
            }
        }
       // holder.bindView(context,user!!.userId.toString())
        holder.txtUserId.text= user!!.userId.toString()
        holder.txtDate.text = getDateString(callLog.startedAt)

    }

    override fun getItemCount(): Int {
        return callLog.size
    }

     class callHistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCallWay: ImageView = itemView.findViewById(R.id.img_call_way)
        val txtUserId: TextView = itemView.findViewById(R.id.txt_username)
        val txtDate: TextView  = itemView.findViewById(R.id.txt_date)
        val imgVideAudio: ImageView = itemView.findViewById(R.id.img_audio_video)
         val proPic: ImageView = itemView.findViewById(R.id.img_user)

        /* fun bindView(context: Context, userName: String) {
             var imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("profilePic/"+userName+".jpg")
             if(imageRef!= null) {
                 val localFile = File.createTempFile("profilePic", "jpg")

                 imageRef.getFile(localFile).addOnSuccessListener {
                     Glide.with(context)
                         .load(localFile.absolutePath)
                         .circleCrop()
                         .into(proPic as ImageView)
                 }.addOnFailureListener {
                     Log.e("GabChat error:", "error when getting profile picture.")
                 }
             }
         }*/

       /* init {
            imgCallWay = itemView.findViewById(R.id.img_call_way)
            txtUserId = itemView.findViewById(R.id.txt_username)
            txtDate = itemView.findViewById(R.id.txt_date)
            imgVideAudio = itemView.findViewById(R.id.img_audio_video)

        }*/
    }


}
