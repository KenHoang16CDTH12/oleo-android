package com.framgia.oleo.screen.search

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.data.source.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
) : BaseViewModel() {

    val users = mutableListOf<User>()
    val usersSeachResult: MutableLiveData<ArrayList<User>> by lazy {
        MutableLiveData<ArrayList<User>>()
    }
    val onAddFriendRequest: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    private val onOpenBoxChat: MutableLiveData<BoxChat>by lazy {
        MutableLiveData<BoxChat>()
    }
    val userId = userRepository.getUser()!!.id
    val getFriendList: MutableLiveData<MutableList<Friend>>by lazy { MutableLiveData<MutableList<Friend>>() }

    fun addFriendRequest(user: User) {
        val defaultMessage = StringBuilder(
            application
                .getString(string.msg_defaut_friend_request_prefix)
        )
            .append(userRepository.getUser()!!.userName)
            .append(
                application.getString(
                    string.msg_defaut_friend_request_postfix
                )
            )
            .toString()

        userRepository.addFriendRequest(user.id,
            userRepository.getUser()!!, defaultMessage,
            OnSuccessListener {
                onAddFriendRequest.value = application.getString(string.msg_on_add_friend_request_success)
            }, onFailureListener = OnFailureListener {
                onAddFriendRequest.value = application.getString(string.msg_on_get_waiting_failed)
            })
    }

    fun getUsers() {
        userRepository.getUsers(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()) {
                    return
                }
                for (datasnapshot in p0.children) {
                    val user = datasnapshot.getValue((User::class.java))
                    user?.let { users.add(it) }
                }
            }
        })
    }

    fun getBoxChat(friend: User): MutableLiveData<BoxChat> {
        messagesRepository.getBoxChat(
            userId,
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

    fun getFriend(): MutableLiveData<MutableList<Friend>> {
        userRepository.getContactsUser(userRepository.getUser()!!.id, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val listFriend = mutableListOf<Friend>()
                if (snapshot.exists())
                    snapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                        listFriend.add(dataSnapshot!!.getValue(Friend::class.java)!!)
                    }
                getFriendList.value = listFriend
            }
        })
        return getFriendList
    }

    fun searchByPhoneNumberOrName(query: String) {
        val searchResult = arrayListOf<User>()
        when (query.isNotEmpty()) {
            true -> for (user in users) {
                if (user.phoneNumber.contains(query) || user.userName.toLowerCase().contains(query)) {
                    searchResult.add(user)
                }
            }
            false -> searchResult.clear()
        }
        usersSeachResult.value = searchResult
    }

    fun getBoxChatLiveData() : MutableLiveData<BoxChat> = onOpenBoxChat

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): SearchViewModel =
            ViewModelProvider(fragment, factory).get(SearchViewModel::class.java)
    }
}
