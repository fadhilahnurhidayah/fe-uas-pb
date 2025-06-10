package com.example.kantinsekre.presentation.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.kantinsekre.MainActivity
import com.example.kantinsekre.databinding.ActivityLoginBinding
import com.example.kantinsekre.presentation.viewmodel.SharedViewModel
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.AuthViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory
import com.example.kantinsekre.util.TokenManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var tokenManager: TokenManager

    companion object {
        private const val ERROR_EMPTY_USERNAME = "Username tidak boleh kosong"
        private const val ERROR_EMPTY_PASSWORD = "Password tidak boleh kosong"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupTokenManager()
        initializeSharedViewModel()
        observeViewModel()
    }

    private fun setupView() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupLoginButton()
    }

    private fun setupTokenManager() {
        tokenManager = TokenManager(this)
    }

    private fun initializeSharedViewModel() {
        sharedViewModel.initialize(authViewModel)
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            !validateUsername(username) -> return
            !validatePassword(password) -> return
            else -> performLogin(username, password)
        }
    }

    private fun validateUsername(username: String): Boolean {
        if (username.isEmpty()) {
            binding.edtUsername.error = ERROR_EMPTY_USERNAME
            binding.edtUsername.requestFocus()
            return false
        }
        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            binding.edtPassword.error = ERROR_EMPTY_PASSWORD
            binding.edtPassword.requestFocus()
            return false
        }
        return true
    }

    private fun performLogin(username: String, password: String) {
        authViewModel.login(username, password)
    }

    private fun observeViewModel() {
        authViewModel.loginResult.observe(this) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    showLoading(true)
                }
                is UiState.Success -> {
                    showLoading(false)
                    handleSuccessfulLogin(uiState.data.data?.token)
                }
                is UiState.Error -> {
                    showLoading(false)
                    handleLoginError(uiState.message)
                }
                is UiState.Idle -> {
                    showLoading(false)
                }

                else -> {}
            }
        }

        // SharedViewModel automatically observes AuthViewModel.currentUser
        // No need to manually set current user here
    }

    @SuppressLint("SetTextI18n")
    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                btnLogin.isEnabled = false
                btnLogin.text = "Loading..."
                progressBar.visibility = View.VISIBLE
            } else {
                btnLogin.isEnabled = true
                btnLogin.text = "MASUK"
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleSuccessfulLogin(token: String?) {
        token?.let { tokenManager.saveToken(it) }

        // Gunakan fetchCurrentUser untuk mendapatkan data user yang sedang login
        authViewModel.fetchCurrentUser()

        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }

    private fun handleLoginError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.edtPassword.text?.clear()
        binding.edtPassword.requestFocus()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}