package com.framgia.oleo.screen.friendrequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.databinding.FriendRequestsFragmentBinding
import com.framgia.oleo.screen.friendrequest.FriendRequestAdapter.OnItemViewListener
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.friend_requests_fragment.recyclerViewFriendRequests
import kotlinx.android.synthetic.main.friend_requests_fragment.toolbarFriendRequest
import kotlinx.android.synthetic.main.toolbar.view.textTitleToolbar
import kotlinx.android.synthetic.main.toolbar.view.toolbarCustom

class FriendRequestsFragment : BaseFragment(), OnItemViewListener {
    override fun onConFirmClick(friendRequest: FriendRequest) {
        viewModel.confirmFriendRequest(friendRequest)
    }

    override fun onDeleteClick(friendRequest: FriendRequest) {
        viewModel.deleteFriendRequest(friendRequest)
    }

    private lateinit var viewModel: FriendRequestsViewModel
    private var binding by autoCleared<FriendRequestsFragmentBinding>()
    private var friendRequestAdapter by autoCleared<FriendRequestAdapter>()
    override fun setUpView() {
        setupActionBar()
        setUpRecyclerView()
        setHasOptionsMenu(true)
    }

    private fun setupActionBar() {
        val toolbar = toolbarFriendRequest.toolbarCustom
        toolbar.textTitleToolbar.text = activity!!.getString(R.string.friend_requests)
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun bindView() {
        viewModel.getUsersByFriendRequests()
        viewModel.usersLiveData.observe(this, Observer {
            friendRequestAdapter.updateData(it)
        })
        viewModel.friendRequestsLiveData.observe(this, Observer {
            friendRequestAdapter.setUsers(it)
        })
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FriendRequestsViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil
            .inflate(inflater, R.layout.friend_requests_fragment, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView() {
        friendRequestAdapter = FriendRequestAdapter()
        recyclerViewFriendRequests.adapter = friendRequestAdapter
        friendRequestAdapter.setOnItemViewListener(this)
    }

    companion object {
        fun newInstance() = FriendRequestsFragment()
    }
}
