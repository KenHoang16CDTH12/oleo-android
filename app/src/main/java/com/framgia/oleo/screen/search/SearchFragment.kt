package com.framgia.oleo.screen.search

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseActivity
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FragmentSearchBinding
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.screen.search.SearchAdapter.OnItemViewListener
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.extension.isCheckMultiClick
import com.framgia.oleo.utils.extension.showSnackBar
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_search.recyclerViewSearch
import kotlinx.android.synthetic.main.fragment_search.searchActionBar
import kotlinx.android.synthetic.main.fragment_search.searchView

class SearchFragment : BaseFragment(), SearchView.OnQueryTextListener, OnItemViewListener {
    private lateinit var viewModel: SearchViewModel
    private var binding by autoCleared<FragmentSearchBinding>()
    private var searchAdapter by autoCleared<SearchAdapter>()
    private lateinit var searchEditText: EditText
    private var inputMethodManager: InputMethodManager? = null
    private var activity: BaseActivity? = null
    private var lisetener:ImageSendMessageClickedListener?=null

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.searchByPhoneNumberOrName(query!!)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchByPhoneNumberOrName(newText!!)
        return true
    }

    override fun onAttach(context: Context?) {
        if (context is Activity)
            inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (context is BaseActivity) activity = context
        if (context is ImageSendMessageClickedListener) lisetener = context
        super.onAttach(context)
    }

    override fun onDetach() {
        inputMethodManager = null
        activity = null
        lisetener = null
        super.onDetach()
    }

    override fun setUpView() {
        setHasOptionsMenu(true)
        setUpSearchView()
        setUpRecyclerView()
    }

    override fun bindView() {
        registerLiveDataViewModel()
        viewModel.getUsers()
        viewModel.getFriend()
        searchAdapter.setUserId(viewModel.userId)
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = SearchViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_search, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    private fun setUpSearchView() {
        activity!!.setSupportActionBar(searchActionBar)
        activity!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activity!!.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        activity!!.supportActionBar!!.setDisplayShowTitleEnabled(false)
        searchView.isFocusable = true
        searchView.setIconifiedByDefault(false)
        searchView.requestFocusFromTouch()
        searchView.setOnQueryTextListener(this)
        searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            ContextCompat.getColor(activity!!.applicationContext, R.color.colorDefault)
        )
        searchEditText.setHintTextColor(
            ContextCompat.getColor(activity!!.applicationContext, R.color.colorGrey500)
        )
        inputMethodManager!!.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        searchView.findViewById<View>(R.id.search_plate).setBackgroundColor(Color.TRANSPARENT)
    }

    private fun registerLiveDataViewModel() {
        viewModel.getBoxChatLiveData().observe(this, Observer { boxChat ->
            lisetener!!.onOpenBoxChat(boxChat)
        })

        viewModel.onGetFriendList.observe(this, Observer { searchAdapter.setUserFriend(it) })

        viewModel.usersSeachResult.observe(this, Observer { searchAdapter.updateData(it) })

        viewModel.onAddFriendRequest.observe(this, Observer { view!!.showSnackBar(it) })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search, menu)
        searchEditText.text.clear()
    }

    private fun setUpRecyclerView() {
        searchAdapter = SearchAdapter()
        recyclerViewSearch.adapter = searchAdapter
        searchAdapter.setOnItemViewListener(this)
    }

    override fun onImageAddFriendClicked(user: User) {
        if (isCheckMultiClick()) viewModel.addFriendRequest(user)
    }

    override fun onImageSendMessageClicked(user: User) {
        if (isCheckMultiClick()) viewModel.getBoxChat(user)
    }

    override fun onImageUserProfileClicked(user: User) {
        //todo
    }

    interface ImageSendMessageClickedListener {
        fun onOpenBoxChat(data: BoxChat)
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
