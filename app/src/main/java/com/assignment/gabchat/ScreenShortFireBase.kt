package com.assignment.gabchat

import android.util.Log
import com.assignment.gabchat.dataclass.FireBaseMessageData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*

var firebaseDatabase: FirebaseDatabase? = null
var dbRef: DatabaseReference? = null
 lateinit var ssData:ArrayList<FireBaseMessageData>

class ScreenShortFireBase {

    fun insertMessage( SenderUserName: String,  ReciverUserName: String,  Message:String){
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef = firebaseDatabase!!.getReference("screenShortMessage")
        var msgInfo = FireBaseMessageData(SenderUserName,ReciverUserName,Message)
        val id: String = dbRef!!.push().getKey().toString()
        dbRef!!.child(id).setValue(msgInfo)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Log.e("ScreenShort:", "Data added to firebase")

                }
            })

    }

    fun getMessage( currentUserName: String): ArrayList<FireBaseMessageData> {
      ssData = arrayListOf<FireBaseMessageData>()

        firebaseDatabase = FirebaseDatabase.getInstance()
        dbRef= FirebaseDatabase.getInstance().getReference("screenShortMessage")
        dbRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        var data = userSnapshot.getValue(FireBaseMessageData::class.java)!!
                        if(currentUserName == data.ReciverUserName) {
                            ssData.add(data)
                        }
                    }
                }else{
                    Log.e("screenshort error","not found data")
                }
                return

            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

        return ssData
    }

    fun deleteMessage( SenderUserName: String,  ReciverUserName: String,  Message:String){

    }
}