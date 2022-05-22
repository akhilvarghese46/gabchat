package com.assignment.gabchat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.ConstantValues.SharedPreferanceObject
import com.assignment.gabchat.Interface.AddContactClickedListener
import com.assignment.gabchat.R
import com.assignment.gabchat.dataclass.ChannelModel
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class AddContactAdapter(context: Context,private val addContactClickedListener: AddContactClickedListener, private val mList: List<ChannelModel>) : RecyclerView.Adapter<AddContactAdapter.ViewHolder>() {
    private var context: Context = context
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_members, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        if(ItemsViewModel.userName != SharedPreferanceObject.SBUserId)
            {
                holder.bindView(context, ItemsViewModel.userName)
                holder.membersName.text = ItemsViewModel.userName
                holder.itemView.setOnClickListener {
                    addContactClickedListener.onAddContactListener(ItemsViewModel)
                }
            }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        fun bindView(context: Context, userName: String) {
            var imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("profilePic/"+userName+".jpg")
            if(imageRef!= null) {
                val localFile = File.createTempFile("profilePic", "jpg")

                imageRef.getFile(localFile).addOnSuccessListener {
                    Glide.with(context)
                        .load(localFile.absolutePath)
                        .circleCrop()
                        .into(proImg as ImageView)
                }.addOnFailureListener {
                    Log.e("GabChat error:", "error when getting profile picture.")
                }
            }
        }

        val proImg: ImageView = itemView.findViewById(R.id.img_user)
        val membersName: TextView = itemView.findViewById(R.id.txt_membername)

    }
}