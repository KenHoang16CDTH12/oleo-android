package com.framgia.oleo.screen.friendrequest

import android.content.Context
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
import com.framgia.oleo.utils.OnActionBarListener
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.friend_requests_fragment.recyclerViewFriendRequests
import kotlinx.android.synthetic.main.toolbar.toolbarCustom
import kotlinx.android.synthetic.main.toolbar.view.toolbarCustom

class FriendRequestsFragment : BaseFragment(), OnItemViewListener {

    private lateinit var viewModel: FriendRequestsViewModel
    private var binding by autoCleared<FriendRequestsFragmentBinding>()
    private var friendRequestAdapter by autoCleared<FriendRequestAdapter>()
    private var listener: OnActionBarListener? = null

    override fun onConFirmClick(friendRequest: FriendRequest) {
        viewModel.confirmFriendRequest(friendRequest)
    }

    override fun onDeleteClick(friendRequest: FriendRequest) {
        viewModel.deleteFriendRequest(friendRequest)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = (context as? OnActionBarListener)!!
    }

    override fun setUpView() {
        setUpRecyclerView()
        setupActionBar()
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setupActionBar() {
        listener!!.setupActionbar(toolbarCustom.toolbarCustom, getString(R.string.friend_requests))
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
