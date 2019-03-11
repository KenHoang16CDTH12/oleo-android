package com.framgia.oleo.screen.contacts

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.Friend
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel() {

    private val userContacts: MutableLiveData<MutableList<Friend>> by lazy {
        MutableLiveData<MutableList<Friend>>()
    }
    private val messageErrorLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }

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

    fun getMessageLiveDataError(): MutableLiveData<String> {
        return messageErrorLiveData
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): ContactsViewModel =
            ViewModelProvider(fragment, factory).get(ContactsViewModel::class.java)
    }
}
