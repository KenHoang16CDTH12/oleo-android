package com.framgia.oleo.screen.messages

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.BoxChat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
) : BaseViewModel() {

    private lateinit var messageAdapter: MessagesAdapter

    fun setAdapter(messageAdapter: MessagesAdapter) {
        this.messageAdapter = messageAdapter
        this.messageAdapter.getUser(userRepository.getUser()!!)
        this.messageAdapter.setMessageRepository(messagesRepository)
    }

    fun getAllMessages() {
        messagesRepository.getListBoxChat(userRepository.getUser()!!.id, object : ChildEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val data = dataSnapshot.getValue(BoxChat::class.java)
                if (data != null) messageAdapter.updateData(data)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        })
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): MessagesViewModel =
            ViewModelProvider(fragment, factory).get(MessagesViewModel::class.java)
    }
}

