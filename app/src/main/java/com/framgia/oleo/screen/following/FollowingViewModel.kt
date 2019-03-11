package com.framgia.oleo.screen.following

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FollowingViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val usersLiveData: MutableLiveData<MutableList<User>> by lazy {
        MutableLiveData<MutableList<User>>()
    }

    val followRequestsLiveData: MutableLiveData<MutableList<FollowRequest>> by lazy {
        MutableLiveData<MutableList<FollowRequest>>()
    }

    val onErrorEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getFollowingRequestOfUser() {
        userRepository.getFollowRequestsOfUser(
            userRepository.getUser()!!.id,
            Constant.STATUS_FOLLOWING,
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    onErrorEvent.value = application.getString(R.string.msg_on_get_following_failed)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val followRequests = mutableListOf<FollowRequest>()
                    for (datasnapshot in dataSnapshot.children) {
                        followRequests.add(datasnapshot.getValue(FollowRequest::class.java)!!)
                    }
                    getUsersByFollowRequests(followRequests, dataSnapshot.children.count())
                    followRequestsLiveData.value = followRequests
                }
            })
    }

    fun getUsersByFollowRequests(
        followRequests: MutableList<FollowRequest>,
        childrenCount: Int
    ) {
        var users = mutableListOf<User>()
        if (followRequests.isEmpty()) {
            usersLiveData.value = users
            return
        }
        for (followRequest in followRequests) {
            userRepository.getUserById(followRequest.id!!, object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    users.add(dataSnapshot.getValue(User::class.java)!!)
                    if (users.size == childrenCount) {
                        usersLiveData.value = users
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onErrorEvent.value = application.getString(R.string.msg_on_get_following_failed)
                }
            })
        }
    }

    fun changeStatusOfFollowRequest(user: User) {
        userRepository.changeFollowStatus(
            userCurrent = userRepository.getUser()!!,
            userFriend = user,
            status = Constant.STATUS_BLOCKING
        )
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): FollowingViewModel =
            ViewModelProvider(fragment, factory).get(FollowingViewModel::class.java)
    }
}
