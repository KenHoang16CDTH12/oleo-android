package com.framgia.oleo.screen.boxchat

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.Message
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.utils.Index
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.util.ArrayList
import java.util.Date
import javax.inject.Inject

class BoxChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
) : BaseViewModel() {

    private val user = userRepository.getUser()
    private var message = MutableLiveData<Message>()
    private var messages = MutableLiveData<ArrayList<Message>>()
    private var imageProfile = MutableLiveData<String>()
    private var oldMessageId = ""

    fun getMessage(roomId: String): MutableLiveData<Message> {
        messagesRepository.getMessage(user!!.id, roomId, object : ChildEventListener {
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onCancelled(snapshot: DatabaseError) {}

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(Message::class.java)
                if (post != null) message.value = post
                if (previousChildName == null) oldMessageId = snapshot.key.toString()
            }
        })
        return message
    }

    fun loadOldMessage(roomId: String): MutableLiveData<ArrayList<Message>> {
        val messageList = arrayListOf<Message>()
        messagesRepository.getOldMessage(user!!.id, roomId, oldMessageId, object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == Index.POSITION_ZERO) oldMessageId = dataSnapshot.key.toString()
                    val post = dataSnapshot.getValue(Message::class.java)
                    if (post != null) messageList.add(post)
                }
                if (messageList.size != Index.POSITION_ZERO) messages.value = messageList
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
        return messages
    }

    fun sendMessage(text: String, boxChat: BoxChat) {
        val messageId = messagesRepository.getMessageId(user!!.id, boxChat.id!!)
        val message = Message(messageId, user.id, text, DateFormat.getDateTimeInstance().format(Date()))
        messagesRepository.sendMessage(user, boxChat, message)
    }

    fun getFriendImageProfile(userId: String): MutableLiveData<String> {
        messagesRepository.getImageProfile(userId, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    val user = dataSnapShot.getValue(User::class.java)
                    imageProfile.value = user!!.image
                }
            }
        })
        return imageProfile
    }

    fun getUserId() = userRepository.getUser()

    companion object {

        const val LIMIT_DATA = 10

        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): BoxChatViewModel =
            ViewModelProvider(fragment, factory).get(BoxChatViewModel::class.java)
    }
}
