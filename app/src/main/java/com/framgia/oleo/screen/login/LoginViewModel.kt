package com.framgia.oleo.screen.login

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.framgia.oleo.base.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val application: Application) : BaseViewModel() {

    fun checkLastLogin(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(application) != null
    }

    fun receiveDataUserGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            //Xử lý lưu vào room database
        } catch (e: ApiException) {
            Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    fun receiveDataUserFacebook(result: LoginResult) {
        val request: GraphRequest = GraphRequest.newMeRequest(result.accessToken) { jsonObject, response ->
            //Handle get value by key
        }
        //Request Graph API
        val bundle = Bundle()
        bundle.putString(BUNDLE_FIELDS, BUNDLE_REQUEST_KEY)
        request.parameters = bundle
        request.executeAsync()
    }

    companion object {
        const val BUNDLE_FIELDS = "fields"
        const val BUNDLE_REQUEST_KEY = "id,name,email"
        fun create(fragment: Fragment, factory: ViewModelProvider.Factory): LoginViewModel =
            ViewModelProvider(fragment, factory).get(LoginViewModel::class.java)
    }
}
