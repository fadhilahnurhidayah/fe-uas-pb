package com.example.kantinsekre.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kantinsekre.R
import com.example.kantinsekre.databinding.FragmentDashboardBinding
import com.example.kantinsekre.presentation.SharedViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel to access login state
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set welcome message with user info
        setupWelcomeMessage()

        // Setup dashboard menu items
        setupMenuItems()
    }

    private fun setupWelcomeMessage() {
        sharedViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val welcomeText = "Welcome, ${user.username} (${user.role})"
                binding.tvWelcomeMessage.text = welcomeText

                // Show/hide admin-only features
                val isAdmin = user.role == "admin"
                binding.cardSettings.visibility = if (isAdmin) View.VISIBLE else View.GONE
            } else {
                // If no  is logged in, navigate back to login
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun setupMenuItems() {
        binding.cardProducts.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_productFragment)
        }

        binding.cardTransactions.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_transactionFragment)
        }

        binding.cardReports.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_reportFragment)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
        }

        binding.btnLogout.setOnClickListener {
            sharedViewModel.clearCurrentUser()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}