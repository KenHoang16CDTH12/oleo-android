package com.framgia.oleo.screen.forgotpassword

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.R.string
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FragmentResetPasswordBinding
import com.framgia.oleo.utils.Constant.MIN_CHARACTER_INPUT_PASSWORD
import com.framgia.oleo.utils.extension.*
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.fragment_signup.textLayoutPassword
import kotlinx.android.synthetic.main.layout_reset_password_dialog.view.buttonSubmit

class ResetPasswordFragment : BaseFragment(), View.OnClickListener {

    private lateinit var viewModel: ResetPasswordViewModel
    private lateinit var alertDialog: AlertDialog
    private var phoneNumber: String? = null
    private var binding by autoCleared<FragmentResetPasswordBinding>()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ResetPasswordViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun setUpView() {
        phoneNumber = arguments!!.getString(EXTRA_PHONE_NUMBER)
        onCheckTextChanged(textLayoutPassword, textInputPassword)
        onCheckTextChanged(textLayoutConfirmPassword, textInputConfirmPassword)
        buttonResetPassword.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
    }

    override fun bindView() {
        viewModel.getResetPassowrdStateLiveData().observe(this, Observer {
            if (it) {
                showResetSuccessDialog()
            } else {
                view!!.showSnackBar(getString(string.reset_password_failed))
            }
        })
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imageViewBack -> goBackFragment()
            R.id.buttonResetPassword -> if (isCheckMultiClick()) {
                checkValidateForm()
            }
            R.id.buttonSubmit -> {
                alertDialog.dismiss()
                goBackFragment()
            }
        }
    }

    private fun onCheckTextChanged(textInputLayout: TextInputLayout, textInputEditText: TextInputEditText) {
        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = ""
                onSetEnableButtonReset(
                    textInputPassword.text.toString(),
                    textInputConfirmPassword.text.toString()
                )
            }
        })
    }

    private fun onSetEnableButtonReset(
        textPassword: String,
        textConfirmPassword: String
    ) {
        buttonResetPassword.isEnabled =
            textPassword.length >= MIN_CHARACTER_INPUT_PASSWORD && textConfirmPassword.length >= MIN_CHARACTER_INPUT_PASSWORD
    }

    private fun checkValidateForm() {
        if (validInputPassword(
                context!!, textInputPassword.text.toString(), textLayoutPassword
            ) && validInputConfirmPassword(
                context!!,
                textInputPassword.text.toString(),
                textInputConfirmPassword.text.toString(),
                textLayoutConfirmPassword
            )
        ) {
            viewModel.resetPassword(phoneNumber!!, textInputPassword.text.toString())
        }
    }

    private fun showResetSuccessDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_reset_password_dialog, null)
        alertDialog = AlertDialog.Builder(context).apply {
            setView(dialogView)
            dialogView.buttonSubmit.setOnClickListener(this@ResetPasswordFragment)
            setCancelable(false)
        }.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    companion object {
        private const val EXTRA_PHONE_NUMBER = "EXTRA_PHONE_NUMBER"

        fun newInstance(phoneNumber: String) = ResetPasswordFragment().apply {
            val bundle = Bundle()
            bundle.putString(EXTRA_PHONE_NUMBER, phoneNumber)
            arguments = bundle
        }
    }
}
