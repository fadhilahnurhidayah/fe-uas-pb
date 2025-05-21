package com.example.kantinsekre.presentation.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kantinsekre.R
import com.google.android.material.textfield.TextInputLayout

class SettingsFragment : Fragment() {

    private var currentAccountName: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeNameEdit = view.findViewById<EditText>(R.id.store_name_edit)
        val taxRateEdit = view.findViewById<EditText>(R.id.tax_rate_edit)
        val receiptSwitch = view.findViewById<Switch>(R.id.receipt_switch)
        val darkModeSwitch = view.findViewById<Switch>(R.id.dark_mode_switch)
        currentAccountName = view.findViewById(R.id.current_account_name)

        // Account related buttons
        val addAccountButton = view.findViewById<Button>(R.id.add_account_button)
        val manageAccountsButton = view.findViewById<Button>(R.id.manage_accounts_button)

        // Load dummy data
        storeNameEdit.setText("Kantin Sekre")
        taxRateEdit.setText("10.0")
        receiptSwitch.isChecked = true
        darkModeSwitch.isChecked = false
        currentAccountName?.text = "admin@kantinsekre.com"

        // Set up account functionality
        setupAccountFeatures(addAccountButton, manageAccountsButton)

        view.findViewById<Button>(R.id.save_settings_button).setOnClickListener {
            val name = storeNameEdit.text.toString()
            val tax = taxRateEdit.text.toString()
            val receipt = receiptSwitch.isChecked
            val dark = darkModeSwitch.isChecked

            Toast.makeText(context, "Pengaturan disimpan", Toast.LENGTH_SHORT).show()
            // Simpan ke SharedPreferences atau DataStore di sini jika diperlukan
        }
    }

    private fun setupAccountFeatures(addAccountButton: Button, manageAccountsButton: Button) {
        // Add account functionality
        addAccountButton.setOnClickListener {
            showAddAccountDialog()
        }

        // Manage accounts functionality
        manageAccountsButton.setOnClickListener {
            showManageAccountsDialog()
        }
    }

    private fun showAddAccountDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.account_email_input)
        val passwordInput = dialogView.findViewById<EditText>(R.id.account_password_input)
        val roleInput = dialogView.findViewById<EditText>(R.id.account_role_input)

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Akun Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah") { dialog, _ ->
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()
                val role = roleInput.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty() && role.isNotEmpty()) {
                    addNewAccount(email, password, role)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Semua data harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun addNewAccount(email: String, password: String, role: String) {
        // Di sini Anda akan menambahkan logika untuk menyimpan akun ke database
        // atau sistem penyimpanan lainnya

        // Contoh implementasi sederhana (dummy):
        Toast.makeText(context, "Akun $email berhasil ditambahkan sebagai $role", Toast.LENGTH_SHORT).show()

        // Update current account display if needed
        currentAccountName?.text = email
    }

    private fun showManageAccountsDialog() {
        // Dummy data untuk contoh
        val accounts = listOf(
            Pair("admin@kantinsekre.com", "Admin"),
            Pair("kasir@kantinsekre.com", "Kasir"),
            Pair("manager@kantinsekre.com", "Manager")
        )

        // Convert accounts to array for dialog
        val accountsArray = accounts.map { "${it.first} (${it.second})" }.toTypedArray()

        // Show dialog with account list
        AlertDialog.Builder(requireContext())
            .setTitle("Kelola Akun")
            .setItems(accountsArray) { _, position ->
                showAccountOptionsDialog(accounts[position].first, accounts[position].second)
            }
            .setNeutralButton("Tutup", null)
            .create()
            .show()
    }

    private fun showAccountOptionsDialog(email: String, role: String) {
        val options = arrayOf("Pilih sebagai akun aktif", "Ubah kata sandi", "Hapus akun")

        AlertDialog.Builder(requireContext())
            .setTitle(email)
            .setItems(options) { _, position ->
                when (position) {
                    0 -> {
                        // Set as active account
                        currentAccountName?.text = email
                        Toast.makeText(context, "$email dipilih sebagai akun aktif", Toast.LENGTH_SHORT).show()
                    }
                    1 -> showChangePasswordDialog(email)
                    2 -> confirmDeleteAccount(email)
                }
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    private fun showChangePasswordDialog(email: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val newPasswordInput = dialogView.findViewById<EditText>(R.id.new_password_input)
        val confirmPasswordInput = dialogView.findViewById<EditText>(R.id.confirm_password_input)

        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Kata Sandi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { dialog, _ ->
                val newPassword = newPasswordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()

                if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                    // Update password logic here
                    Toast.makeText(context, "Kata sandi untuk $email berhasil diubah", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Kata sandi tidak cocok atau kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun confirmDeleteAccount(email: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage("Apakah Anda yakin ingin menghapus akun $email?")
            .setPositiveButton("Ya") { _, _ ->
                // Delete account logic here
                Toast.makeText(context, "Akun $email berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .create()
            .show()
    }
}