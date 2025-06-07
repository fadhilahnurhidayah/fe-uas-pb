package com.example.kantinsekre.presentation.transaction

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.adapters.TransactionItemAdapter
import com.example.kantinsekre.models.ItemRequest
import com.example.kantinsekre.models.Menu
import com.example.kantinsekre.models.TransaksiRequest
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.presentation.SharedViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

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
        loadProducts()
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
                loadProducts()
                return@setOnClickListener
            }
            showAddItemDialog()
        }

        saveTransactionButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val response = apiService.getAllMenu()
                if (response.isSuccessful && response.body()?.success == true) {
                    products = response.body()?.data ?: emptyList()
                } else {
                    Snackbar.make(requireView(), "Gagal memuat daftar menu", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddItemDialog() {
        val productNames = products.map { "${it.nama} - ${formatCurrency(it.harga?.toDoubleOrNull() ?: 0.0)}" }.toTypedArray()
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

        val price = product.harga?.toDoubleOrNull() ?: 0.0
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

    private fun updateTotalAmount() {
        var total = 0.0
        for (item in items) {
            val product = products.find { it.nama == item.namaMenu }
            product?.let {
                val quantity = item.jumlahMenu?.toIntOrNull() ?: 0
                val price = it.harga?.toDoubleOrNull() ?: 0.0
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

        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val currentUser = sharedViewModel.getCurrentUser()
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                val request = TransaksiRequest(
                    namaPembeli = customerName,
                    namaUser = currentUser?.nama ?: "admin",
                    tanggal = currentDate,
                    items = items
                )

                val response = apiService.addTransaksi(request)
                if (response.isSuccessful) {
                    Snackbar.make(requireView(), "Transaksi berhasil disimpan", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Snackbar.make(requireView(), "Gagal menyimpan transaksi", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
} 

