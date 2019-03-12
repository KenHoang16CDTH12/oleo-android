package com.framgia.oleo.screen.messages

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.BoxChat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class MessagesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
) : BaseViewModel() {

    private lateinit var messageAdapter: MessagesAdapter
    val onUpdateBoxChat: MutableLiveData<ArrayList<BoxChat>> = MutableLiveData()
    private var listBoxChat = arrayListOf<BoxChat>()

    fun setAdapter(messageAdapter: MessagesAdapter) {
        this.messageAdapter = messageAdapter
        this.messageAdapter.getUser(userRepository.getUser()!!)
        this.messageAdapter.setMessageRepository(messagesRepository)
    }

    fun getAllMessages(): MutableLiveData<ArrayList<BoxChat>> {
        messagesRepository.getListBoxChat(userRepository.getUser()!!.id, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                    if (dataSnapshot != null) listBoxChat.add(dataSnapshot.getValue(BoxChat::class.java)!!)
                }
                onUpdateBoxChat.value = listBoxChat
                listBoxChat.clear()
            }
        })
        return onUpdateBoxChat
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): MessagesViewModel =
            ViewModelProvider(fragment, factory).get(MessagesViewModel::class.java)
    }
}

