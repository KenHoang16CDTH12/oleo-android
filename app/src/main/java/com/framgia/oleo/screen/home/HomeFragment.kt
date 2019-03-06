package com.framgia.oleo.screen.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FragmentHomeBinding
import com.framgia.oleo.screen.messages.MessagesFragment
import com.framgia.oleo.screen.setting.SettingFragment
import com.framgia.oleo.utils.extension.showToast
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.navigation
import kotlinx.android.synthetic.main.fragment_home.viewPager

class HomeFragment : BaseFragment(), BottomNavigationView.OnNavigationItemSelectedListener, OnPageChangeListener {
    private lateinit var viewModel: HomeViewModel
    private var binding by autoCleared<FragmentHomeBinding>()
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    private var checkLocationListener: OnCallBackLocationListener? = null

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = HomeViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun setUpView() {
        onCheckPermissionLocation()
    }

    override fun bindView() {
        // Add Show View
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnCallBackLocationListener) checkLocationListener = context
    }

    override fun onDetach() {
        super.onDetach()
        checkLocationListener = null
    }

    private fun initView() {
        // SetUp View
        val pagerAdapter = ViewPagerAdapter(childFragmentManager)
        pagerAdapter.addFragment(MessagesFragment.newInstance())
        //Todo replace later
        pagerAdapter.addFragment(Fragment())
        pagerAdapter.addFragment(Fragment())
        pagerAdapter.addFragment(SettingFragment.newInstance())
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(this)
        navigation.setOnNavigationItemSelectedListener(this)
        checkLocationListener?.onCallBackLocation(true)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.navigation_messages -> {
                viewPager.setCurrentItem(TAB_MESSAGES, false)
                return true
            }
            R.id.navigation_contacts -> {
                viewPager.setCurrentItem(TAB_CONTACTS, false)
                return true
            }
            R.id.navigation_groups -> {
                viewPager.setCurrentItem(TAB_GROUPS, false)
                return true
            }
            R.id.navigation_more -> {
                viewPager.setCurrentItem(TAB_MORE, false)
                return true
            }
        }
        return true
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        when (position) {
            TAB_MESSAGES -> navigation.selectedItemId = R.id.navigation_messages
            TAB_CONTACTS -> navigation.selectedItemId = R.id.navigation_contacts
            TAB_GROUPS -> navigation.selectedItemId = R.id.navigation_groups
            else -> navigation.selectedItemId = R.id.navigation_more
        }
    }

    private fun onCheckPermissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) initView()
            else requestPermissions(permissions, PERMISSION_REQUEST)
        } else initView()
    }

    private fun checkPermission(permissions: Array<String>): Boolean {
        var isCheckedLocationPermission = true
        for (i in permissions.indices) {
            if (activity?.checkCallingOrSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED) {
                isCheckedLocationPermission = false
            }
        }
        return isCheckedLocationPermission
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var isCheckedPermission = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isCheckedPermission = false
                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        context!!.showToast(PERMISSION_DENIED)
                    } else {
                        //Todo update later
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", activity?.packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, REQUEST_APP_DETAILS)
                    }
                }
            }
            if (isCheckedPermission) {
                initView()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_APP_DETAILS) {
            onCheckPermissionLocation()
        }
    }

    interface OnCallBackLocationListener {
        fun onCallBackLocation(isCheckPermission: Boolean = false)
    }

    companion object {

        private const val REQUEST_APP_DETAILS = 99
        private const val PERMISSION_REQUEST = 10
        private const val PERMISSION_DENIED = "Permission denied"
        private const val TAB_MESSAGES = 0
        private const val TAB_CONTACTS = 1
        private const val TAB_GROUPS = 2
        private const val TAB_MORE = 3

        fun newInstance() = HomeFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}
