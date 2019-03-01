package com.framgia.oleo.screen.setting

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class SettingViewModel @Inject constructor(
    private val googleSignInOptions: GoogleSignInOptions, private val userRepository: UserRepository
) : BaseViewModel() {

    fun getGoogleSignInOptions(): GoogleSignInOptions = googleSignInOptions

    fun deleteUser() {
        userRepository.deleteUser()
    }

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): SettingViewModel =
            ViewModelProvider(fragment, factory).get(SettingViewModel::class.java)
    }
}
