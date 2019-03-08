package com.framgia.oleo.screen.messages

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.utils.Constant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class MessageOptionViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val userFriend: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val followRequest: MutableLiveData<FollowRequest> by lazy {
        MutableLiveData<FollowRequest>()
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

    fun getFollowRequest(id: String) {
        userRepository.getFollowRequestById(id, userRepository.getUser()!!, object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    followRequest.value = p0.getValue(FollowRequest::class.java)
                }
            }
        })
    }

    fun addFollowRequest(userFriend: User) {
        userRepository.addFollowRequest(userRepository.getUser()!!, userFriend)
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): MessageOptionViewModel =
            ViewModelProvider(fragment, factory).get(MessageOptionViewModel::class.java)
    }
}
