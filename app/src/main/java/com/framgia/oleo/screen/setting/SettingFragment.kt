package com.framgia.oleo.screen.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FragmentSettingBinding
import com.framgia.oleo.utils.extension.isCheckMultiClick
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_setting.textFriendRequest
import kotlinx.android.synthetic.main.fragment_setting.textViewLogOut
import kotlinx.android.synthetic.main.fragment_setting.textViewNameUser
import kotlinx.android.synthetic.main.fragment_setting.textViewWatchList

class SettingFragment : BaseFragment(), View.OnClickListener {

    private lateinit var viewModel: SettingViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private var listener: OnSettingListener? = null
    private var binding by autoCleared<FragmentSettingBinding>()
    private var user : User? = null

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = SettingViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSettingListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun setUpView() {
        textViewLogOut.setOnClickListener(this)
        textFriendRequest.setOnClickListener(this)
        textViewWatchList.setOnClickListener(this)
    }

    override fun bindView() {
        user = viewModel.getUser()
        textViewNameUser.text = user!!.userName
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.textViewLogOut -> if (isCheckMultiClick()) logOut()
            R.id.textFriendRequest -> listener?.onFriendRequestClick()
            R.id.textViewWatchList -> listener?.onWatchListClick()
        }
    }

    private fun logOut() {
        val builder = AlertDialog.Builder(activity!!, R.style.alertDialog)
        builder.setMessage(getString(R.string.validate_log_out))
            .setTitle(getString(R.string.log_out))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, id ->
            signOutFacebook()
            signOutGoogle()
            viewModel.deleteUser()
            listener?.onLogOutClick()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun signOutFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return //already logged out
        }
        GraphRequest(AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    }

    private fun signOutGoogle() {
        googleSignInClient = GoogleSignIn.getClient(activity!!, viewModel.getGoogleSignInOptions())
        if (FirebaseAuth.getInstance() != null) {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient.signOut()
        }
    }

    interface OnSettingListener {
        fun onLogOutClick()
        fun onFriendRequestClick()
        fun onWatchListClick()
    }

    companion object {

        fun newInstance() = SettingFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}
