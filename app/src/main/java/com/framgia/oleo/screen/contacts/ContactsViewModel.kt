package com.framgia.oleo.screen.contacts

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.data.source.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val userRepository: UserRepository, private val
    messagesRepository: MessagesRepository) :
    BaseViewModel() {

    private val userContacts: MutableLiveData<MutableList<Friend>> by lazy {
        MutableLiveData<MutableList<Friend>>()
    }
    private val searchResult: MutableLiveData<ArrayList<Friend>> by lazy {
        MutableLiveData<ArrayList<Friend>>()
    }
    private val messageErrorLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    val onOpenBoxChat: MutableLiveData<BoxChat>by lazy {
        MutableLiveData<BoxChat>()
    }

    fun getContacts() {
        userRepository.getContactsUser(userRepository.getUser()!!.id, object : ValueEventListener {
            var contacts = mutableListOf<Friend>()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) dataSnapshot.children.forEach { name ->
                    contacts.add(name.getValue(Friend::class.java)!!)
                    userContacts.value = contacts
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getBoxChat(friend: User): MutableLiveData<BoxChat> {
        messagesRepository.getBoxChat(
            userRepository.getUser()!!.id,
            friend,
            object : ValueEventListener {

                override fun onDataChange(snapShot: DataSnapshot) {
                    if (snapShot.getValue(BoxChat::class.java)!!.id.toString() == friend.id)
                        onOpenBoxChat.value = snapShot.getValue(BoxChat::class.java)
                }

                override fun onCancelled(p0: DatabaseError) {}
            })
        return onOpenBoxChat
    }

    fun searchContacts(query: String) {
        var result = arrayListOf<Friend>()
        when (query.isNotEmpty()) {
            true -> for (friend in userContacts.value!!) {
                if (friend.user!!.phoneNumber.contains(query) || friend.user!!.userName.toLowerCase().contains(query)) {
                    result.add(friend)
                }
            }
            false -> result = userContacts.value as ArrayList<Friend>
        }
        searchResult.value = result
    }

    fun getLiveDataContacts(): MutableLiveData<MutableList<Friend>> = userContacts

    fun getMessageLiveDataError(): MutableLiveData<String> = messageErrorLiveData

    fun getSearchResultLiveData(): MutableLiveData<ArrayList<Friend>> = searchResult

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): ContactsViewModel =
            ViewModelProvider(fragment, factory).get(ContactsViewModel::class.java)
    }
}
