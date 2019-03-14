package com.framgia.oleo.screen.messages

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.utils.Constant
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class MessageOptionViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val userFriend: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    private val onNavigateEvent: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private val onErrorEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isFriendAlready: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val onAddFriendRequest: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getUserFriend(id: String) {
        userRepository.getUserById(id, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    userFriend.value = p0.getValue(User::class.java)
                }
            }
        })
    }

    fun registerLiveData(id: String) {
        userRepository.getFollowRequestById(id, userRepository.getUser()!!, object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                onErrorEvent.value = application.getString(R.string.msg_on_database_error)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isFollowed = false
                if (dataSnapshot.exists()) {
                    val followRequest = dataSnapshot.getValue(FollowRequest::class.java)
                    isFollowed = followRequest!!.status.equals(Constant.STATUS_FOLLOWING)
                }
                when (isFollowed) {
                    true -> onNavigateEvent.value = true
                    else -> onErrorEvent.value = application.getString(R.string.msg_do_not_have_permission)
                }
            }
        })
    }

    fun addFollowRequest(userFriend: User) {
        userRepository.getFollowRequestSingleValueEvent(userRepository.getUser()!!.id, userFriend, object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                onErrorEvent.value = application.getString(R.string.msg_on_follow_request_exists)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when {
                    dataSnapshot.exists() -> userRepository
                        .addFollowRequest(userRepository.getUser()!!, userFriend)
                    else -> onErrorEvent.value = application.
                        getString(R.string.msg_on_follow_request_exists)
                }
            }
        })
    }

    fun getUserFriendLiveData(): MutableLiveData<User> = userFriend

    fun getOnNavigateEventLiveData(): MutableLiveData<Boolean> = onNavigateEvent

    fun getOnErrorEventLiveData(): MutableLiveData<String> = onErrorEvent

    fun deleteFriend(friendId: String) {
        userRepository.deleteFriend(userRepository.getUser()!!.id, friendId)
    }

    fun checkFriendByUserId(friendId: String) {
        userRepository.checkFriendByUserId(userRepository.getUser()!!.id, friendId, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isFriendAlready.value = dataSnapshot.exists()
            }
        })
    }

    fun addFriendRequest(user: User) {
        val defaultMessage = StringBuilder(application.getString(R.string.msg_defaut_friend_request_prefix))
            .append(userRepository.getUser()!!.userName)
            .append(application.getString(R.string.msg_defaut_friend_request_postfix))
            .toString()

        userRepository.addFriendRequest(user.id,
            userRepository.getUser()!!, defaultMessage,
            OnSuccessListener {
                onAddFriendRequest.value = application.getString(R.string.msg_on_add_friend_request_success)
            }, onFailureListener = OnFailureListener {
                onAddFriendRequest.value = application.getString(R.string.msg_on_get_waiting_failed)
            })
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): MessageOptionViewModel =
            ViewModelProvider(fragment, factory).get(MessageOptionViewModel::class.java)
    }
}
