package com.example.kantinsekre.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.kantinsekre.R
import com.example.kantinsekre.presentation.SharedViewModel
import com.example.kantinsekre.presentation.auth.LoginActivity
import com.example.kantinsekre.models.User
import com.example.kantinsekre.models.UserItem
import com.example.kantinsekre.models.UserResponse
import com.example.kantinsekre.network.ApiClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class UserAccountInfo(
    val user: User,
    var isActive: Boolean = false,
    val createdDate: String = ""
)

class SettingsFragment : Fragment() {

    private var currentAccountName: TextView? = null
    private var currentAccountRole: TextView? = null
    private val accountsList = mutableListOf<UserItem>()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var storeNameEdit: TextInputEditText? = null
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
       fetchAllUsers()
        setupListeners()
        updateCurrentAccountDisplay()
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

    private fun fetchAllUsers() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val response = apiService.getAllUsers()
                accountsList.clear()
                accountsList.addAll(response.data)
                updateCurrentAccountDisplay()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Gagal memuat user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateCurrentAccountDisplay() {
        val activeAccountInfo = accountsList.find { it.isActive }
        currentAccountName?.text = activeAccountInfo?.user?.username ?: "Tidak ada akun aktif"
        currentAccountRole?.text = activeAccountInfo?.user?.role ?: ""
    }

