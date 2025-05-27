package com.example.kantinsekre.presentation.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.kantinsekre.R

data class UserAccount(
    val email: String,
    val password: String,
    val role: String,
    val isActive: Boolean = false,
    val createdDate: String = ""
)

class SettingsFragment : Fragment() {

    private var currentAccountName: TextView? = null
    private var currentAccountRole: TextView? = null
    private val accountsList = mutableListOf<UserAccount>()

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
        loadDummyData()
        setupAccountFeatures(view)
        setupSaveButton(view)
    }

    private fun initializeViews(view: View) {
        currentAccountName = view.findViewById(R.id.current_account_name)
        currentAccountRole = view.findViewById(R.id.current_account_role)
    }

    private fun loadDummyData() {
        val storeNameEdit = view?.findViewById<EditText>(R.id.store_name_edit)
        val darkModeSwitch = view?.findViewById<Switch>(R.id.dark_mode_switch)

        storeNameEdit?.setText("Kantin Sekre")
        darkModeSwitch?.isChecked = false
        accountsList.clear()
        accountsList.addAll(listOf(
            UserAccount("owner@kantinsekre.com", "owner123", "Owner", true, "01/01/2024"),
            UserAccount("kasir1@kantinsekre.com", "kasir123", "Kasir", false, "15/01/2024"),
            UserAccount("kasir2@kantinsekre.com", "kasir456", "Kasir", false, "20/01/2024")
        ))

        updateCurrentAccountDisplay()
    }

    private fun updateCurrentAccountDisplay() {
        val activeAccount = accountsList.find { it.isActive }
        currentAccountName?.text = activeAccount?.email ?: "Tidak ada akun aktif"
        currentAccountRole?.text = activeAccount?.role ?: ""
    }

    private fun setupAccountFeatures(view: View) {
        val addAccountButton = view.findViewById<Button>(R.id.add_account_button)
        val manageAccountsButton = view.findViewById<Button>(R.id.manage_accounts_button)

        addAccountButton.setOnClickListener {
            showAddAccountDialog()
        }

        manageAccountsButton.setOnClickListener {
            showManageAccountsDialog()
        }
    }

    private fun setupSaveButton(view: View) {
        view.findViewById<Button>(R.id.save_settings_button).setOnClickListener {
            val storeNameEdit = view.findViewById<EditText>(R.id.store_name_edit)
            val darkModeSwitch = view.findViewById<Switch>(R.id.dark_mode_switch)

            val name = storeNameEdit.text.toString()
            val dark = darkModeSwitch.isChecked

            if (name.isNotEmpty()) {
                Toast.makeText(context, "Pengaturan berhasil disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddAccountDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.account_email_input)
        val passwordInput = dialogView.findViewById<EditText>(R.id.account_password_input)
        val roleSpinner = dialogView.findViewById<Spinner>(R.id.account_role_spinner)

        // Setup role spinner
        val roles = arrayOf("Kasir", "Owner")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
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
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.purple_700, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(android.R.color.darker_gray, null))
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
            accountsList.any { it.email == email } -> {
                Toast.makeText(context, "Email sudah terdaftar", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun addNewAccount(email: String, password: String, role: String) {
        val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())

        val newAccount = UserAccount(email, password, role, false, currentDate)
        accountsList.add(newAccount)

        Toast.makeText(
            context,
            "✅ Akun $role berhasil ditambahkan!\n📧 $email",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showManageAccountsDialog() {
        if (accountsList.isEmpty()) {
            Toast.makeText(context, "📭 Belum ada akun yang terdaftar", Toast.LENGTH_SHORT).show()
            return
        }

        val accountsDisplay = accountsList.map { account ->
            val status = if (account.isActive) "🟢 AKTIF" else "⚪"
            "$status ${account.email}\n👤 ${account.role} • 📅 ${account.createdDate}"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("👥 Kelola Akun (${accountsList.size} akun)")
            .setItems(accountsDisplay) { _, position ->
                showAccountOptionsDialog(accountsList[position])
            }
            .setNeutralButton("Tutup", null)
            .create()
            .show()
    }

    private fun showAccountOptionsDialog(account: UserAccount) {
        val options = mutableListOf<String>()

        if (!account.isActive) {
            options.add("🔄 Aktifkan Akun")
        }
        options.addAll(listOf(
            "🔑 Ubah Password",
            "👤 Ubah Role",
            "🗑️ Hapus Akun"
        ))

        AlertDialog.Builder(requireContext())
            .setTitle("📧 ${account.email}\n👤 ${account.role}")
            .setItems(options.toTypedArray()) { _, position ->
                when (options[position]) {
                    "🔄 Aktifkan Akun" -> setActiveAccount(account)
                    "🔑 Ubah Password" -> showChangePasswordDialog(account)
                    "👤 Ubah Role" -> showChangeRoleDialog(account)
                    "🗑️ Hapus Akun" -> confirmDeleteAccount(account)
                }
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    private fun setActiveAccount(account: UserAccount) {
        // Deactivate all accounts
        accountsList.forEachIndexed { index, acc ->
            accountsList[index] = acc.copy(isActive = false)
        }

        // Activate selected account
        val index = accountsList.indexOf(account)
        accountsList[index] = account.copy(isActive = true)

        updateCurrentAccountDisplay()
        Toast.makeText(context, "✅ ${account.email} sekarang aktif", Toast.LENGTH_SHORT).show()
    }

    private fun showChangePasswordDialog(account: UserAccount) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val newPasswordInput = dialogView.findViewById<EditText>(R.id.new_password_input)
        val confirmPasswordInput = dialogView.findViewById<EditText>(R.id.confirm_password_input)

        AlertDialog.Builder(requireContext())
            .setTitle("🔑 Ubah Password\n📧 ${account.email}")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val newPassword = newPasswordInput.text.toString().trim()
                val confirmPassword = confirmPasswordInput.text.toString().trim()

                when {
                    newPassword.isEmpty() -> {
                        Toast.makeText(context, "❌ Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                    newPassword.length < 6 -> {
                        Toast.makeText(context, "❌ Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmPassword -> {
                        Toast.makeText(context, "❌ Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val index = accountsList.indexOf(account)
                        accountsList[index] = account.copy(password = newPassword)
                        Toast.makeText(context, "✅ Password berhasil diubah", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showChangeRoleDialog(account: UserAccount) {
        val roles = arrayOf("Kasir", "Admin")
        val currentIndex = roles.indexOf(account.role)

        AlertDialog.Builder(requireContext())
            .setTitle("👤 Ubah Role\n📧 ${account.email}")
            .setSingleChoiceItems(roles, currentIndex) { dialog, which ->
                val newRole = roles[which]
                if (newRole != account.role) {
                    val index = accountsList.indexOf(account)
                    accountsList[index] = account.copy(role = newRole)

                    updateCurrentAccountDisplay()
                    Toast.makeText(context, "✅ Role diubah menjadi $newRole", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    private fun confirmDeleteAccount(account: UserAccount) {
        val message = if (account.isActive) {
            "⚠️ Akun ini sedang aktif!\n\nApakah Anda yakin ingin menghapus akun:\n📧 ${account.email}?"
        } else {
            "Apakah Anda yakin ingin menghapus akun:\n📧 ${account.email}?"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("🗑️ Hapus Akun")
            .setMessage(message)
            .setPositiveButton("Ya, Hapus") { _, _ ->
                accountsList.remove(account)
                if (account.isActive && accountsList.isNotEmpty()) {
                    // If deleted account was active, activate the first remaining account
                    accountsList[0] = accountsList[0].copy(isActive = true)
                }
                updateCurrentAccountDisplay()
                Toast.makeText(context, "✅ Akun ${account.email} berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }
}