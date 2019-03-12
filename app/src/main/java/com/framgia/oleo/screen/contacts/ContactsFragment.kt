package com.framgia.oleo.screen.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FragmentContactsBinding
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.material.snackbar.Snackbar

class ContactsFragment : BaseFragment() {

    private lateinit var viewModel: ContactsViewModel
    private var binding by autoCleared<FragmentContactsBinding>()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ContactsViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun setUpView() {
    }

    override fun bindView() {
        getDataLive()
        viewModel.getContacts()
    }

    private fun getDataLive() {
        viewModel.getMessageLiveDataError().observe(this, Observer { message ->
            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        })
    }

    companion object {
        fun newInstance() = ContactsFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}
