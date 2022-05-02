package com.assignment.gabchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sendbird.android.BaseMessage
import com.sendbird.android.SendBird
import com.sendbird.android.UserMessage
import kotlinx.android.synthetic.main.item_send_message.view.*
import kotlinx.android.synthetic.main.item_recive_message.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val VIEW_TYPE_USER_MESSAGE_ME = 10
    private val VIEW_TYPE_USER_MESSAGE_OTHER = 11

    private var messages: MutableList<BaseMessage>
    private var context: Context

    init {
        messages = ArrayList()
        this.context = context
    }

    fun loadMessages(messages: MutableList<BaseMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    fun addFirst(message: BaseMessage) {
        messages.add(0, message)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            VIEW_TYPE_USER_MESSAGE_ME  -> {
                MyUserHolder(layoutInflater.inflate(R.layout.item_send_message, parent, false))
            }
            VIEW_TYPE_USER_MESSAGE_OTHER ->  {
                OtherUserHolder(layoutInflater.inflate(R.layout.item_recive_message, parent, false))
            }
            else -> MyUserHolder(layoutInflater.inflate(R.layout.item_send_message, parent, false)) //Generic return

        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = messages.get(position)

        when (message) {
            is UserMessage -> {
                if (message.sender.userId.equals(SendBird.getCurrentUser().userId)) return VIEW_TYPE_USER_MESSAGE_ME
                else return VIEW_TYPE_USER_MESSAGE_OTHER
            }
            //Handle other types of messages FILE/ADMIN ETC
            else ->  return -1
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MyUserHolder
                holder.bindView(context, messages.get(position) as UserMessage)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherUserHolder
                holder.bindView(context, messages.get(position) as UserMessage)
            }
            //Handle other types of messages FILE/ADMIN ETC
        }

    }

    class MyUserHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageText = view.text_gchat_message_me
        val date = view.text_gchat_date_me
        val messageDate = view.text_gchat_timestamp_me

        fun bindView(context: Context, message: UserMessage) {

            messageText.setText(message.message)
            messageDate.text = DateUtil.formatTime(message.createdAt)

            date.visibility = View.VISIBLE
            date.text = DateUtil.formatDate(message.createdAt)
        }
    }

    class OtherUserHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageText = view.text_gchat_message_other
        val date = view.text_gchat_date_other
        val timestamp = view.text_gchat_timestamp_other
        val profileImage = view.image_gchat_profile_other
        val user = view.text_gchat_user_other



        fun bindView(context: Context, message: UserMessage) {

            messageText.setText(message.message)

            timestamp.text = DateUtil.formatTime(message.createdAt)

            date.visibility = View.VISIBLE
            date.text = DateUtil.formatDate(message.createdAt)

            Glide.with(context).load(message.sender.profileUrl).apply(RequestOptions().override(75, 75))
                .into(profileImage)
            user.text = message.sender.nickname

        }

    }

    object DateUtil {
        fun formatTime(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }


        fun formatDate(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }
    }

}