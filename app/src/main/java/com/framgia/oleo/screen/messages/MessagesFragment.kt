package com.framgia.oleo.screen.messages

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseActivity
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.databinding.FragmentMessagesBinding
import com.framgia.oleo.screen.boxchat.BoxChatFragment
import com.framgia.oleo.utils.OnItemRecyclerViewClick
import com.framgia.oleo.utils.extension.addFragmentToActivity
import com.framgia.oleo.utils.extension.isCheckMultiClick
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_messages.recyclerViewMessages
import kotlinx.android.synthetic.main.fragment_messages.textSearchMessage
import kotlinx.android.synthetic.main.fragment_search.searchActionBar

class MessagesFragment : BaseFragment(), OnItemRecyclerViewClick<BoxChat>, View.OnClickListener {
    private var listener: OnSearchListener? = null
    private lateinit var viewModel: MessagesViewModel
    private var binding by autoCleared<FragmentMessagesBinding>()
    private var messagesAdapter by autoCleared<MessagesAdapter>()
    private var activity: BaseActivity? = null

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = MessagesViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSearchListener) listener = context
        if (context is BaseActivity) activity = context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        activity = null
    }

    override fun setUpView() {
        // SetUp View
        setHasOptionsMenu(true)
        activity!!.setSupportActionBar(searchActionBar)
        activity!!.supportActionBar!!.setDisplayShowTitleEnabled(false)
        messagesAdapter = MessagesAdapter()
        recyclerViewMessages.adapter = messagesAdapter
        viewModel.setAdapter(messagesAdapter)
        messagesAdapter.setListener(this)
        textSearchMessage.setOnClickListener(this)
        registerLiveData()
    }

    override fun bindView() {
        // Add Show View
        viewModel.getAllBoxChat()
    }

    override fun onItemClickListener(data: BoxChat) {
        //Open Chat Screen
        if (isCheckMultiClick())
            activity!!.addFragmentToActivity(R.id.containerMain, BoxChatFragment.newInstance(data))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.textSearchMessage -> if (isCheckMultiClick()) listener?.onSearchClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        activity!!.menuInflater.inflate(com.framgia.oleo.R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.newMessage -> {
                //Add New Message
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerLiveData(){
        viewModel.onBoxChatAdded.observe(this, Observer {
            messagesAdapter.addData(it)
        })

        viewModel.onBoxChatRemoved.observe(this, Observer {
            messagesAdapter.removeData(it)
        })
    }

    interface OnSearchListener {
        fun onSearchClick()
    }

    companion object {
        fun newInstance() = MessagesFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}
