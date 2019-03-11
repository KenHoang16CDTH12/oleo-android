package com.framgia.oleo.screen.followed

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.Followed
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.utils.Constant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class FollowedViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val usersLiveData: MutableLiveData<MutableList<User>> by lazy {
        MutableLiveData<MutableList<User>>()
    }

    val followedLiveData: MutableLiveData<MutableList<Followed>> by lazy {
        MutableLiveData<MutableList<Followed>>()
    }

    val onErrorEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getFollowedsOfUser() {
        userRepository.getFollowedsOfUser(userRepository.getUser()?.id!!, object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                onErrorEvent.value = application.getString(R.string.msg_on_get_following_failed)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val followeds = mutableListOf<Followed>()
                for (datasnapshot in dataSnapshot.children) {
                    followeds.add(datasnapshot.getValue(Followed::class.java)!!)
                }
                getUsersByFolloweds(followeds, dataSnapshot.children.count())
                followedLiveData.value = followeds
            }
        })
    }

    fun getUsersByFolloweds(
        followeds: MutableList<Followed>,
        childrenCount: Int
    ) {
        var users = mutableListOf<User>()
        if (followeds.isEmpty()) {
            usersLiveData.value = users
            return
        }
        for (followed in followeds) {
            userRepository.getUserById(followed.id!!, object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    users.add(dataSnapshot.getValue(User::class.java)!!)
                    if (users.size == childrenCount) {
                        usersLiveData.value = users
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onErrorEvent.value = application.getString(R.string.msg_on_get_followed_failed)
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

    fun deleteUserFollowed(user: User) {
        userRepository.deleteUserFollowed(userRepository.getUser()?.id!!,user)
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): FollowedViewModel =
            ViewModelProvider(fragment, factory).get(FollowedViewModel::class.java)
    }
}
