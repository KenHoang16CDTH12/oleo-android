package com.framgia.oleo.screen.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_option_message_header.*
import kotlinx.android.synthetic.main.toolbar.*

class MessageOptionFragment : BaseFragment() {
    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_option_message, container, false)
    }

    override fun setUpView() {
        imageToolbar.setOnClickListener { fragmentManager?.popBackStack() }
    }

    override fun bindView() {
        textViewNameUser.text = arguments?.getString(ARGUMENT_USER_NAME)
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
