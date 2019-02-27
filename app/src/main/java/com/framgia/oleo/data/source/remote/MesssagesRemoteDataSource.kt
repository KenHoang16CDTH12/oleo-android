package com.framgia.oleo.data.source.remote

import com.framgia.oleo.data.source.MesssagesDataSource
import com.framgia.oleo.data.source.model.Message
import com.framgia.oleo.screen.boxchat.BoxChatViewModel
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.Index
import com.google.firebase.database.ChildEventListener
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

    override fun getOldMessage(userId: String, roomId: String, oldMessageId: String,
        valueEventListener: ValueEventListener) {
        firebaseDatabase.reference.child(Constant.PATH_STRING_USER).child(userId)
            .child(Constant.PATH_STRING_BOX).child(roomId).child(Constant.PATH_STRING_MESSAGE)
            .limitToLast(BoxChatViewModel.LIMIT_DATA).endAt(oldMessageId).orderByKey()
            .addListenerForSingleValueEvent(valueEventListener)
    }

    override fun sendMessage(userId: String, boxId: String, messageId: String, message: Message) {
        firebaseDatabase.reference.child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_BOX)
            .child(boxId)
            .child(Constant.PATH_STRING_MESSAGE)
            .child(messageId)
            .setValue(message)
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

    companion object {
        fun newInstance() = MesssagesRemoteDataSource()
    }
}
