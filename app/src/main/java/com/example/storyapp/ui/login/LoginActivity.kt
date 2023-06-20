package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.remote.user.AuthResponse
import com.example.storyapp.data.remote.user.LoginModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.preference.UserPreference
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.signup.SignUpActivity
import com.example.storyapp.utils.isEmailValid
import com.example.storyapp.utils.isPasswordValid

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        supportActionBar?.hide()

        loginViewModel = ViewModelProvider(
            this
        )[LoginViewModel::class.java]

        setupAction()
        playAnimation()
        buttonEnable()
        emailHandler()
        passwordHandler()

        loginBinding.tvRegDirect.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }


        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        loginViewModel.login.observe(this) {
            login(it)
        }
        loginViewModel.error.observe(this) { isError ->
            if (isError) {
                errorMessage()
            }
        }
    }

    private fun playAnimation() {
        val login = ObjectAnimator.ofFloat(loginBinding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val title =
            ObjectAnimator.ofFloat(loginBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(loginBinding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(loginBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEtL = ObjectAnimator.ofFloat(loginBinding.emailEditTextLayout, View.ALPHA, 1f)
            .setDuration(500)
        val emailEt = ObjectAnimator.ofFloat(loginBinding.emailEditText, View.ALPHA, 1f)
            .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(loginBinding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEtL =
            ObjectAnimator.ofFloat(loginBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val passwordEt = ObjectAnimator.ofFloat(loginBinding.passwordEditText, View.ALPHA, 1f)
            .setDuration(500)
        val accountAvailability =
            ObjectAnimator.ofFloat(loginBinding.accountAvailability, View.ALPHA, 1f)
                .setDuration(500)
        val loginButton =
            ObjectAnimator.ofFloat(loginBinding.loginButton, View.ALPHA, 1f).setDuration(500)
        val regDirector =
            ObjectAnimator.ofFloat(loginBinding.registerDirector, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                login,
                title,
                message,
                email,
                emailEtL,
                emailEt,
                password,
                passwordEtL,
                passwordEt,
                accountAvailability,
                loginButton,
                regDirector
            )
            start()
        }
    }

    private fun buttonEnable() {
        val email = loginBinding.emailEditText.text.toString()
        val password = loginBinding.passwordEditText.text.toString()
        loginBinding.loginButton.isEnabled = isEmailValid(email) && isPasswordValid(password)
    }

    private fun setupAction() {
        loginBinding.loginButton.setOnClickListener {
            val email = loginBinding.emailEditText.text.toString()
            val password = loginBinding.passwordEditText.text.toString()
            loginViewModel.login(email, password)
                }
            }


    private fun emailHandler() {
        loginBinding.emailEditText.addTextChangedListener(object : TextWatcher {
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
        loginBinding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }

        })
    }

    private fun saveLoginState(authResponse: AuthResponse) {
        val userPref = UserPreference(this)
        val loginResult = authResponse.loginResult
        val loginModel = LoginModel(
            name = loginResult.name, userId = loginResult.userId, token = loginResult.token
        )
        userPref.saveUser(loginModel)
    }

    private fun login(authResponse: AuthResponse) {
        if (!authResponse.error) {
            saveLoginState(authResponse)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loginBinding.progressBar.visibility = View.VISIBLE
            loginBinding.progressBar.bringToFront()
        } else {
            loginBinding.progressBar.visibility = View.GONE
        }
    }

    private fun errorMessage() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.alert_login))
            setMessage(getString(R.string.login_invalid))
            setPositiveButton(getString(R.string.back)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}