package com.framgia.oleo.screen.following

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.utils.Constant
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import javax.inject.Inject

class FollowingViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val onChildAddedEvent: MutableLiveData<FollowRequest> by lazy {
        MutableLiveData<FollowRequest>()
    }

    val onChildChangedEvent: MutableLiveData<FollowRequest> by lazy {
        MutableLiveData<FollowRequest>()
    }

    val onChildRemovedEvent: MutableLiveData<FollowRequest> by lazy {
        MutableLiveData<FollowRequest>()
    }

    val onErrorEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getFollowingRequestOfUser() {
        userRepository.getFollowRequestsOfUser(
            userRepository.getUser()!!.id,
            Constant.STATUS_FOLLOWING,
            object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    onErrorEvent.value = application.getString(R.string.msg_on_get_waiting_failed)
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                    //todo
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                    onChildChangedEvent.value = dataSnapshot.getValue(FollowRequest::class.java)
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    onChildAddedEvent.value = dataSnapshot.getValue(FollowRequest::class.java)
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    onChildRemovedEvent.value = dataSnapshot.getValue(FollowRequest::class.java)
                }
            })
    }

    fun changeStatusOfFollowRequest(followRequest: FollowRequest) {
        followRequest.status = Constant.STATUS_BLOCKING
        userRepository.changeFollowStatus(followRequest = followRequest, userCurrent = userRepository.getUser()!!)
        userRepository.addUserFollowed(followRequest.id!!, userRepository.getUser()!!)
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): FollowingViewModel =
            ViewModelProvider(fragment, factory).get(FollowingViewModel::class.java)
    }
}
