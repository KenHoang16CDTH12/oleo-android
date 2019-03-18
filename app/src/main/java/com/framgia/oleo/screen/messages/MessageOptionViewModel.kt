package com.framgia.oleo.screen.messages

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.BoxChat
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
    private val userRepository: UserRepository,
    private val messagesRepository: MessagesRepository
) : BaseViewModel() {

    val userFriend: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val onFollowRequestStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val onMessageEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val friendRequestStatus: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val onAddFriendRequest: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val resultError: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isBoxChatDeleted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val boxChatName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getUserFriend(friendId: String) {
        userRepository.getUserById(friendId, object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(data: DataSnapshot) {
                if (data.exists()) {
                    userFriend.value = data.getValue(User::class.java)
                }
            }
        })
    }

    fun getBoxChatName(boxChatId: String) {
        messagesRepository.getNameBoxChat(userRepository.getUser()!!.id, boxChatId, object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(data: DataSnapshot) {
                if (data.exists())
                    boxChatName.value = data.getValue(BoxChat::class.java)!!.userFriendName
            }
        })
    }

    fun getFollowRequestOfUser(id: String) {
        userRepository.getFollowRequestById(id, userRepository.getUser()!!, object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                onMessageEvent.value = application.getString(R.string.msg_on_database_error)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isFollowed = false
                if (dataSnapshot.exists()) {
                    val followRequest = dataSnapshot.getValue(FollowRequest::class.java)
                    if (followRequest != null)
                        onFollowRequestStatus.value = followRequest.status
                } else {
                    onFollowRequestStatus.value = Constant.STATUS_NOT_EXIST
                }
            }
        })
    }

    fun onUpdateNickNameMyFriend(friendId: String, newName: String) {
        userRepository.updateNameFriend(userRepository.getUser()!!.id, friendId, newName,
            OnSuccessListener {},
            OnFailureListener { error ->
                resultError.value = error.message
            })
    }

    fun addFollowRequest(userFriend: User) {
        userRepository
            .addFollowRequest(userRepository.getUser()!!, userFriend, OnFailureListener {
                onMessageEvent.value = application.getString(R.string.msg_on_add_follow_request_failed)
            }, OnSuccessListener {
                onMessageEvent.value = application.getString(R.string.msg_on_add_follow_request_success)
            })
    }

    fun deleteFollowRequest(friendId: String) {
        userRepository.deleteFollowRequest(userRepository.getUser()!!.id, friendId,
            OnSuccessListener {
                onMessageEvent.value = application.getString(string.remove_follow_request)
            }, OnFailureListener {
            })
    }

    fun deleteFriend(friendId: String) {
        userRepository.deleteFriend(userRepository.getUser()!!.id, friendId)
    }

    fun checkFriendByUserId(friendId: String) {
        userRepository.checkFriendByUserId(userRepository.getUser()!!.id, friendId, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    friendRequestStatus.value = Constant.STATUS_FRIEND_ACCEPT
                } else {
                    friendRequestStatus.value = Constant.STATUS_NOT_EXIST.toInt()
                }
            }
        })
    }

    fun checkFriendRequest(friendId: String) {
        userRepository.checkFriendRequest(userRepository.getUser()!!.id, friendId, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    friendRequestStatus.value = Constant.STATUS_FRIEND_WAITING
                } else if (friendRequestStatus.value != Constant.STATUS_FRIEND_ACCEPT) {
                    friendRequestStatus.value = Constant.STATUS_NOT_EXIST.toInt()
                }
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

    fun deleteFriendRequest(friendId: String) {
        userRepository.deleteFriendRequest(userRepository.getUser()!!.id, friendId)
    }

    fun deleteBoxChat(boxChatId: String) {
        messagesRepository.deleteBoxChat(
            userRepository.getUser()!!.id,
            boxChatId,
            OnSuccessListener {
                isBoxChatDeleted.value = true
            },
            OnFailureListener {
                isBoxChatDeleted.value = false
            })
    }

    fun checkIsFriend(friendId: String): Boolean = userRepository.getUser()!!.id != friendId

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): MessageOptionViewModel =
            ViewModelProvider(fragment, factory).get(MessageOptionViewModel::class.java)
    }
}
