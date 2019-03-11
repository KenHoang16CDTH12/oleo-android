package com.framgia.oleo.screen.followed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FollowedFragmentBinding
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.followed_fragment.recyclerViewFollowed

class FollowedFragment : BaseFragment(), FollowedAdapter.OnItemViewListener {
    private lateinit var viewModel: FollowedViewModel
    private var followedFragmentBinding by autoCleared<FollowedFragmentBinding>()
    private var followedAdapter by autoCleared<FollowedAdapter>()

    override fun onUnfollowClick(user: User) {
        viewModel.deleteUserFollowed(user)
    }

    override fun setUpView() {
        setUpRecyclerView()
    }

    fun setUpRecyclerView() {
        followedAdapter = FollowedAdapter()
        recyclerViewFollowed.adapter = followedAdapter
        followedAdapter.setOnItemViewListener(this)
    }

    override fun bindView() {
        viewModel.getFollowedsOfUser()
        viewModel.usersLiveData.observe(this, Observer {
            followedAdapter.updateData(it)
        })
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FollowedViewModel.create(this, viewModelFactory)
        followedFragmentBinding = DataBindingUtil
            .inflate(inflater, R.layout.followed_fragment, container, false)
        followedFragmentBinding.viewModel = viewModel
        followedFragmentBinding.setLifecycleOwner(this)
        return followedFragmentBinding.root
    }

    companion object {
        fun newInstance() = FollowedFragment()
    }
}
