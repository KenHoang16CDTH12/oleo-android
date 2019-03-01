package com.framgia.oleo.screen.main

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private var isCheckedUser = MutableLiveData<Boolean>()

    fun getUserLocal() {
        isCheckedUser.value = userRepository.getUser() != null
    }

    fun isCheckUser(): MutableLiveData<Boolean> {
        return isCheckedUser
    }

    companion object {
        fun create(
            activity: FragmentActivity, factory: ViewModelProvider.Factory
        ): MainViewModel = ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }
}
