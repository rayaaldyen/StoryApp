package com.example.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.utils.isEmailValid
import com.example.storyapp.utils.isPasswordValid


class SignUpActivity : AppCompatActivity() {
    private lateinit var signBinding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signBinding.root)

        supportActionBar?.hide()

        signUpViewModel = ViewModelProvider(
            this
        )[SignUpViewModel::class.java]

        signUpViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        signUpViewModel.error.observe(this) { isError ->
            if (isError) {
                errorMessage()
            }
        }
        signUpViewModel.loggedIn.observe(this) {
            loggedIn(it)
        }
        buttonEnable()
        nameHandler()
        emailHandler()
        passwordHandler()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        val register =
            ObjectAnimator.ofFloat(signBinding.tvRegister, View.ALPHA, 1f).setDuration(500)
        val title =
            ObjectAnimator.ofFloat(signBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(signBinding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEtL =
            ObjectAnimator.ofFloat(signBinding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val nameEt =
            ObjectAnimator.ofFloat(signBinding.nameEditText, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(signBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEtL =
            ObjectAnimator.ofFloat(signBinding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailEt =
            ObjectAnimator.ofFloat(signBinding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(signBinding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEtL = ObjectAnimator.ofFloat(signBinding.passwordEditTextLayout, View.ALPHA, 1f)
            .setDuration(500)
        val passwordEt = ObjectAnimator.ofFloat(signBinding.passwordEditText, View.ALPHA, 1f)
            .setDuration(500)
        val signUpButton =
            ObjectAnimator.ofFloat(signBinding.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                register,
                title,
                name,
                nameEtL,
                nameEt,
                email,
                emailEtL,
                emailEt,
                password,
                passwordEtL,
                passwordEt,
                signUpButton
            )
            startDelay = 500
            start()
        }
    }

    private fun buttonEnable() {
        val name = signBinding.nameEditText.text.toString()
        val email = signBinding.emailEditText.text.toString()
        val password = signBinding.passwordEditText.text.toString()
        signBinding.signupButton.isEnabled =
            !TextUtils.isEmpty(name) && isEmailValid(email) && isPasswordValid(password)
    }

    private fun setupAction() {
        signBinding.signupButton.setOnClickListener {
            val name = signBinding.nameEditText.text.toString()
            val email = signBinding.emailEditText.text.toString()
            val password = signBinding.passwordEditText.text.toString()
            signUpViewModel.userSignUp(name, email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            signBinding.progressBar.visibility = View.VISIBLE
            signBinding.progressBar.bringToFront()
        } else {
            signBinding.progressBar.visibility = View.GONE
        }
    }

    private fun nameHandler() {
        signBinding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }

        })
    }

    private fun emailHandler() {
        signBinding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }

        })
    }

    private fun passwordHandler() {
        signBinding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }

        })
    }

    private fun loggedIn(loggedIn: Boolean) {
        if (loggedIn) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.signup_pass_tittle))
                setMessage(getString(R.string.signup_successfully))
                setPositiveButton("Login") { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun errorMessage() {
        Toast.makeText(
            this@SignUpActivity,
            getString(R.string.signup_failed),
            Toast.LENGTH_SHORT
        )
            .show()
    }
}