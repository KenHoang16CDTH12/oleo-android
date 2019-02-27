package com.framgia.oleo.screen.search

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.framgia.oleo.data.source.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val users = mutableListOf<User>()
    val usersSeachResult: MutableLiveData<ArrayList<User>> by lazy {
        MutableLiveData<ArrayList<User>>()
    }
    val onAddFriendRequest: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun addFriendRequest(user: User) {
        var defautMessage = StringBuilder(
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
            userRepository.getUser()!!, defautMessage,
            OnSuccessListener {
                onAddFriendRequest.value = application.getString(string.msg_on_add_friend_request_success)
            }, onFailureListener = OnFailureListener {
                onAddFriendRequest.value = application.getString(string.msg_on_add_friend_request_failed)
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

    fun getUserByPhoneNumber(phone: String) {
        var seachResult = arrayListOf<User>()
        when (phone.length != 0) {
            true -> for (user in users) {
                if (user.phoneNumber.contains(phone)) {
                    seachResult.add(user)
                }
            }
            false -> seachResult.clear()
        }
        usersSeachResult.value = seachResult
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): SearchViewModel =
            ViewModelProvider(fragment, factory).get(SearchViewModel::class.java)
    }
}
