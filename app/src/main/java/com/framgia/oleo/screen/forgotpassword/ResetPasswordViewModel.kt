package com.framgia.oleo.screen.forgotpassword

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import javax.inject.Inject

class ResetPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val onResetPasswordState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun resetPassword(userId: String, password: String) {
        userRepository.updatePassword(userId, password,
            OnSuccessListener { onResetPasswordState.value = true },
            OnFailureListener { onResetPasswordState.value = false })
    }

    fun getResetPassowrdStateLiveData(): MutableLiveData<Boolean> = onResetPasswordState

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): ResetPasswordViewModel =
            ViewModelProvider(fragment, factory).get(ResetPasswordViewModel::class.java)
    }
}
