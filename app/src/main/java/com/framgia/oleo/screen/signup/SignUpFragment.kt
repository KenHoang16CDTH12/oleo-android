package com.framgia.oleo.screen.signup

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.framgia.oleo.R
import com.framgia.oleo.base.BaseFragment
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.FragmentSignupBinding
import com.framgia.oleo.utils.Constant.MIN_CHARACTER_INPUT_PASSWORD
import com.framgia.oleo.utils.extension.*
import com.framgia.oleo.utils.liveData.autoCleared
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpFragment : BaseFragment(), View.OnClickListener {

    private lateinit var viewModel: SignUpViewModel
    private var isSignUp = false
    private var binding by autoCleared<FragmentSignupBinding>()

    var onResultWhenLoginSuccess: ((phoneNumber: String, password: String) -> Unit)? = null

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = SignUpViewModel.create(this, viewModelFactory)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, null, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun bindView() {
        onCheckTextChanged()
        buttonClose.setOnClickListener(this)
        buttonSignUp.setOnClickListener(this)
    }

    override fun setUpView() {
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.buttonClose -> if (isCheckMultiClick()) goBackFragment()
            R.id.buttonSignUp -> if (isCheckMultiClick()) {
                textMessageSignUp.text = ""
                onCheckValidateFormAndSignUp()
            }
        }
    }

    private fun onCheckValidateFormAndSignUp() {
        if (validInputUserName(context!!, textInputUserName.text.toString(), textLayoutUserName) && validInputEmail(
                context!!, textInputEmail.text.toString(), textLayoutEmail
            ) && validInputPhoneNumber(
                context!!, textInputPhoneNumber.text.toString(), textLayoutPhoneNumber
            ) && validInputPassword(
                context!!, textInputPassword.text.toString(), textLayoutPassword
            ) && validInputConfirmPassword(
                context!!,
                textInputPassword.text.toString(),
                textInputConfirmPassword.text.toString(),
                textLayoutConfirmPassword
            )
        ) {
            onSignUp()
        }
        isCheckClickableImageButtonClick(buttonSignUp)
    }

    private fun onSignUp() {
        val progressDialog = AlertDialog.Builder(context).setView(R.layout.layout_progress_dialog).create()
        progressDialog.show()
        viewModel.getUserByPhoneNumber(textInputPhoneNumber.text.toString(), object : ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (isSignUp) return
                if (dataSnapShot.exists()) {
                    progressDialog.dismiss()
                    activity?.showToast(getString(R.string.error_phone_number_exists))
                    return
                }
                isSignUp = true
                viewModel.registerUser(getUser(), OnCompleteListener<Void> {
                    progressDialog.dismiss()
                    if (!it.isSuccessful) {
                        activity?.showToast(getString(R.string.sign_up_fail))
                        return@OnCompleteListener
                    }
                    onResultWhenLoginSuccess!!.invoke(
                        textInputPhoneNumber.text.toString(),
                        textInputPassword.text.toString()
                    )
                    goBackFragment()
                }, OnFailureListener {
                    activity?.showToast(getString(R.string.sign_up_fail))
                })
            }

            override fun onCancelled(error: DatabaseError) {
                activity?.showToast(getString(R.string.sign_up_fail))
            }
        })
    }

    private fun getUser(): User = User(
        textInputPhoneNumber.text.toString(),
        textInputUserName.text.toString(),
        textInputEmail.text.toString(),
        textInputPhoneNumber.text.toString(),
        "",
        textInputPassword.text.toString()
    )

    private fun onCheckTextChanged() {
        onCheckTextChangedSignUp(textLayoutUserName, textInputUserName)
        onCheckTextChangedSignUp(textLayoutEmail, textInputEmail)
        onCheckTextChangedSignUp(textLayoutPhoneNumber, textInputPhoneNumber)
        onCheckTextChangedSignUp(textLayoutPassword, textInputPassword)
        onCheckTextChangedSignUp(textLayoutConfirmPassword, textInputConfirmPassword)
    }

    private fun onCheckTextChangedSignUp(textInputLayout: TextInputLayout, textInputEditText: TextInputEditText) {

        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = ""
                onSetEnableButtonLogin(
                    textInputUserName.text.toString(),
                    textInputEmail.text.toString(),
                    textInputPhoneNumber.text.toString(),
                    textInputPassword.text.toString(),
                    textInputConfirmPassword.text.toString()
                )
            }
        })
    }

    private fun onSetEnableButtonLogin(
        textUserName: String,
        textEmail: String,
        textPhoneNumber: String,
        textPassword: String,
        textConfirmPassword: String
    ) {
        if (textUserName.isNotBlank() && textEmail.isNotBlank() && textPhoneNumber.isNotBlank() && textPassword.length >= MIN_CHARACTER_INPUT_PASSWORD && textConfirmPassword.length >= MIN_CHARACTER_INPUT_PASSWORD) {
            buttonSignUp.isEnabled = true
            buttonSignUp.setBackgroundResource(R.drawable.button_sign_up_bg)
        } else {
            buttonSignUp.isEnabled = false
            buttonSignUp.setBackgroundResource(R.drawable.button_sign_up_disable_bg)
        }
    }

    companion object {

        const val USER_TABLE_FIRE_BASE = "User"

        fun newInstance() = SignUpFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }
}
