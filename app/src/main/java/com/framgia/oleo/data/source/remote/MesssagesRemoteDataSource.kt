package com.framgia.oleo.data.source.remote

import com.framgia.oleo.data.source.MesssagesDataSource
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.Message
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.screen.boxchat.BoxChatViewModel
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.Index
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MesssagesRemoteDataSource : MesssagesDataSource {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun getMessageId(userId: String, boxId: String): String = firebaseDatabase.reference.child(Constant
        .PATH_STRING_USER)
        .child(userId)
        .child(Constant.PATH_STRING_BOX)
        .child(boxId)
        .child(Constant.PATH_STRING_MESSAGE)
        .push().key.toString()

    override fun getMessage(userId: String, roomId: String, childEventListener: ChildEventListener) {
        firebaseDatabase.reference.child(Constant.PATH_STRING_USER).child(userId)
            .child(Constant.PATH_STRING_BOX).child(roomId).child(Constant.PATH_STRING_MESSAGE)
            .limitToLast(BoxChatViewModel.LIMIT_DATA)
            .addChildEventListener(childEventListener)
    }

    override fun getOldMessage(
        userId: String, roomId: String, oldMessageId: String,
        valueEventListener: ValueEventListener
    ) {
        firebaseDatabase.reference.child(Constant.PATH_STRING_USER).child(userId)
            .child(Constant.PATH_STRING_BOX).child(roomId).child(Constant.PATH_STRING_MESSAGE)
            .limitToLast(BoxChatViewModel.LIMIT_DATA).endAt(oldMessageId).orderByKey()
            .addListenerForSingleValueEvent(valueEventListener)
    }

    override fun sendMessage(user: User, boxChat: BoxChat, message: Message) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(user.id)
            .child(Constant.PATH_STRING_BOX)
            .child(boxChat.id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
                            .child(user.id)
                            .child(Constant.PATH_STRING_BOX)
                            .child(boxChat.id!!)
                            .setValue(boxChat)
                    }
                    firebaseDatabase.reference.child(Constant.PATH_STRING_USER)
                        .child(user.id)
                        .child(Constant.PATH_STRING_BOX)
                        .child(boxChat.id!!)
                        .child(Constant.PATH_STRING_MESSAGE)
                        .child(message.id!!)
                        .setValue(message)
                }
            })
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(boxChat.id!!)
            .child(Constant.PATH_STRING_BOX)
            .child(user.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if (!p0.exists()) {
                        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
                            .child(boxChat.id!!)
                            .child(Constant.PATH_STRING_BOX)
                            .child(user.id)
                            .setValue(BoxChat(user.id, arrayListOf(), user.userName))
                    }
                    firebaseDatabase.reference.child(Constant.PATH_STRING_USER)
                        .child(boxChat.id!!)
                        .child(Constant.PATH_STRING_BOX)
                        .child(user.id)
                        .child(Constant.PATH_STRING_MESSAGE)
                        .child(message.id!!)
                        .setValue(message)
                }
            })
    }

    override fun getImageProfile(userId: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.reference.child(Constant.PATH_STRING_USER).child(userId)
            .addValueEventListener(valueEventListener)
    }

    override fun getListBoxChat(userId: String, childEventListener: ChildEventListener) {
        firebaseDatabase.reference
            .child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_BOX)
            .addChildEventListener(childEventListener)
    }

    override fun getLastMessage(userId: String, roomId: String, childEventListener: ChildEventListener) {
        firebaseDatabase.reference
            .child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_BOX)
            .child(roomId).child(Constant.PATH_STRING_MESSAGE)
            .limitToLast(Index.POSITION_ONE)
            .addChildEventListener(childEventListener)
    }

    override fun getBoxChat(userId: String, friend: User, valueEventListener: ValueEventListener) {
        firebaseDatabase.reference.child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_BOX)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        snapshot.child(friend.id).ref.setValue(
                            BoxChat(friend.id, arrayListOf(), friend.userName)
                        ).addOnSuccessListener {
                            snapshot.child(friend.id).ref.addListenerForSingleValueEvent(valueEventListener)
                        }
                        return
                    }
                    snapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                        if (dataSnapshot!!.getValue(BoxChat::class.java)!!.id.toString() == friend.id) {
                            dataSnapshot.ref.addListenerForSingleValueEvent(valueEventListener)
                            return
                        }

                    }
                    snapshot.child(friend.id).ref.setValue(
                        BoxChat(friend.id, arrayListOf(), friend.userName)
                    ).addOnSuccessListener {
                        snapshot.child(friend.id).ref.addListenerForSingleValueEvent(valueEventListener)
                    }
                }
            })
    }

    override fun getNameBoxChat(userId: String, boxChatId: String, onValueEventListener: ValueEventListener) {
        firebaseDatabase.reference
            .child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_BOX)
            .child(boxChatId)
            .addValueEventListener(onValueEventListener)
    }

    override fun deleteBoxChat(
        userId: String,
        boxChatId: String,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_BOX)
            .child(boxChatId).ref
            .removeValue()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    companion object {
        fun newInstance() = MesssagesRemoteDataSource()
    }
}
