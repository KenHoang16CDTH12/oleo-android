package com.framgia.oleo.screen.follow

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.framgia.oleo.R
import com.framgia.oleo.screen.followed.FollowedFragment
import com.framgia.oleo.screen.followed.FollowingFragment
import com.framgia.oleo.screen.followed.WaitingFragment

class FollowPagerAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {
    private val tabTitles = context.resources.getStringArray(R.array.tab_titles)

    override fun getItem(position: Int): Fragment {
        when (position) {
            TAB_FOLLOW -> return FollowedFragment.newInstance()
            TAB_FOLLOWING -> return FollowingFragment.newInstance()
            else -> return WaitingFragment.newInstance()
        }
    }

    override fun getCount(): Int = PAGE_COUNT

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    companion object {
        const val PAGE_COUNT = 3
        const val TAB_FOLLOW = 0
        const val TAB_FOLLOWING = 1
    }
}
