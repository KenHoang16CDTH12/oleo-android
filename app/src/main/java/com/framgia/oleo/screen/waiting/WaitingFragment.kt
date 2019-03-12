package com.framgia.oleo.screen.waiting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.databinding.WaitingFragmentBinding
import com.framgia.oleo.screen.waiting.WaitingAdapter.OnItemViewListener
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.waiting_fragment.recyclerViewWating

class WaitingFragment : BaseFragment(), OnItemViewListener {
    private lateinit var viewModel: WaitingViewModel
    private var waitingFragmentBinding by autoCleared<WaitingFragmentBinding>()
    private var waitingAdapter by autoCleared<WaitingAdapter>()

    override fun onAcceptClick(followRequest: FollowRequest) {
        viewModel.changeStatusOfFollowRequest(followRequest)
    }

    override fun setUpView() {
        setUpRecyclerView()
    }

    fun setUpRecyclerView() {
        waitingAdapter = WaitingAdapter()
        recyclerViewWating.adapter = waitingAdapter
        waitingAdapter.setOnItemViewListener(this)
    }

    override fun bindView() {
        viewModel.getFollowRequest()
        viewModel.onChildAddedEvent.observe(this, Observer {
            waitingAdapter.addFollowRequest(it)
        })
        viewModel.onChildChangedEvent.observe(this, Observer {
            waitingAdapter.changeFollowRequest(it)
        })
        viewModel.onChildRemovedEvent.observe(this, Observer {
            waitingAdapter.removeFollowRequest(it)
        })
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = WaitingViewModel.create(this, viewModelFactory)
        waitingFragmentBinding = DataBindingUtil
            .inflate(inflater, R.layout.waiting_fragment, container, false)
        waitingFragmentBinding.viewModel = viewModel
        waitingFragmentBinding.setLifecycleOwner(this)
        return waitingFragmentBinding.root
    }

    companion object {
        fun newInstance() = WaitingFragment()
    }
}
