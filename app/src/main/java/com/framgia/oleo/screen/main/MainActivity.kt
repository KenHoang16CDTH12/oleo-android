package com.framgia.oleo.screen.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseActivity
import com.framgia.oleo.databinding.ActivityMainBinding
import com.framgia.oleo.screen.boxchat.BoxChatFragment.OnMessageOptionListener
import com.framgia.oleo.screen.home.HomeFragment
import com.framgia.oleo.screen.login.LoginFragment
import com.framgia.oleo.screen.messages.MessageOptionFragment
import com.framgia.oleo.screen.messages.MessagesFragment
import com.framgia.oleo.screen.search.SearchFragment
import com.framgia.oleo.screen.setting.SettingFragment.OnLogOutListener
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.extension.*


class MainActivity : BaseActivity(), MessagesFragment.OnSearchListener, OnLogOutListener, OnMessageOptionListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var currentFragment: Fragment
    private var isDoubleTapBack = false
    private val loginFragment = LoginFragment.newInstance()
    private val homeFragment = HomeFragment.newInstance()
    private val searchFragment = SearchFragment.newInstance()
    private lateinit var inputMethodManager: InputMethodManager
    override fun onCreateView(savedInstanceState: Bundle?) {
        viewModel = MainViewModel.create(this, viewModelFactory)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = viewModel
    }

    override fun setUpView() {
        registerIsCheckUserData()
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun bindView() {
        viewModel.getUserLocal()
    }

    //Todo edit later
//    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> if (currentFocus!! is EditText) {
//                currentFocus!!.clearFocus()
//                inputMethodManager.hideSoftInputFromWindow(
//                    currentFocus!!.windowToken,
//                    InputMethodManager.HIDE_NOT_ALWAYS
//                )
//            }else{
//
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }

    override fun onBackPressed() {
        if (goBackFragment()) return
        if (isDoubleTapBack) {
            finish()
            return
        }
        isDoubleTapBack = true
        showToast(getString(R.string.click_again))
        Handler().postDelayed({
            isDoubleTapBack = false
        }, Constant.MAX_TIME_DOUBLE_CLICK_EXIT.toLong())
    }

    override fun onSearchClick() {
        addFragmentToActivity(R.id.containerMain, searchFragment)
    }

    override fun onLogOutClick() {
        clearAllFragment()
        replaceFragmentInActivity(R.id.containerMain, loginFragment, false)
    }

    override fun onMessageOptionClick(userFriendName: String) {
        addFragmentToActivity(R.id.containerMain, MessageOptionFragment.newInstance(userFriendName))
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
}
