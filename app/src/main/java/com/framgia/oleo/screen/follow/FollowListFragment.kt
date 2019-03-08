package com.framgia.oleo.screen.follow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FollowListFragmentBinding
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.OnActionBarListener
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.follow_list_fragment.tabLayoutFollowList
import kotlinx.android.synthetic.main.follow_list_fragment.toolbarfollowList
import kotlinx.android.synthetic.main.follow_list_fragment.viewPagerFollowList
import kotlinx.android.synthetic.main.toolbar.view.toolbarCustom

class FollowListFragment : BaseFragment() {
    private lateinit var viewModel: FollowListViewModel
    private var binding by autoCleared<FollowListFragmentBinding>()
    private var followPagerAdapter by autoCleared<FollowPagerAdapter>()
    private var listener: OnActionBarListener? = null

    override fun setUpView() {
        setupTabLayoutWithViewPager()
        setupActionBar()
        setHasOptionsMenu(true)
    }

    override fun bindView() {
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = FollowListViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil
            .inflate(inflater, R.layout.follow_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = (context as? OnActionBarListener)!!
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setupActionBar() {
        listener!!.setupActionbar(
            toolbar = toolbarfollowList.toolbarCustom,
            title = getString(R.string.follow_list)
        )
    }

    private fun setupTabLayoutWithViewPager() {
        followPagerAdapter = FollowPagerAdapter(childFragmentManager, activity as MainActivity)
        viewPagerFollowList.adapter = followPagerAdapter
        tabLayoutFollowList.setupWithViewPager(viewPagerFollowList)
    }

    companion object {
        fun newInstance() = FollowListFragment()
    }
}
