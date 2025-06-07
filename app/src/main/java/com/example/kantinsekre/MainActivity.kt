package com.example.kantinsekre

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import com.example.kantinsekre.databinding.ActivityMainBinding
import com.example.kantinsekre.presentation.SharedViewModel
import com.example.kantinsekre.presentation.product.ProductFragment
import com.example.kantinsekre.presentation.report.ReportFragment
import com.example.kantinsekre.presentation.transaction.TransactionFragment
//import com.example.kantinsekre.presentation.report.ReportFragment
//import com.example.kantinsekre.presentation.settings.SettingsFragment

/**
 * MainActivity adalah aktivitas utama aplikasi yang menangani proses login
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    companion object {
        private const val ERROR_EMPTY_FIELDS = "Username dan password harus diisi"
        private const val SUCCESS_LOGIN = "Login berhasil"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setupView()
        setupLoginButton()

        setupBottomNavigation()
        supportActionBar?.hide()

        if (savedInstanceState == null) {
            loadFragment(ProductFragment())
        }
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        when {
            isInputEmpty(username, password) -> showErrorMessage(ERROR_EMPTY_FIELDS)
            else -> processLogin(username, password)
        }
    }

    private fun isInputEmpty(username: String, password: String): Boolean {
        return username.isEmpty() || password.isEmpty()
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun processLogin(username: String, password: String) {
        // TODO: Implementasi logika login yang sebenarnya
        showErrorMessage(SUCCESS_LOGIN)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_product -> {
                    loadFragment(ProductFragment())
                    true
                }
                R.id.navigation_transaction -> {
                    loadFragment(TransactionFragment())
                    true
                }
                R.id.navigation_report -> {
                    loadFragment(ReportFragment())
                    true
                }
//                R.id.navigation_settings -> {
//                    loadFragment(SettingsFragment())
//                    true
//                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}