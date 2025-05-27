package com.example.kantinsekre.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kantinsekre.MainActivity
import com.example.kantinsekre.databinding.FragmentLoginBinding
import com.example.kantinsekre.presentation.SharedViewModel
import com.example.kantinsekre.util.DummyDataProvider

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.edtUsername.error = "Username cannot be empty"
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Password cannot be empty"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            performLogin(username, password)
        }

        // Menu button click listener
        binding.btnMenu.setOnClickListener {
            showLoginHints()
        }
    }

    private fun performLogin(username: String, password: String) {
        // Show loading state
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."

        // Perform login using DummyDataProvider
        val user = DummyDataProvider.login(username, password)

        if (user != null) {
            // Save user in shared ViewModel
            sharedViewModel.setCurrentUser(user)

            Toast.makeText(requireContext(), "Login successful as ${user.role}", Toast.LENGTH_SHORT).show()

            // Navigate to MainActivity using Intent
            navigateToMainActivity()
        } else {
            // Reset button state
            binding.btnLogin.isEnabled = true
            binding.btnLogin.text = "Login"

            Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()

            // Clear password field for security
            binding.edtPassword.text?.clear()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoginHints() {
        val message = """
            Demo Login Credentials:
            
            Admin: admin / admin123
            Cashier: kasir / kasir123
        """.trimIndent()

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}