    private fun setupListeners() {
        addAccountButton?.setOnClickListener {
            showAddAccountDialog()
        }

        manageAccountsButton?.setOnClickListener {
            showManageAccountsDialog()
        }

        saveSettingsButton?.setOnClickListener {
            saveSettings()
        }

        logoutButton?.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun saveSettings() {
        val name = storeNameEdit?.text.toString().trim()
        val dark = darkModeSwitch?.isChecked ?: false

        if (name.isNotEmpty()) {
            Toast.makeText(context, "Pengaturan berhasil disimpan!", Toast.LENGTH_SHORT).show()
            // Di sini Anda bisa menyimpan `name` dan `dark` ke SharedPreferences atau ViewModel
            // sharedViewModel.setStoreName(name)
            // sharedViewModel.setDarkMode(dark)
        } else {
            Toast.makeText(context, "Nama toko tidak boleh kosong.", Toast.LENGTH_SHORT).show()
        }
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
        sharedViewModel.setCurrentUser(null)

        Toast.makeText(requireContext(), "Anda telah logout.", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showAddAccountDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val emailInput = dialogView.findViewById<TextInputEditText>(R.id.account_email_input)
        val passwordInput = dialogView.findViewById<TextInputEditText>(R.id.account_password_input)
        val roleSpinner = dialogView.findViewById<Spinner>(R.id.account_role_spinner)

        val roles = arrayOf("Kasir", "Owner")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Akun Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                val role = roleSpinner.selectedItem.toString()

                if (validateNewAccount(email, password)) {
                    addNewAccount(email, password, role)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun validateNewAccount(email: String, password: String): Boolean {
        when {
            email.isEmpty() || password.isEmpty() -> {
                Toast.makeText(context, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return false
            }
            password.length < 6 -> {
                Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return false
            }
            accountsList.any { it.user.username == email } -> {
                Toast.makeText(context, "Email sudah terdaftar", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun addNewAccount(email: String, password: String, role: String) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val newId = (accountsList.maxOfOrNull { it.user.id } ?: 0) + 1
        val newUser = User(newId, email, password, role)
        val newAccountInfo = UserAccountInfo(newUser, false, currentDate)

        accountsList.add(newAccountInfo)
        Toast.makeText(
            context,
            "Akun $role berhasil ditambahkan!\n📧 $email",
            Toast.LENGTH_LONG
        ).show()
        // di sini memanggil API untuk menambahkan user
        // dan perbarui accountsList dari respons API.
    }

    private fun showManageAccountsDialog() {
        if (accountsList.isEmpty()) {
            Toast.makeText(context, "Belum ada akun yang terdaftar", Toast.LENGTH_SHORT).show()
            return
        }

        val accountsDisplay = accountsList.map { accountInfo ->
            val status = if (accountInfo.isActive) "AKTIF" else "⚪"
            "$status ${accountInfo.user.username}\n👤 ${accountInfo.user.role} • 📅 ${accountInfo.createdDate}"
        }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("👥 Kelola Akun (${accountsList.size} akun)")
            .setItems(accountsDisplay) { _, position ->
                showAccountOptionsDialog(accountsList[position])
            }
            .setNeutralButton("Tutup", null)
            .show()
    }

    private fun showAccountOptionsDialog(accountInfo: UserAccountInfo) {
        val options = mutableListOf<String>()

        if (!accountInfo.isActive) {
            options.add("🔄 Aktifkan Akun")
        }
        options.addAll(listOf(
            "Ubah Password",
            "Ubah Role",
            "Hapus Akun"
        ))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("📧 ${accountInfo.user.username}\n👤 ${accountInfo.user.role}")
            .setItems(options.toTypedArray()) { _, position ->
                when (options[position]) {
                    "Aktifkan Akun" -> setActiveAccount(accountInfo)
                    "Ubah Password" -> showChangePasswordDialog(accountInfo)
                    "Ubah Role" -> showChangeRoleDialog(accountInfo)
                    "Hapus Akun" -> confirmDeleteAccount(accountInfo)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun setActiveAccount(accountInfo: UserAccountInfo) {
        accountsList.forEach { it.isActive = false }

        accountInfo.isActive = true

        sharedViewModel.setCurrentUser(accountInfo.user)

        updateCurrentAccountDisplay()
        Toast.makeText(context, "${accountInfo.user.username} sekarang aktif", Toast.LENGTH_SHORT).show()
        // PENTING: Jika Anda menggunakan API, di sini Anda harus memanggil API untuk mengaktifkan user
    }

    private fun showChangePasswordDialog(accountInfo: UserAccountInfo) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val newPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.new_password_input)
        val confirmPasswordInput = dialogView.findViewById<TextInputEditText>(R.id.confirm_password_input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ubah Password\n📧 ${accountInfo.user.username}")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val newPassword = newPasswordInput.text.toString().trim()
                val confirmPassword = confirmPasswordInput.text.toString().trim()

                when {
                    newPassword.isEmpty() -> {
                        Toast.makeText(context, " assword tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.length < 6 -> {
                        Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmPassword -> {
                        Toast.makeText(context, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val index = accountsList.indexOf(accountInfo)
                        if (index != -1) {
                            val updatedUser = accountInfo.user.copy(password = newPassword)
                            accountsList[index] = accountInfo.copy(user = updatedUser)

                            if (accountInfo.isActive) {
                                sharedViewModel.setCurrentUser(updatedUser)
                            }

                            Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                            // PENTING: , panggil API untuk mengubah password
                        }
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showChangeRoleDialog(accountInfo: UserAccountInfo) {
        val roles = arrayOf("Kasir", "Owner")
        val currentIndex = roles.indexOf(accountInfo.user.role)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("👤 Ubah Role\n📧 ${accountInfo.user.username}")
            .setSingleChoiceItems(roles, currentIndex) { dialog, which ->
                val newRole = roles[which]
                if (newRole != accountInfo.user.role) {
                    val index = accountsList.indexOf(accountInfo)
                    if (index != -1) {
                        val updatedUser = accountInfo.user.copy(role = newRole)
                        accountsList[index] = accountInfo.copy(user = updatedUser)

                        if (accountInfo.isActive) {
                            sharedViewModel.setCurrentUser(updatedUser)
                        }

                        updateCurrentAccountDisplay() // Perbarui tampilan jika role akun aktif berubah
                        Toast.makeText(context, "Role diubah menjadi $newRole", Toast.LENGTH_SHORT).show()
                        //panggil API untuk mengubah role
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDeleteAccount(accountInfo: UserAccountInfo) {
        val message = if (accountInfo.isActive) {
            "⚠️ Akun ini sedang aktif!\n\nApakah Anda yakin ingin menghapus akun:\n📧 ${accountInfo.user.username}?"
        } else {
            "Apakah Anda yakin ingin menghapus akun:\n📧 ${accountInfo.user.username}?"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🗑️ Hapus Akun")
            .setMessage(message)
            .setPositiveButton("Ya, Hapus") { _, _ ->
                val wasActive = accountInfo.isActive
                accountsList.remove(accountInfo)

                if (wasActive) {
                    if (accountsList.isNotEmpty()) {
                        // Jika akun yang dihapus adalah akun aktif, aktifkan akun pertama yang tersisa
                        accountsList[0].isActive = true
                        sharedViewModel.setCurrentUser(accountsList[0].user)
                    } else {
                        // Jika tidak ada akun tersisa, pastikan tampilan akun aktif dikosongkan
                        sharedViewModel.setCurrentUser(null) // Hapus user dari ViewModel jika tidak ada akun
                        // Mungkin perlu navigasi kembali ke login jika tidak ada akun tersisa sama sekali
                    }
                }
                updateCurrentAccountDisplay()
                Toast.makeText(context, "✅ Akun ${accountInfo.user.username} berhasil dihapus", Toast.LENGTH_SHORT).show()
                //panggil API untuk menghapus user
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}