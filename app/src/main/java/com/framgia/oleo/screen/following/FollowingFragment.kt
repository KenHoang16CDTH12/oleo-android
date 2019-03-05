package com.framgia.oleo.screen.followed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FollowingFragmentBinding
import com.framgia.oleo.utils.liveData.autoCleared

class FollowingFragment : BaseFragment() {
    private lateinit var viewModel: FollowingViewModel
    private var binding by autoCleared<FollowingFragmentBinding>()

    override fun setUpView() {
    }

    override fun bindView() {
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FollowingViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil
            .inflate(inflater, R.layout.follow_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    companion object {
        fun newInstance() = FollowedFragment()
    }
}
