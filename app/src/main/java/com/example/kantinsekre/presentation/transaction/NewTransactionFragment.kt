package com.example.kantinsekre.presentation.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.adapters.TransactionItemAdapter
import com.example.kantinsekre.models.ItemRequest
import com.example.kantinsekre.models.Menu
import com.example.kantinsekre.models.TransaksiRequest
import com.example.kantinsekre.presentation.viewmodel.SharedViewModel
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.TransactionViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NewTransactionFragment : Fragment() {

    private lateinit var customerNameEditText: TextInputEditText
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var addItemButton: MaterialButton
    private lateinit var saveTransactionButton: MaterialButton
    private lateinit var totalAmountText: TextView
    private val items = mutableListOf<ItemRequest>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: TransactionItemAdapter
    private var products: List<Menu> = emptyList()

    // ViewModel dengan ViewModelFactory
    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // Load products menggunakan ViewModel
        transactionViewModel.fetchAllProducts()
    }

    /**
     * Observe ViewModel untuk perubahan data
     */
    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        // Observe products
        transactionViewModel.products.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Tampilkan loading indicator jika diperlukan
                }
                is UiState.Success -> {
                    products = uiState.data
                }
                is UiState.Error -> {
                    Snackbar.make(requireView(), uiState.message, Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }

        // Observe add transaction result
        transactionViewModel.addTransactionResult.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    saveTransactionButton.isEnabled = false
                    saveTransactionButton.text = "Saving..."
                }
                is UiState.Success -> {
                    saveTransactionButton.isEnabled = true
                    saveTransactionButton.text = "Save Transaction"
                    Snackbar.make(requireView(), "Transaksi berhasil disimpan", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is UiState.Error -> {
                    saveTransactionButton.isEnabled = true
                    saveTransactionButton.text = "Save Transaction"
                    Snackbar.make(requireView(), uiState.message, Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    saveTransactionButton.isEnabled = true
                    saveTransactionButton.text = "Save Transaction"
                }
            }
        }
    }

    private fun initializeViews(view: View) {
        customerNameEditText = view.findViewById(R.id.customer_name_edit_text)
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view)
        addItemButton = view.findViewById(R.id.add_item_button)
        saveTransactionButton = view.findViewById(R.id.save_transaction_button)
        totalAmountText = view.findViewById(R.id.total_amount_text)
    }

    private fun setupRecyclerView() {
        adapter = TransactionItemAdapter(items) { item ->
            adapter.removeItem(item)
            updateTotalAmount()
        }
        itemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemsRecyclerView.adapter = adapter
    }

    private fun setupButtons() {
        addItemButton.setOnClickListener {
            if (products.isEmpty()) {
                Snackbar.make(requireView(), "Memuat daftar menu...", Snackbar.LENGTH_SHORT).show()
                transactionViewModel.fetchAllProducts() // Refresh menggunakan ViewModel
                return@setOnClickListener
            }
            showAddItemDialog()
        }

        saveTransactionButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun showAddItemDialog() {
        val productNames = products.map { "${it.nama} - ${formatCurrency(it.harga.toDoubleOrNull() ?: 0.0)}" }.toTypedArray()
        var selectedProduct: Menu? = null
        var selectedIndex = 0

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Menu")
            .setSingleChoiceItems(productNames, 0) { _, which ->
                selectedIndex = which
                selectedProduct = products[which]
            }
            .setPositiveButton("Pilih", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                selectedProduct?.let { product ->
                    showQuantityDialog(product)
                    dialog.dismiss()
                } ?: run {
                    Snackbar.make(requireView(), "Silakan pilih menu terlebih dahulu", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun showQuantityDialog(product: Menu) {
        val quantityEditText = EditText(requireContext()).apply {
            setText("1")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(3))
        }

        val price = product.harga.toDoubleOrNull() ?: 0.0
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Jumlah ${product.nama}")
            .setMessage("Harga: ${formatCurrency(price)}")
            .setView(quantityEditText)
            .setPositiveButton("Tambah", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val quantityText = quantityEditText.text.toString()
                if (quantityText.isEmpty()) {
                    Snackbar.make(requireView(), "Jumlah tidak boleh kosong", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val quantity = quantityText.toIntOrNull() ?: 0
                if (quantity <= 0) {
                    Snackbar.make(requireView(), "Jumlah harus lebih dari 0", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (quantity > 999) {
                    Snackbar.make(requireView(), "Jumlah maksimal 999", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val item = ItemRequest(
                    namaMenu = product.nama,
                    jumlahMenu = quantity.toString()
                )
                adapter.addItem(item)
                updateTotalAmount()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(amount)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalAmount() {
        var total = 0.0
        for (item in items) {
            val product = products.find { it.nama == item.namaMenu }
            product?.let {
                val quantity = item.jumlahMenu?.toIntOrNull() ?: 0
                val price = it.harga.toDoubleOrNull() ?: 0.0
                total += (price * quantity)
            }
        }
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        totalAmountText.text = "Total: ${format.format(total)}"
    }

    private fun saveTransaction() {
        val customerName = customerNameEditText.text.toString().trim()
        if (customerName.isEmpty()) {
            customerNameEditText.error = "Nama pembeli tidak boleh kosong"
            return
        }

        if (items.isEmpty()) {
            Snackbar.make(requireView(), "Tambahkan minimal satu item", Snackbar.LENGTH_SHORT).show()
            return
        }


        // Get current user data
        val currentUser = sharedViewModel.getCurrentUser()

        // Format tanggal dengan timezone Indonesia
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        }.format(Date())

        val userName = if (currentUser != null) {
            "${currentUser.nama}"
        } else {
            "owner"
        }

        val request = TransaksiRequest(
            namaPembeli = customerName,
            namaUser = userName,
            tanggal = currentDate,
            status = "pending",
            items = items
        )

        transactionViewModel.addTransaction(request)
    }
} 

