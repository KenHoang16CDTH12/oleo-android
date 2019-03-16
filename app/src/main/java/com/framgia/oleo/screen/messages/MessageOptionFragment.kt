package com.framgia.oleo.screen.messages

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FragmentOptionMessageBinding
import com.framgia.oleo.screen.location.LocationFragment
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.OnActionBarListener
import com.framgia.oleo.utils.extension.addFragment
import com.framgia.oleo.utils.extension.clearAllFragment
import com.framgia.oleo.utils.extension.goBackFragment
import com.framgia.oleo.utils.extension.showSnackBar
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_option_message.layoutHeader
import kotlinx.android.synthetic.main.fragment_option_message.textViewLocationList
import kotlinx.android.synthetic.main.fragment_option_message.textViewRemoveBox
import kotlinx.android.synthetic.main.fragment_option_message.textViewUnFriend
import kotlinx.android.synthetic.main.fragment_option_message.textViewRename
import kotlinx.android.synthetic.main.fragment_option_message.toolbarOption
import kotlinx.android.synthetic.main.fragment_option_message_header.textViewNameUser
import kotlinx.android.synthetic.main.fragment_option_message_header.view.textViewNameUser
import kotlinx.android.synthetic.main.toolbar.view.toolbarCustom

class MessageOptionFragment : BaseFragment(), View.OnClickListener, OnMessageOptionListener {
    private lateinit var viewModel: MessageOptionViewModel
    private var binding by autoCleared<FragmentOptionMessageBinding>()
    private var listener: OnActionBarListener? = null
    private lateinit var dialog: AlertDialog
    private var isAlreadyFriend: Boolean = false
    private var userFriend : User? = null

    override fun onFollowClick(userFriend: User) {
        viewModel.onFollowClick(userFriend)
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = MessageOptionViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_option_message, container, false)
        binding.listener = this
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun setUpView() {
        setupActionBar()
        setHasOptionsMenu(true)
        setupProgressBar()
    }

    override fun bindView() {
        registerLiveData()
        textViewLocationList.setOnClickListener(this)
        textViewRename.setOnClickListener(this)
        textViewUnFriend.setOnClickListener(this)
        textViewRemoveBox.setOnClickListener(this)
        val userFriendId = arguments?.getString(ARGUMENT_USER_ID)
        viewModel.checkFriendByUserId(userFriendId!!)
        viewModel.getUserFriend(userFriendId)
        viewModel.getBoxChatName(userFriendId)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = (context as? OnActionBarListener)!!
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.textViewLocationList -> {
                dialog.show()
                viewModel.getFollowRequestOfUser(arguments?.getString(ARGUMENT_USER_ID)!!)
            }
            R.id.textViewUnFriend ->
                if (isAlreadyFriend) {
                    showUnFriendDialog()
                } else {
                    viewModel.addFriendRequest(binding.user!!)
                }
            R.id.textViewRename -> onShowDiaLogUpdateNameFriend()
            R.id.textViewRemoveBox -> showRemoveBoxChatDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> goBackFragment()
        }
        return true
    }

    private fun setupActionBar() {
        listener!!.setupActionbar(
            toolbar = toolbarOption.toolbarCustom,
            title = activity!!.getString(R.string.follow_list)
        )
    }

    private fun setupProgressBar() {
        val builder = AlertDialog.Builder(activity)
        builder.setView(R.layout.progress_dialog)
        builder.setCancelable(true)
        dialog = builder.create()
    }

    private fun registerLiveData() {
        viewModel.userFriend.observe(this, Observer {
            binding.layoutHeader.user = it
            binding.user = it
            userFriend = it
        })

        viewModel.onNavigateEvent.observe(this, Observer {
            dialog.dismiss()
            addFragment(
                R.id.containerMain, LocationFragment.newInstance(
                    textViewNameUser.text.toString(),
                    arguments?.getString(ARGUMENT_USER_ID)!!
                ), true
            )
        })

        viewModel.onMessageEvent.observe(this, Observer {
            view!!.showSnackBar(it)
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        })

        viewModel.isFriendAlready.observe(this, Observer {
            isAlreadyFriend = it
            if (it) {
                updateFriendStatus(getString(R.string.unfriend), R.drawable.ic_unfriend)
            } else {
                updateFriendStatus(getString(R.string.add_friend), R.drawable.ic_add_friend_message_option)
            }
        })

        viewModel.onAddFriendRequest.observe(this, Observer {
            view!!.showSnackBar(it)
        })

        viewModel.resultError.observe(this, Observer {
            Snackbar.make(view!!, it, Snackbar.LENGTH_SHORT).show()
        })

        viewModel.isBoxChatDeleted.observe(this, Observer {
            if (it){
                (activity as MainActivity).clearAllFragment()
            }else{
                view!!.showSnackBar(getString(R.string.delete_box_chat_failed))
            }
        })

        viewModel.boxChatName.observe(this, Observer {
            layoutHeader.textViewNameUser.text = it
        })
    }

    private fun showUnFriendDialog() {
        AlertDialog.Builder(context, R.style.alertDialog).apply {
            setMessage(context.getString(R.string.unfriend_message, binding.user!!.userName))
            setPositiveButton(context.getString(R.string.ok)) { dialog, which -> viewModel.deleteFriend(binding.user!!.id) }
            setNegativeButton(context.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
        }.create().show()
    }

    private fun showRemoveBoxChatDialog() {
        AlertDialog.Builder(context, R.style.alertDialog).apply {
            setMessage(getString(R.string.delete_box_chat_message))
            setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                viewModel.deleteBoxChat(binding.user!!.id)}
            setNegativeButton(context.getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
        }.create().show()
    }

    private fun updateFriendStatus(title: String, drawable: Int) {
        textViewUnFriend.text = title
        textViewUnFriend.setCompoundDrawablesWithIntrinsicBounds(
            drawable,
            RESOURCE_VALUE,
            RESOURCE_VALUE,
            RESOURCE_VALUE
        )
    }

    private fun onShowDiaLogUpdateNameFriend() {
        val textNameUser = textViewNameUser.text.toString()
        val inputText = TextInputLayout(context)
        inputText.setPadding(resources.getDimensionPixelOffset(R.dimen.dp_20),RESOURCE_VALUE,resources
            .getDimensionPixelOffset(R.dimen.dp_20),RESOURCE_VALUE)
        val editText = EditText(context)
        editText.text = Editable.Factory.getInstance().newEditable(textNameUser)
        editText.setLines(MAX_LINE_TEXT)
        editText.setSelection(editText.text.length)
        inputText.hint = resources.getString(R.string.user_name)
        inputText.addView(editText)

        val builder = AlertDialog.Builder(context, R.style.alertDialog)
        builder.setTitle(getString(string.update_name))
            .setView(inputText)
            .setMessage(getString(string.enter_a_new_name, textNameUser))
            .setIcon(R.mipmap.ic_launcher_round)
            .setCancelable(false)
            .setPositiveButton(getString(string.save)) { dialog, which -> viewModel
                .onUpdateNickNameMyFriend(userFriend!!.id, editText.text.toString())}
        builder.setNegativeButton(android.R.string.cancel) {dialog, which -> dialog.cancel() }
        builder.create().show()
    }

    companion object {
        private const val ARGUMENT_USER_ID = "ARGUMENT_USER_ID"
        private const val RESOURCE_VALUE = 0
        private const val MAX_LINE_TEXT = 1

        fun newInstance(id: String) = MessageOptionFragment().apply {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_USER_ID, id)
            arguments = bundle
        }
    }
}
