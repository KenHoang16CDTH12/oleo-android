package com.framgia.oleo.screen.followed

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import javax.inject.Inject

class FollowingViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): FollowingViewModel =
            ViewModelProvider(fragment, factory).get(FollowingViewModel::class.java)
    }
}
