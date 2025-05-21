package com.example.kantinsekre.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kantinsekre.R
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
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()

            if (username.isEmpty()) {
                binding.edtUsername.error = "Username cannot be empty"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }

            // Perform login using DummyDataProvider
            val user = DummyDataProvider.login(username, password)
            if (user != null) {
                // Save user in shared ViewModel
                sharedViewModel.setCurrentUser(user)

                // Navigate to dashboard screen after successful login
                Toast.makeText(requireContext(), "Login successful as ${user.role}", Toast.LENGTH_SHORT).show()

                // Navigate to the dashboard fragment
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
            } else {
                Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        // Menu button click listener
        binding.btnMenu.setOnClickListener {
            // Handle menu button click
            Toast.makeText(requireContext(), "Menu clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}