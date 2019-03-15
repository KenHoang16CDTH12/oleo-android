package com.framgia.oleo.data.source

import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.Message
import com.framgia.oleo.data.source.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener

interface MesssagesDataSource {

    fun getMessageId(userId: String, boxId: String): String

    fun getMessage(userId: String, roomId: String, childEventListener: ChildEventListener)

    fun getOldMessage(userId: String, roomId: String, oldMessageId: String, valueEventListener: ValueEventListener)

    fun sendMessage(user: User, boxChat: BoxChat, message: Message)

    fun getImageProfile(userId: String, valueEventListener: ValueEventListener)

    fun getListBoxChat(userId: String, childEventListener: ChildEventListener)

    fun getLastMessage(userId: String, roomId: String, childEventListener: ChildEventListener)

    fun getBoxChat(userId: String, friend: User, valueEventListener: ValueEventListener)

    fun getNameBoxChat(userId: String, boxChatId : String, onValueEventListener: ValueEventListener)
}
