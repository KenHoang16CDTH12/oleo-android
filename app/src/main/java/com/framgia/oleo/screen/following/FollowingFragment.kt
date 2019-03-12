package com.framgia.oleo.screen.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.databinding.FollowingFragmentBinding
import com.framgia.oleo.screen.following.FollowingAdapter.OnItemViewListener
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.following_fragment.recyclerViewFollowing

class FollowingFragment : BaseFragment(), OnItemViewListener {
    private lateinit var viewModel: FollowingViewModel
    private var followingFragmentBinding by autoCleared<FollowingFragmentBinding>()
    private var followingAdapter by autoCleared<FollowingAdapter>()

    override fun onBlockClick(followRequest: FollowRequest) {
        viewModel.changeStatusOfFollowRequest(followRequest)
    }

    override fun setUpView() {
        setUpRecyclerView()
    }

    fun setUpRecyclerView() {
        followingAdapter = FollowingAdapter()
        recyclerViewFollowing.adapter = followingAdapter
        followingAdapter.setOnItemViewListener(this)
    }

    override fun bindView() {
        viewModel.getFollowingRequestOfUser()
        viewModel.onChildAddedEvent.observe(this, Observer {
            followingAdapter.addFollowRequest(it)
        })
        viewModel.onChildChangedEvent.observe(this, Observer {
            followingAdapter.changeFollowRequest(it)
        })
        viewModel.onChildRemovedEvent.observe(this, Observer {
            followingAdapter.removeFollowRequest(it)
        })
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FollowingViewModel.create(this, viewModelFactory)
        followingFragmentBinding = DataBindingUtil
            .inflate(inflater, R.layout.following_fragment, container, false)
        followingFragmentBinding.viewModel = viewModel
        followingFragmentBinding.setLifecycleOwner(this)
        return followingFragmentBinding.root
    }

    companion object {
        fun newInstance() = FollowingFragment()
    }
}
