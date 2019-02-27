package com.framgia.oleo.screen.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FragmentSearchBinding
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private lateinit var viewModel: SearchViewModel
    private var binding by autoCleared<FragmentSearchBinding>()
    private var searchAdapter by autoCleared<SearchAdapter>()

    override fun onQueryTextSubmit(query: String?): Boolean {
        viewModel.getUserByPhoneNumber(query!!)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.getUserByPhoneNumber(newText!!)
        return true
    }

    override fun setUpView() {
        setUpSearchView()
        setUpRecyclerView()
        imageBack.setOnClickListener { fragmentManager?.popBackStack() }
    }

    override fun bindView() {
        viewModel.getUsers()
        viewModel.usersSeachResult.observe(this, Observer {
            searchAdapter.updateData(it)
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
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        searchView.isFocusable = true
        searchView.isIconified = false
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
    }

    private fun setUpRecyclerView() {
        searchAdapter = SearchAdapter()
        recyclerViewSearch.adapter = searchAdapter
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
