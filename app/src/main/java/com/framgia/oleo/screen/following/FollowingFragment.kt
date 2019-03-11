package com.framgia.oleo.screen.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FollowingFragmentBinding
import com.framgia.oleo.screen.following.FollowingAdapter.OnItemViewListener
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.following_fragment.recyclerViewFollowing

class FollowingFragment : BaseFragment(), OnItemViewListener {
    private lateinit var viewModel: FollowingViewModel
    private var followingFragmentBinding by autoCleared<FollowingFragmentBinding>()
    private var followingAdapter by autoCleared<FollowingAdapter>()

    override fun onBlockClick(user: User) {
        viewModel.changeStatusOfFollowRequest(user)
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
        viewModel.usersLiveData.observe(this, Observer {
            followingAdapter.updateData(it)
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
