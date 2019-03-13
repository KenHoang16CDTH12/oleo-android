package com.framgia.oleo.screen.forgotpassword

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.databinding.FragmentForgotPasswordBinding
import com.framgia.oleo.utils.Index
import com.framgia.oleo.utils.extension.*
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.layout_verify_sms_code_dialog.*
import kotlinx.android.synthetic.main.layout_verify_sms_code_dialog.view.*
import java.util.concurrent.TimeUnit

class ForgotPasswordFragment : BaseFragment(), View.OnClickListener {
    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var alertDialog: AlertDialog
    private var token: ForceResendingToken? = null
    private var verifyId: String? = null
    private var listener: OnForgotPasswordListener? = null
    private var binding by autoCleared<FragmentForgotPasswordBinding>()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ForgotPasswordViewModel.create(this, viewModelFactory)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnForgotPasswordListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun setUpView() {
        setUpVerifyDialog()
        buttonSubmit.setOnClickListener(this)
        imageViewBack.setOnClickListener(this)
    }

    override fun bindView() {
        viewModel.getIsUserExistLiveData().observe(this, Observer {
            if (it) {
                alertDialog.show()
                sendSMSCode()
            } else {
                view?.showSnackBar(getString(R.string.phone_number_not_exist))
            }
            progressBarCheckUser.hide()
            buttonSubmit.show()
        })
        viewModel.isSignInSuccess.observe(this, Observer {
            if (it) {
                alertDialog.dismiss()
                listener!!.onVerifyCodeClick(editTextPhoneNumber.text.toString())
            } else {
                alertDialog.buttonVerifyCode.show()
                alertDialog.progressBarVerifyCode.hide()
                alertDialog.textLayoutSMSCode.error = getString(R.string.sms_code_not_correct)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageViewBack -> goBackFragment()
            R.id.buttonSubmit -> if (isCheckMultiClick()) checkExistUser()
            R.id.buttonVerifyCode -> if (isCheckMultiClick()) {
                if (validateSMSCode(
                        context!!,
                        alertDialog.editTextSMSCode.text.toString(),
                        alertDialog.textLayoutSMSCode
                    )
                ) {
                    if (verifyId.isNullOrEmpty()) {
                        alertDialog.textLayoutSMSCode.error = getString(R.string.sms_code_not_correct)
                        return
                    }
                    alertDialog.buttonVerifyCode.hide()
                    alertDialog.progressBarVerifyCode.show()
                    viewModel.signInWithPhoneAuthCredential(
                        PhoneAuthProvider.getCredential(verifyId!!, alertDialog.editTextSMSCode.text.toString())
                    )
                }
            }
            R.id.imageViewCloseDialog -> {
                alertDialog.editTextSMSCode.text!!.clear()
                alertDialog.textViewResendCode.isEnabled = true
                alertDialog.dismiss()
            }
            R.id.textViewResendCode -> {
                alertDialog.textViewResendCode.isEnabled = false
                alertDialog.textViewResendCode.postDelayed({
                    alertDialog.textViewResendCode.isEnabled = true
                }, RESEND_CODE_TIME)
                sendSMSCode()
            }
        }
    }

    private fun checkExistUser() {
        if (validInputPhoneNumber(context!!, editTextPhoneNumber.text.toString(), textLayoutPhoneNumber)) {
            progressBarCheckUser.show()
            buttonSubmit.hide()
            viewModel.checkExistUser(editTextPhoneNumber.text.toString())
        }
    }

    private fun sendSMSCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            editTextPhoneNumber.text.toString().replaceRange(
                Index.POSITION_ZERO,
                Index.POSITION_ONE,
                PHONE_NUMBER_CODE
            ),
            RETRIEVAL_SMS_TIME_OUT,
            TimeUnit.SECONDS,
            activity!!,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {}

                override fun onVerificationFailed(firebaseException: FirebaseException?) {
                    if (firebaseException is FirebaseTooManyRequestsException) {
                        view?.showSnackBar(getString(R.string.out_of_sms_request))
                    }
                }

                override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                    verifyId = verificationId
                }

                override fun onCodeSent(id: String, resendingToken: ForceResendingToken) {
                    super.onCodeSent(id, resendingToken)
                    verifyId = id
                    token = resendingToken
                }
            }, token
        )
    }

    private fun setUpVerifyDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.layout_verify_sms_code_dialog, null)
        alertDialog = AlertDialog.Builder(context).apply {
            setView(dialogView)
            dialogView.buttonVerifyCode.setOnClickListener(this@ForgotPasswordFragment)
            dialogView.imageViewCloseDialog.setOnClickListener(this@ForgotPasswordFragment)
            dialogView.textViewResendCode.setOnClickListener(this@ForgotPasswordFragment)
            dialogView.editTextSMSCode.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    dialogView.textLayoutSMSCode.error = ""
                }
            })
            setCancelable(false)
        }.create()
        alertDialog.setCanceledOnTouchOutside(false)
    }

    interface OnForgotPasswordListener {
        fun onVerifyCodeClick(phoneNumber: String)
    }

    companion object {
        private const val PHONE_NUMBER_CODE = "+84"
        private const val RETRIEVAL_SMS_TIME_OUT = 60L
        private const val RESEND_CODE_TIME = 10000L * 6 * 1
        fun newInstance() = ForgotPasswordFragment()
    }
}
