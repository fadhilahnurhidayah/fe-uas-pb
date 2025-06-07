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
import com.example.kantinsekre.models.AuthResponse
import com.example.kantinsekre.presentation.SharedViewModel
import com.example.kantinsekre.utils.TokenManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupListeners()
        tokenManager = TokenManager(this)
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val nama = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (nama.isEmpty()) {
                binding.edtUsername.error = "Username cannot be empty"
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Password cannot be empty"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            val user = User(nama, password)
            lifecycleScope.launch {
                try {
                    val apiService = ApiClient.create(context = this@LoginActivity)
                    val response = apiService.login(user)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val token = response.body()?.data?.token
                        if (token != null) {
                            tokenManager.saveToken(token)
                        }

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.edtPassword.text?.clear()
                }
            }
        }
    }
}