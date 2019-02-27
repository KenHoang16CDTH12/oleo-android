package com.framgia.oleo.screen.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.framgia.oleo.R
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.screen.main.MainActivity
import com.framgia.oleo.utils.extension.goBackFragment
import kotlinx.android.synthetic.main.fragment_option_message.toolbarOption
import kotlinx.android.synthetic.main.fragment_option_message_header.textViewNameUser
import kotlinx.android.synthetic.main.toolbar.view.textTitleToolbar
import kotlinx.android.synthetic.main.toolbar.view.toolbarCustom

class MessageOptionFragment : BaseFragment() {
    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_option_message, container, false)
    }

    override fun setUpView() {
        setupActionBar()
        setHasOptionsMenu(true)
    }

    override fun bindView() {
        textViewNameUser.text = arguments?.getString(ARGUMENT_USER_NAME)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> (activity!! as MainActivity).goBackFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        var toolbar = toolbarOption.toolbarCustom
        toolbar.textTitleToolbar.text= activity!!.getString(string.option)
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)

    }

    companion object {

        private const val ARGUMENT_USER_NAME = "ARGUMENT_USER_NAME"

        fun newInstance(name: String) = MessageOptionFragment().apply {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_USER_NAME, name)
            arguments = bundle
        }
    }
}
