package com.framgia.oleo.screen.location

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.databinding.FragmentLocationBinding
import com.framgia.oleo.utils.OnItemRecyclerViewClick
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_location.locationToolbar
import kotlinx.android.synthetic.main.fragment_location.textViewNameFriend
import java.util.Locale

class LocationFragment : BaseFragment(), View.OnClickListener, OnItemRecyclerViewClick<Place> {

    private lateinit var viewModel: LocationViewModel
    private var binding by autoCleared<FragmentLocationBinding>()
    private var locationAdapter = LocationAdapter()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = LocationViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun setUpView() {
        viewModel.setAdapter(locationAdapter)
        locationAdapter.setListener(this)
        locationToolbar.setOnClickListener(this)
    }

    override fun bindView() {
        viewModel.getListLocation(arguments?.getString(ARGUMENT_ID_FRIEND)!!)
        textViewNameFriend.text = arguments?.getString(ARGUMENT_NAME_FRIEND)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.locationToolbar -> goBackFragment()
        }
    }

    override fun onItemClickListener(data: Place) {
        val appUri = String.format(
            Locale.ENGLISH,
            MAP_URI_SCHEME_APP,
            data.latitude!!.toFloat(),
            data.longitude!!.toFloat(),
            data.latitude!!.toFloat(),
            data.longitude!!.toFloat(),
            data.address.toString()
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUri))
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            val browserUri = String.format(
                Locale.ENGLISH,
                MAP_URI_SCHEME_BROWSER,
                data.address.toString()
            )
            val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(browserUri))
            startActivity(unrestrictedIntent)
        }
    }

    companion object {

        private const val MAP_URI_SCHEME_APP = "geo:%f,%f?z=17&q=%f,%f(%s)"
        private const val MAP_URI_SCHEME_BROWSER = "https://www.google.com/maps/search/?api=1&query=%s"
        private const val ARGUMENT_NAME_FRIEND = "ARGUMENT_NAME_FRIEND"
        private const val ARGUMENT_ID_FRIEND = "ARGUMENT_ID_FRIEND"

        fun newInstance(nameFriend: String, id: String) = LocationFragment().apply {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_NAME_FRIEND, nameFriend)
            bundle.putString(ARGUMENT_ID_FRIEND, id)
            arguments = bundle
        }
    }
}
