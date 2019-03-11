package com.framgia.oleo.screen.friendrequest

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FriendRequestsViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val friend: MutableLiveData<User> by lazy { MutableLiveData<User>() }

    val onAddFriendRequest: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val usersLiveData: MutableLiveData<MutableList<User>> by lazy {
        MutableLiveData<MutableList<User>>()
    }
    val friendRequestsLiveData: MutableLiveData<List<FriendRequest>> by lazy {
        MutableLiveData<List<FriendRequest>>()
    }

    fun getUserById(friendRequests: List<FriendRequest>) {
        val users = mutableListOf<User>()
        for (friendRequest in friendRequests) {
            userRepository.getUserById(friendRequest.id!!, object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    user?.let { users.add(it) }
                    if (users.size == friendRequests.size) {
                        usersLiveData.value = users
                    }
                }
            })
        }
    }

    fun getUsersByFriendRequests() {
        userRepository.getFriendRequests(userRepository.getUser()!!.id, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val friendRequests = mutableListOf<FriendRequest>()
                for (datasnapshot in p0.children) {
                    val friendRequest = datasnapshot.getValue(FriendRequest::class.java)
                    if (friendRequest != null && friendRequest.status == 0) {
                        friendRequests.add(friendRequest)
                    }
                }
                val friendRequestsFilter = friendRequests.filter { it.status == 0 }
                if (friendRequests.isEmpty()) {
                    usersLiveData.value = mutableListOf()
                }
                if (!friendRequests.isEmpty()) {
                    getUserById(friendRequestsFilter)
                }
                friendRequestsLiveData.value = friendRequestsFilter
            }
        })
    }

    fun confirmFriendRequest(friendRequest: FriendRequest) {
        val user = userRepository.getUser()
        userRepository.getUserById(friendRequest.id!!, object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                if (data.exists()) {
                    friend.value = data.getValue(User::class.java)
                    userRepository.confirmFriendRequest(user!!, friend.value!!, friendRequest,
                          onSuccessListener = OnSuccessListener {
                                  userRepository.addFriend(user.id, friendRequest.id!!, user, friend.value!!)
                                  onAddFriendRequest.value =
                                      application.getString(string.msg_on_add_friend_success) },
                          onFailureListener = OnFailureListener {
                                  onAddFriendRequest.value =
                                      application.getString(string.msg_on_get_waiting_failed) })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteFriendRequest(friendRequest: FriendRequest) {
        userRepository.deleteFriendRequest(
            userRepository.getUser()!!,
            friendRequest,
            onSuccessListener = OnSuccessListener {
                onAddFriendRequest.value = application.getString(string.msg_on_delete_friend_request)
            },
            onFailureListener = OnFailureListener {
                onAddFriendRequest.value = application.getString(string.msg_on_add_friend_success)
            })
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): FriendRequestsViewModel =
            ViewModelProvider(fragment, factory).get(FriendRequestsViewModel::class.java)
    }
}
