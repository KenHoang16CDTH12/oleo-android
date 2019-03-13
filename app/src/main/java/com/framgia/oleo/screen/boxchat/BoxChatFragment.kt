package com.framgia.oleo.screen.boxchat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.databinding.FragmentBoxchatBinding
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.OnActionBarListener
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.extension.isCheckMultiClick
import com.framgia.oleo.utils.liveData.autoCleared
import kotlinx.android.synthetic.main.fragment_boxchat.buttonSend
import kotlinx.android.synthetic.main.fragment_boxchat.editSendMessage
import kotlinx.android.synthetic.main.fragment_boxchat.recyclerViewBoxChat
import kotlinx.android.synthetic.main.fragment_boxchat.swipeRefreshBoxChat
import kotlinx.android.synthetic.main.fragment_boxchat.textTitleChatBox
import kotlinx.android.synthetic.main.fragment_boxchat.toolbarBoxChat

@Suppress("DEPRECATION")
class BoxChatFragment : BaseFragment(), TextWatcher, View.OnClickListener, View.OnTouchListener {

    private lateinit var viewModel: BoxChatViewModel
    private var binding by autoCleared<FragmentBoxchatBinding>()
    private lateinit var boxChat: BoxChat
    private lateinit var adapter: BoxChatAdapter
    private var onBoxChatListener: OnBoxChatListener? = null
    private var isEnable = false

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = BoxChatViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_boxchat, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        if (context is OnBoxChatListener) onBoxChatListener = context
        super.onAttach(context)
    }

    override fun onDestroyView() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onDestroyView()
    }

    override fun onDetach() {
        onBoxChatListener = null
        super.onDetach()
    }

    override fun setUpView() {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(toolbarBoxChat)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        (activity as MainActivity).supportActionBar!!.title = ""
        adapter = BoxChatAdapter()
        recyclerViewBoxChat.adapter = adapter
        adapter.setUser(viewModel.getUserId()!!)
        handleEvent()
    }

    override fun bindView() {
        boxChat = arguments!!.getParcelable(ARGUMENT_ROOM_ID)!!
        viewModel.getBoxChatName(boxChat.id!!)
        textTitleChatBox.text = boxChat.userFriendName
        viewModel.getFriendImageProfile(boxChat.id!!)
        viewModel.getMessage(boxChat.id!!)
        registerLiveData()
    }

    private fun registerLiveData() {
        viewModel.boxChatName.observe(this, Observer {
            textTitleChatBox.text = it
        })

        viewModel.newMessageLiveData.observe(this, Observer { message ->
            adapter.updateData(message)
            recyclerViewBoxChat.smoothScrollToPosition(adapter.itemCount)
            swipeRefreshBoxChat.isEnabled = true
        })

        viewModel.oldMessagesLiveData.observe(this, Observer { messagesList ->
            adapter.updateOldData(messagesList)
        })

        viewModel.imageProfileLiveData.observe(this, Observer { imageProfile ->
            adapter.setUserFriendImage(imageProfile)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.option_message, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
            R.id.menu_option -> onBoxChatListener?.onMessageOptionClick(userFriendId = boxChat.id!!)
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
                    viewModel.sendMessage(editSendMessage.text.toString(), boxChat)
                    editSendMessage.text.clear()
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v?.id == R.id.buttonSend) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    editSendMessage.isFocusable = true
                    editSendMessage.requestFocusFromTouch()
                    (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                        editSendMessage, InputMethodManager.SHOW_IMPLICIT
                    )
                    buttonSend.performClick()
                }
            }
        }
        return true
    }

    private fun handleEvent() {
        swipeRefreshBoxChat.isEnabled = false
        swipeRefreshBoxChat.setOnRefreshListener {
            viewModel.loadOldMessage(boxChat.id!!)
            swipeRefreshBoxChat.isRefreshing = false
        }
        editSendMessage.addTextChangedListener(this)
        editSendMessage.setOnEditorActionListener { textView, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    if (isEnable) buttonSend.performClick()
                    true
                }
                else -> false
            }
        }
        buttonSend.setOnClickListener(this)
        buttonSend.setOnTouchListener(this)
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

    interface OnBoxChatListener {
        fun onMessageOptionClick(userFriendId: String)
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
