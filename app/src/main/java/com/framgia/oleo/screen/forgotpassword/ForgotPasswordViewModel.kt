package com.framgia.oleo.screen.forgotpassword

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.framgia.oleo.base.BaseViewModel
import com.framgia.oleo.data.source.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    private val isUserExistLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSignInSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun checkExistUser(phoneNumber: String) {
        userRepository.getUserByPhoneNumber(phoneNumber, object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                isUserExistLiveData.value = dataSnapshot.exists()
            }
        })
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isSignInSuccess.value = task.isSuccessful
            }
    }

    fun getIsUserExistLiveData(): MutableLiveData<Boolean> = isUserExistLiveData

    companion object {
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): ForgotPasswordViewModel =
            ViewModelProvider(fragment, factory).get(ForgotPasswordViewModel::class.java)
    }
}
