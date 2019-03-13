package com.framgia.oleo.screen.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.databinding.FragmentContactsBinding
import com.framgia.oleo.screen.boxchat.BoxChatFragment
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.OnItemRecyclerViewClick
import com.framgia.oleo.utils.extension.addFragmentToActivity
import com.framgia.oleo.utils.extension.isCheckMultiClick
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_contacts.recyclerViewContacts

class ContactsFragment : BaseFragment(), OnItemRecyclerViewClick<Friend> {

    private lateinit var viewModel: ContactsViewModel
    private var binding by autoCleared<FragmentContactsBinding>()
    private lateinit var adapter: ContactsAdapter

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
        adapter = ContactsAdapter(this)
        recyclerViewContacts.adapter = adapter
    }

    override fun bindView() {
        getDataLive()
        viewModel.getContacts()
    }

    override fun onItemClickListener(data: Friend) {
        if (isCheckMultiClick())
            viewModel.getBoxChat(data.user!!).observe(this, Observer { boxChat ->
                (activity!! as MainActivity).addFragmentToActivity(R.id.containerMain, BoxChatFragment.newInstance
                (boxChat))
            })
    }

    private fun getDataLive() {
        viewModel.getMessageLiveDataError().observe(this, Observer { message ->
            Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
        })

        viewModel.getLiveDataContacts().observe(this, Observer { data ->
            adapter.updateData(data)
        })
    }

    companion object {
        fun newInstance() = ContactsFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}
