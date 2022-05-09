package com.assignment.gabchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.ConstantValues.SendBirdConstantValues
import com.assignment.gabchat.R
import com.assignment.gabchat.TimeMange
import com.sendbird.android.BaseMessage
import com.sendbird.android.SendBird
import com.sendbird.android.UserMessage
import kotlinx.android.synthetic.main.item_recive_message.view.*
import kotlinx.android.synthetic.main.item_send_message.view.*

class MessageAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val userMessage = SendBirdConstantValues.USER_MESSAGE
    private val otherMessage = SendBirdConstantValues.USER_MESSAGE_OTHER

    private var messageData: MutableList<BaseMessage> = ArrayList()
    private var context: Context = context
    private var userId: String = SendBird.getCurrentUser().userId


    fun loadPrevMsgs(messages: MutableList<BaseMessage>) {
        this.messageData = messages
        notifyDataSetChanged()
    }

    fun addNewMessage(message: BaseMessage) {
        messageData.add(0, message)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            userMessage -> {
                UserViewHolder(layoutInflater.inflate(R.layout.item_send_message, parent, false))
            }
            otherMessage -> {
                OtherUserViewHolder(
                    layoutInflater.inflate(
                        R.layout.item_recive_message,
                        parent,
                        false
                    )
                )
            }
            else ->
                UserViewHolder(layoutInflater.inflate(R.layout.item_send_message, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {

        when (val msg = messageData.get(position)) {
            is UserMessage -> {
                if (msg.sender.userId == userId)
                    return userMessage
                else
                    return otherMessage
            }
            else -> return -1
        }
    }

    override fun getItemCount() = messageData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            userMessage -> {
                holder as UserViewHolder
                holder.bindView(context, messageData[position] as UserMessage)
            }
            otherMessage -> {
                holder as OtherUserViewHolder
                holder.bindView(context, messageData[position] as UserMessage)
            }
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var timeMang = TimeMange()
        val txtMsg = view.txt_user_msg
        val date = view.txt_date
        val msgDate = view.text_gchat_timestamp_me

        fun bindView(context: Context, msg: UserMessage) {
            date.visibility = View.VISIBLE
            txtMsg.setText(msg.message)
            msgDate.text = timeMang.time(msg.createdAt)
            date.text = timeMang.date(msg.createdAt)
        }
    }

    class OtherUserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var timeMang = TimeMange()

        val txtMsg = view.text_gchat_message_other
        val date = view.text_gchat_date_other
        val timestamp = view.text_gchat_timestamp_other
       // val profileImage = view.image_gchat_profile_other
       // val user = view.text_gchat_user_other


        fun bindView(context: Context, message: UserMessage) {
            date.visibility = View.VISIBLE
            txtMsg.setText(message.message)

            timestamp.text = timeMang.time(message.createdAt)


            date.text = timeMang.date(message.createdAt)

           // Glide.with(context).load(message.sender.profileUrl)
               // .apply(RequestOptions().override(75, 75))
               // .into(profileImage)
            //user.text = message.sender.nickname

        }

    }



}