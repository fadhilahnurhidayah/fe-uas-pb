package com.example.kantinsekre.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kantinsekre.MainActivity
import com.example.kantinsekre.databinding.ActivityLoginBinding
import com.example.kantinsekre.models.User
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.presentation.SharedViewModel
import com.example.kantinsekre.utils.TokenManager
import kotlinx.coroutines.launch

/**
 * Activity untuk menangani proses login user
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var tokenManager: TokenManager

    companion object {
        private const val ERROR_EMPTY_USERNAME = "Username tidak boleh kosong"
        private const val ERROR_EMPTY_PASSWORD = "Password tidak boleh kosong"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupTokenManager()
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
        val user = User(username, password)
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(context = this@LoginActivity)
                val response = apiService.login(user)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    handleSuccessfulLogin(response.body()?.data?.token)
                } else {
                    handleFailedLogin()
                }
            } catch (e: Exception) {
                handleLoginError(e)
            }
        }
    }

    private fun handleSuccessfulLogin(token: String?) {
        token?.let { tokenManager.saveToken(it) }
        navigateToMainActivity()
    }

    private fun handleFailedLogin() {
        Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
        binding.edtPassword.text?.clear()
    }

    private fun handleLoginError(error: Exception) {
        error.printStackTrace()
        Toast.makeText(this, "Terjadi kesalahan: ${error.message}", Toast.LENGTH_SHORT).show()
        binding.edtPassword.text?.clear()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}