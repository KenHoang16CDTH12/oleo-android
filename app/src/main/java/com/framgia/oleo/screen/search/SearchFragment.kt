package com.framgia.oleo.screen.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FragmentSearchBinding
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.screen.search.SearchAdapter.OnItemViewListener
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.extension.showSnackBar
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_search.recyclerViewSearch
import kotlinx.android.synthetic.main.fragment_search.searchActionBar
import kotlinx.android.synthetic.main.fragment_search.searchView

class SearchFragment : BaseFragment(), SearchView.OnQueryTextListener, OnItemViewListener {
    private lateinit var viewModel: SearchViewModel
    private var binding by autoCleared<FragmentSearchBinding>()
    private var searchAdapter by autoCleared<SearchAdapter>()

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.searchByPhoneNumberOrName(query!!)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchByPhoneNumberOrName(newText!!)
        return true
    }

    override fun setUpView() {
        setUpSearchView()
        setUpRecyclerView()
        setHasOptionsMenu(true)
    }

    override fun bindView() {
        viewModel.getUsers()
        searchAdapter.setUserId(viewModel.userId)
        viewModel.usersSeachResult.observe(this, Observer {
            searchAdapter.updateData(it)
        })
        viewModel.onAddFriendRequest.observe(this, Observer {
            view!!.showSnackBar(it)
        })
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
        (activity as MainActivity).setSupportActionBar(searchActionBar)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        searchView.isFocusable = true
        searchView.setIconifiedByDefault(false)
        searchView.requestFocusFromTouch()
        searchView.setOnQueryTextListener(this)
        val searchEditText = searchView
            .findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            ContextCompat.getColor(activity!!.applicationContext, R.color.colorDefault)
        )
        searchEditText.setHintTextColor(
            ContextCompat.getColor(activity!!.applicationContext, R.color.colorGrey500)
        )
        searchEditText.text.clear()
        searchView.findViewById<View>(R.id.search_plate).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search, menu)
    }

    private fun setUpRecyclerView() {
        searchAdapter = SearchAdapter()
        recyclerViewSearch.adapter = searchAdapter
        searchAdapter.setOnItemViewListener(this)
    }

    override fun onImageAddFriendClicked(user: User) {
        viewModel.addFriendRequest(user)
    }

    override fun onImageSendMessageClicked(user: User) {
        //todo
    }

    override fun onImageUserProfileClicked(user: User) {
        //todo
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
