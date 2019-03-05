package com.framgia.oleo.screen.followed

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import javax.inject.Inject

class WaitingViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository
) : BaseViewModel() {

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): WaitingViewModel =
            ViewModelProvider(fragment, factory).get(WaitingViewModel::class.java)
    }
}
