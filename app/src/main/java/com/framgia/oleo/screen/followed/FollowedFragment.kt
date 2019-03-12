package com.framgia.oleo.screen.followed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.Followed
import com.framgia.oleo.databinding.FollowedFragmentBinding
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.followed_fragment.recyclerViewFollowed

class FollowedFragment : BaseFragment(), FollowedAdapter.OnItemViewListener {
    private lateinit var viewModel: FollowedViewModel
    private var followedFragmentBinding by autoCleared<FollowedFragmentBinding>()
    private var followedAdapter by autoCleared<FollowedAdapter>()

    override fun onUnfollowClick(followed: Followed) {
        viewModel.deleteUserFollowed(followed)
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
        viewModel.onChildAddedEvent.observe(this, Observer {
            followedAdapter.addFollowRequest(it)
        })
        viewModel.onChildChangedEvent.observe(this, Observer {
            followedAdapter.changeFollowRequest(it)
        })
        viewModel.onChildRemovedEvent.observe(this, Observer {
            followedAdapter.removeFollowRequest(it)
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
