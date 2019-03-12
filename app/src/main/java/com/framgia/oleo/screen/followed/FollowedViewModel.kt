package com.framgia.oleo.screen.followed

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.Followed
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import javax.inject.Inject

class FollowedViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val onChildAddedEvent: MutableLiveData<Followed> by lazy {
        MutableLiveData<Followed>()
    }

    val onChildChangedEvent: MutableLiveData<Followed> by lazy {
        MutableLiveData<Followed>()
    }

    val onChildRemovedEvent: MutableLiveData<Followed> by lazy {
        MutableLiveData<Followed>()
    }

    val onErrorEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getFollowedsOfUser() {
        userRepository.getFollowedsOfUser(userRepository.getUser()!!.id, object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                onErrorEvent.value = application.getString(R.string.msg_on_get_followed_failed)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                //todo
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                onChildChangedEvent.value = dataSnapshot.getValue(Followed::class.java)
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                onChildAddedEvent.value = dataSnapshot.getValue(Followed::class.java)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                onChildRemovedEvent.value = dataSnapshot.getValue(Followed::class.java)
            }
        })
    }

    fun deleteUserFollowed(followed: Followed) {
        userRepository.deleteUserFollowed(userRepository.getUser()?.id!!, followed)
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): FollowedViewModel =
            ViewModelProvider(fragment, factory).get(FollowedViewModel::class.java)
    }
}
