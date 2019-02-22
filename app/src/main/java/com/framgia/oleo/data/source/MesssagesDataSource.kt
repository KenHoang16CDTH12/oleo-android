package com.framgia.oleo.data.source

import com.framgia.oleo.data.source.model.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener


interface MesssagesDataSource {

    fun getMessageId(userId: String, boxId: String): String

    fun getMessage(userId: String, roomId: String, childEventListener: ChildEventListener)

    fun getOldMessage(userId: String, roomId: String, oldMessageId: String, valueEventListener: ValueEventListener)

    fun sendMessage(userId: String, boxId: String, messageId: String, message: Message)

    fun getImageProfile(userId: String, valueEventListener: ValueEventListener)

    fun getListBoxChat(userId: String, childEventListener: ChildEventListener)

    fun getLastMessage(userId: String, roomId: String, childEventListener: ChildEventListener)
}
