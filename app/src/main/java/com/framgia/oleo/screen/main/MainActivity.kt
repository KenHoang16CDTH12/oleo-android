package com.framgia.oleo.screen.main

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseActivity
import com.framgia.oleo.data.service.LocationService
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.databinding.ActivityMainBinding
import com.framgia.oleo.screen.boxchat.BoxChatFragment
import com.framgia.oleo.screen.boxchat.BoxChatFragment.OnBoxChatListener
import com.framgia.oleo.screen.follow.FollowListFragment
import com.framgia.oleo.screen.forgotpassword.ForgotPasswordFragment.OnForgotPasswordListener
import com.framgia.oleo.screen.forgotpassword.ResetPasswordFragment
import com.framgia.oleo.screen.friendrequest.FriendRequestsFragment
import com.framgia.oleo.screen.home.HomeFragment
import com.framgia.oleo.screen.home.HomeFragment.OnCallBackLocationListener
import com.framgia.oleo.screen.login.LoginFragment
import com.framgia.oleo.screen.messages.MessageOptionFragment
import com.framgia.oleo.screen.messages.MessagesFragment
import com.framgia.oleo.screen.search.SearchFragment
import com.framgia.oleo.screen.setting.SettingFragment.OnSettingListener
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.OnActionBarListener
import com.framgia.oleo.utils.extension.addFragmentToActivity
import com.framgia.oleo.utils.extension.clearAllFragment
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.extension.replaceFragmentInActivity
import com.framgia.oleo.utils.extension.showToast
import kotlinx.android.synthetic.main.toolbar.view.textTitleToolbar

class MainActivity : BaseActivity(), MessagesFragment.OnSearchListener, OnSettingListener,
    OnBoxChatListener, OnCallBackLocationListener, OnActionBarListener, OnForgotPasswordListener,
    SearchFragment.ImageSendMessageClickedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var currentFragment: Fragment
    private var isDoubleTapBack = false
    private val loginFragment = LoginFragment.newInstance()
    private val homeFragment = HomeFragment.newInstance()
    private val searchFragment = SearchFragment.newInstance()
    private val friendRequestsFragment = FriendRequestsFragment.newInstance()
    private val followListFragment = FollowListFragment.newInstance()
    private lateinit var inputMethodManager: InputMethodManager
    private var isCheckPermissionLocation = false

    override fun onCreateView(savedInstanceState: Bundle?) {
        viewModel = MainViewModel.create(this, viewModelFactory)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel
    }

    override fun setUpView() {
        registerIsCheckUserData()
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun bindView() {
        viewModel.getUserLocal()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                if (currentFocus != null && (currentFocus is EditText || currentFocus is SearchView)) {
                    val rect = Rect()
                    currentFocus!!.getGlobalVisibleRect(rect)
                    if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        inputMethodManager.hideSoftInputFromWindow(
                            window.decorView.rootView.windowToken, HIDE_INPUT_FLAG
                        )
                        currentFocus!!.clearFocus()
                    }
                }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        if (goBackFragment()) return
        if (isDoubleTapBack) {
            finish()
            return
        }
        isDoubleTapBack = true
        showToast(getString(R.string.click_again))
        Handler().postDelayed(
            { isDoubleTapBack = false },
            Constant.MAX_TIME_DOUBLE_CLICK_EXIT.toLong()
        )
    }

    override fun onSearchClick() {
        addFragmentToActivity(R.id.containerMain, searchFragment)
    }

    override fun onLogOutClick() {
        LocationService.isCheckLogout = true
        stopService(Intent(applicationContext, LocationService::class.java))
        clearAllFragment()
        replaceFragmentInActivity(R.id.containerMain, loginFragment, false)
    }

    override fun onFriendRequestClick() {
        addFragmentToActivity(R.id.containerMain, friendRequestsFragment)
    }

    override fun onMessageOptionClick(userFriendId: String) {
        addFragmentToActivity(R.id.containerMain, MessageOptionFragment.newInstance(userFriendId))
    }

    override fun onWatchListClick() {
        addFragmentToActivity(R.id.containerMain, followListFragment)
    }

    override fun onCallBackLocation(isCheckPermission: Boolean) {
        isCheckPermissionLocation = if (isCheckPermission) {
            startService(Intent(applicationContext, LocationService::class.java))
            true
        } else false
    }

    override fun setupActionbar(toolbar: Toolbar, title: String) {
        toolbar.textTitleToolbar.text = title
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onVerifyCodeClick(phoneNumber: String) {
        supportFragmentManager.popBackStack()
        addFragmentToActivity(R.id.containerMain, ResetPasswordFragment.newInstance(phoneNumber))
    }

    override fun onOpenBoxChat(data: BoxChat) {
        supportFragmentManager.popBackStack()
        addFragmentToActivity(R.id.containerMain, BoxChatFragment.newInstance(data))
    }

    private fun registerIsCheckUserData() {
        viewModel.isCheckUser().observe(this, Observer { result ->
            if (result) {
                replaceFragmentInActivity(R.id.containerMain, homeFragment)
                currentFragment = homeFragment
                supportActionBar?.show()
            } else {
                replaceFragmentInActivity(R.id.containerMain, loginFragment)
                currentFragment = loginFragment
                supportActionBar?.hide()
            }
        })
    }

    companion object {
        private const val HIDE_INPUT_FLAG = 0
    }
}
