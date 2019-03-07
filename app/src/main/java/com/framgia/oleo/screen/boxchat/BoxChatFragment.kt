package com.framgia.oleo.screen.boxchat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.databinding.FragmentBoxchatBinding
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_boxchat.buttonSend
import kotlinx.android.synthetic.main.fragment_boxchat.editSendMessage
import kotlinx.android.synthetic.main.fragment_boxchat.recyclerViewBoxChat
import kotlinx.android.synthetic.main.fragment_boxchat.swipeRefreshBoxChat
import kotlinx.android.synthetic.main.fragment_boxchat.textTitleChatBox
import kotlinx.android.synthetic.main.fragment_boxchat.toolbarBoxChat

@Suppress("DEPRECATION")
class BoxChatFragment : BaseFragment(), TextWatcher, View.OnClickListener {

    private lateinit var viewModel: BoxChatViewModel
    private var binding by autoCleared<FragmentBoxchatBinding>()
    private lateinit var boxChat: BoxChat
    private lateinit var adapter: BoxChatAdapter
    private var onMessageOptionListener: OnMessageOptionListener? = null
    private var isEnable = false

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = BoxChatViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_boxchat, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        if (context is OnMessageOptionListener) onMessageOptionListener = context
        super.onAttach(context)
    }

    override fun onDetach() {
        onMessageOptionListener = null
        super.onDetach()
    }

    override fun setUpView() {
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(toolbarBoxChat)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        (activity as MainActivity).supportActionBar!!.title = ""
        adapter = BoxChatAdapter()
        recyclerViewBoxChat.adapter = adapter
        adapter.setUser(viewModel.getUserId()!!)
        swipeRefreshBoxChat.isEnabled = false
        swipeRefreshBoxChat.setOnRefreshListener {
            viewModel.loadOldMessage(boxChat.id!!).observe(this, Observer { messagesList ->
                adapter.updateOldData(messagesList)
            })
            swipeRefreshBoxChat.isRefreshing = false
        }
        editSendMessage.addTextChangedListener(this)
        buttonSend.setOnClickListener(this)
    }

    override fun bindView() {
        boxChat = arguments!!.getParcelable(ARGUMENT_ROOM_ID)!!
        viewModel.getFriendImageProfile(boxChat.userFriendId!!).observe(this, Observer { imageProfile ->
            adapter.setUserFriendImage(imageProfile)
        })
        viewModel.getMessage(boxChat.id!!).observe(this, Observer { message ->
            adapter.updateData(message)
            recyclerViewBoxChat.smoothScrollToPosition(adapter.itemCount)
            swipeRefreshBoxChat.isEnabled = true
        })
        textTitleChatBox.text = boxChat.userFriendName
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.option_message, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
            R.id.menu_option -> onMessageOptionListener?.onMessageOptionClick(
                textTitleChatBox.text.toString(),
                boxChat.userFriendId.toString()
            )
        }
        return true
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        isEnable = onSetEnableButtonSend(s.toString())
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSend -> {
                if (isEnable) {
                    viewModel.sendMessage(
                        editSendMessage.text.toString(),
                        boxChat.id.toString(),
                        boxChat.userFriendId.toString()
                    )
                    editSendMessage.text.clear()
                }
            }
        }
    }

    private fun onSetEnableButtonSend(textPhone: String): Boolean {
        return if (textPhone.isNotBlank()) {
            buttonSend.isEnabled = true
            buttonSend.backgroundTintList = resources.getColorStateList(R.color.colorMessageBackground)
            true
        } else {
            buttonSend.isEnabled = false
            buttonSend.backgroundTintList = resources.getColorStateList(R.color.colorGrey400)
            false
        }
    }

    interface OnMessageOptionListener {
        fun onMessageOptionClick(userFriendName: String, id: String)
    }

    companion object {

        const val ARGUMENT_ROOM_ID = "ARGUMENT_ROOM_ID"

        fun newInstance(boxChat: BoxChat) = BoxChatFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(ARGUMENT_ROOM_ID, boxChat)
            arguments = bundle
        }
    }
}
