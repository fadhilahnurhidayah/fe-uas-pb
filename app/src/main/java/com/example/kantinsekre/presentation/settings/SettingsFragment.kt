package com.example.kantinsekre.presentation.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kantinsekre.R
import com.example.kantinsekre.models.DisplayUser
import com.example.kantinsekre.presentation.viewmodel.SharedViewModel
import com.example.kantinsekre.presentation.auth.LoginActivity
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.AuthViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText


class SettingsFragment : Fragment() {

    private var currentAccountName: TextView? = null
    private var currentAccountRole: TextView? = null
    private val accountsList = mutableListOf<DisplayUser>()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    // ViewModel dengan ViewModelFactory
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    private var darkModeSwitch: SwitchMaterial? = null
    private var saveSettingsButton: MaterialButton? = null
    private var logoutButton: MaterialButton? = null
    private var addAccountButton: MaterialButton? = null
    private var manageAccountsButton: MaterialButton? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        observeViewModel()
        setupListeners()
        updateCurrentAccountDisplay()

        // Don't fetch users automatically - only when manage accounts is clicked
    }

    /**
     * Observe ViewModel untuk perubahan data
     */
    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        // Observe users list
        authViewModel.users.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Show loading indicator if needed
                    Toast.makeText(requireContext(), "Memuat daftar akun...", Toast.LENGTH_SHORT).show()
                }
                is UiState.Success -> {
                    accountsList.clear()
                    accountsList.addAll(uiState.data)
                    // Show manage accounts dialog after successfully fetching users
                    showManageAccountsDialog()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), "Gagal memuat daftar akun: ${uiState.message}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }

        // Observe current user by ID - simplified
        authViewModel.currentUser.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    // Update current account display dengan data dari API
                    val user = uiState.data
                    currentAccountName?.text = user.nama
                    currentAccountRole?.text = "Role: ${user.role}"
                }
                is UiState.Error -> {
                    // Fallback ke data dari SharedViewModel jika API gagal
                    updateCurrentAccountDisplay()
                }
                else -> {
                    // Loading, Idle - do nothing to avoid issues
                }
            }
        }

        // Observe add user result - simplified
        authViewModel.addUserResult.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "Akun berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Loading, Idle - do nothing
                }
            }
        }

        // Observe update user result - simplified
        authViewModel.updateUserResult.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "User berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    updateCurrentAccountDisplay()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Loading, Idle - do nothing
                }
            }
        }

        // Observe delete user result - simplified
        authViewModel.deleteUserResult.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    Toast.makeText(requireContext(), "User berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    updateCurrentAccountDisplay()
                }
                is UiState.Error -> {
                    Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Loading, Idle - do nothing
                }
            }
        }
    }
    private fun initializeViews(view: View) {
        currentAccountName = view.findViewById(R.id.current_account_name)
        currentAccountRole = view.findViewById(R.id.current_account_role)
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch)
        saveSettingsButton = view.findViewById(R.id.save_settings_button)
        logoutButton = view.findViewById(R.id.logout_button)
        addAccountButton = view.findViewById(R.id.add_account_button)
        manageAccountsButton = view.findViewById(R.id.manage_accounts_button)
    }


    @SuppressLint("SetTextI18n")
    private fun updateCurrentAccountDisplay() {
        val currentUser = sharedViewModel.getCurrentUser()
        if (currentUser != null && currentUser.nama != null) {
            currentAccountName?.text = currentUser.nama
            currentAccountRole?.text = "ID: ${currentUser.nama} • ${currentUser.role ?: "Unknown"}"
        } else {
            currentAccountName?.text = "Tidak ada akun aktif"
            currentAccountRole?.text = "Silakan login untuk melanjutkan"
        }
    }
    private fun setupListeners() {
        addAccountButton?.setOnClickListener {
            showAddAccountDialog()
        }

        manageAccountsButton?.setOnClickListener {
            // Fetch users only when manage accounts button is clicked
            authViewModel.fetchAllUsers()
        }

        saveSettingsButton?.setOnClickListener {
            saveSettings()
        }

        logoutButton?.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }
    private fun saveSettings() {
        val darkModeEnabled = darkModeSwitch?.isChecked == true

        Toast.makeText(context, "Pengaturan berhasil disimpan!", Toast.LENGTH_SHORT).show()
        // Di sini Anda bisa menyimpan `darkModeEnabled` ke SharedPreferences atau ViewModel
        // sharedViewModel.setDarkMode(darkModeEnabled)

        // Log untuk debugging
        android.util.Log.d("SettingsFragment", "Dark mode enabled: $darkModeEnabled")
    }
    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout dari aplikasi?")
            .setPositiveButton("Logout") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        Toast.makeText(requireContext(), "Anda telah logout.", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    private fun showAddAccountDialog() {
        try {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
            val emailInput = dialogView.findViewById<TextInputEditText>(R.id.account_email_input)
            val passwordInput = dialogView.findViewById<TextInputEditText>(R.id.account_password_input)
            val roleSpinner = dialogView.findViewById<Spinner>(R.id.account_role_spinner)

        val roles = arrayOf("kasir", "owner")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Akun Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val emailOrUsername = emailInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                val role = roleSpinner.selectedItem.toString()


                if (validateNewAccount(emailOrUsername, password)) {
                    addNewAccount(emailOrUsername, password, role)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
        } catch (_: Exception) {
            Toast.makeText(context, "Error: Dialog tidak dapat dibuka", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validateNewAccount(emailOrUsername: String, password: String): Boolean {
        when {
            emailOrUsername.isEmpty() || password.isEmpty() -> {
                Toast.makeText(context, "Email/Username dan password harus diisi", Toast.LENGTH_SHORT).show()
                return false
            }
            !isValidEmailOrUsername(emailOrUsername) -> {
                Toast.makeText(context, "Format email atau username tidak valid", Toast.LENGTH_SHORT).show()
                return false
            }
            password.length < 6 -> {
                Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return false
            }
            accountsList.any { it.nama == emailOrUsername } -> {
                Toast.makeText(context, "Email/Username sudah terdaftar", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun isValidEmailOrUsername(input: String): Boolean {
        return when {
            android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> true
            isValidUsername(input) -> true
            else -> false
        }
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length >= 3 &&
               username.matches(Regex("^[a-zA-Z0-9_]+$")) &&
               !username.startsWith("_") &&
               !username.endsWith("_")
    }

    private fun addNewAccount(emailOrUsername: String, password: String, role: String) {

        // Gunakan ViewModel untuk add user dengan parameter yang benar
        authViewModel.addUser(emailOrUsername, password, role)
    }
    private fun showManageAccountsDialog() {
        if (accountsList.isEmpty()) {
            Toast.makeText(context, "Belum ada akun yang terdaftar", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = sharedViewModel.getCurrentUser()
        val accountsDisplay = accountsList.map { user ->
            val status = if (user.idUser == currentUser?.idUser) "🟢 AKTIF" else "⚪"
            "$status ${user.nama}\n🆔 ID: ${user.idUser} • 👤 ${user.role}"
        }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("👥 Kelola Akun (${accountsList.size} akun)")
            .setItems(accountsDisplay) { _, position ->
                val selectedUser = accountsList[position]
                showAccountOptionsDialog(selectedUser)
            }
            .setNeutralButton("Tutup", null)
            .show()
    }
    private fun showAccountOptionsDialog(user: DisplayUser) {
        val currentUser = sharedViewModel.getCurrentUser()
        val options = mutableListOf<String>()

        // Show activate option only if user is not currently active
        if (user.idUser != currentUser?.idUser) {
            options.add("🔄 Aktifkan Akun")
        }

        // Always show these options
        options.addAll(listOf(
            "✏️ Ubah Password",
            "👤 Ubah Role",
            "🗑️ Hapus Akun"
        ))

        val statusText = if (user.idUser == currentUser?.idUser) "🟢 AKTIF" else "⚪ TIDAK AKTIF"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Kelola Akun\n📧 ${user.nama}\n🆔 ID: ${user.idUser}\n👤 ${user.role}\n$statusText")
            .setItems(options.toTypedArray()) { dialog, position ->
                when (options[position]) {
                    "🔄 Aktifkan Akun" -> {
                        try {
                            setActiveAccount(user)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Error: Gagal mengaktifkan akun", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                    "✏️ Ubah Password" -> {
                        showChangePasswordDialog(user)
                        dialog.dismiss()
                    }
                    "👤 Ubah Role" -> {
                        showChangeRoleDialog(user)
                        dialog.dismiss()
                    }
                    "🗑️ Hapus Akun" -> {
                        confirmDeleteAccount(user)
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun setActiveAccount(user: DisplayUser) {
        try {
            // Gunakan fetchCurrentUser untuk mendapatkan data user terbaru
            authViewModel.fetchCurrentUser()

            // Update display
            updateCurrentAccountDisplay()

            Toast.makeText(context, "${user.nama} sekarang aktif", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(context, "Error: Gagal mengaktifkan akun", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showChangePasswordDialog(user: DisplayUser) {
        try {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
            val newPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.new_password_input)
            val confirmPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.confirm_password_input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ubah Password\n📧 ${user.nama}")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val newPassword = newPasswordInput.text.toString().trim()
                val confirmPassword = confirmPasswordInput.text.toString().trim()

                when {
                    newPassword.isEmpty() -> {
                        Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.length < 6 -> {
                        Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmPassword -> {
                        Toast.makeText(context, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // Gunakan ViewModel untuk update password
                        authViewModel.updateUserPassword(user.idUser.toString(), newPassword, user)

                        // Update current user jika yang diubah adalah user aktif
                        val currentUser = sharedViewModel.getCurrentUser()
                        if (user.idUser == currentUser?.idUser) {
                            // Update display safely
                            updateCurrentAccountDisplay()
                        }

                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
            .show()
        } catch (_: Exception) {
            Toast.makeText(context, "Error: Dialog tidak dapat dibuka", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showChangeRoleDialog(user: DisplayUser) {
        val roles = arrayOf("kasir", "owner")
        val currentIndex = roles.indexOf(user.role)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("👤 Ubah Role\n📧 ${user.nama}")
            .setSingleChoiceItems(roles, currentIndex) { dialog, which ->
                val newRole = roles[which]
                if (newRole != user.role) {
                    // Gunakan ViewModel untuk update role
                    authViewModel.updateUserRole(user.idUser.toString(), newRole, user)

                    // Update current user jika yang diubah adalah user aktif
                    val currentUser = sharedViewModel.getCurrentUser()
                    if (user.idUser == currentUser?.idUser) {
                        // Update display safely
                        updateCurrentAccountDisplay()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun confirmDeleteAccount(user: DisplayUser) {
        val currentUser = sharedViewModel.getCurrentUser()
        val message = if (user.idUser == currentUser?.idUser) {
            "⚠️ Akun ini sedang aktif!\n\nApakah Anda yakin ingin menghapus akun:\n📧 ${user.nama}?"
        } else {
            "Apakah Anda yakin ingin menghapus akun:\n📧 ${user.nama}?"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🗑️ Hapus Akun")
            .setMessage(message)
            .setPositiveButton("Ya, Hapus") { _, _ ->
                val wasActive = user.idUser == currentUser?.idUser

                // Gunakan ViewModel untuk delete user
                authViewModel.deleteUser(user.idUser.toString())

                if (wasActive) {
                    // Jika akun yang dihapus adalah akun aktif, navigasi ke login
                    updateCurrentAccountDisplay()
                    Toast.makeText(context, "⚠️ Akun aktif dihapus. Silakan login kembali.", Toast.LENGTH_LONG).show()

                    // Navigate to login
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}