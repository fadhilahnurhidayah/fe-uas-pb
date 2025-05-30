package com.example.kantinsekre.presentation.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.adapters.TransactionAdapter
import com.example.kantinsekre.models.Product
//import com.example.kantinsekre.util.DummyDataProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val currentItems = mutableListOf<Product>()
    private lateinit var totalTextView: TextView

    private lateinit var itemCountTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var transactionIdTextView: TextView
    private lateinit var addItemButton: MaterialButton
    private lateinit var payButton: MaterialButton
    private lateinit var clearButton: MaterialButton
    private lateinit var emptyStateView: View
    private lateinit var billContainer: MaterialCardView
    private lateinit var summaryContainer: MaterialCardView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView(view)
//        setupButtons()
        initializeTransaction()
        updateUI()
    }

    private fun initializeViews(view: View) {
        totalTextView = view.findViewById(R.id.total_text_view)
        itemCountTextView = view.findViewById(R.id.item_count_text_view)
        dateTimeTextView = view.findViewById(R.id.datetime_text_view)
        transactionIdTextView = view.findViewById(R.id.transaction_id_text_view)
        addItemButton = view.findViewById(R.id.add_item_button)
        payButton = view.findViewById(R.id.pay_button)
        clearButton = view.findViewById(R.id.clear_button)
        emptyStateView = view.findViewById(R.id.empty_state_view)
        billContainer = view.findViewById(R.id.bill_container)
        summaryContainer = view.findViewById(R.id.summary_container)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.transaction_recycler_view)

        transactionAdapter = TransactionAdapter(currentItems) { product, position ->
            showRemoveItemDialog(product, position)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

//    private fun setupButtons() {
//        addItemButton.setOnClickListener {
//            showAddItemDialog()
//        }
//
//        payButton.setOnClickListener {
//            if (currentItems.isNotEmpty()) {
//                showPaymentConfirmationDialog()
//            } else {
//                Snackbar.make(requireView(), "Tidak ada item untuk dibayar", Snackbar.LENGTH_SHORT).show()
//            }
//        }
//
//        clearButton.setOnClickListener {
//            if (currentItems.isNotEmpty()) {
//                showClearConfirmationDialog()
//            }
//        }
//    }

    private fun initializeTransaction() {
        // Set transaction ID and datetime
        val transactionId = "TXN${System.currentTimeMillis().toString().takeLast(8)}"
        val currentDateTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())

        transactionIdTextView.text = transactionId
        dateTimeTextView.text = currentDateTime
    }

//    private fun showAddItemDialog() {
//        val availableProducts = DummyDataProvider.productList
//        if (availableProducts.isEmpty()) {
//            Snackbar.make(requireView(), "Tidak ada produk tersedia", Snackbar.LENGTH_SHORT).show()
//            return
//        }
//
//        val productNames = availableProducts.map {
//            "${it.name}\n${formatCurrency(it.price.toDouble())}"
//        }.toTypedArray()
//
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Pilih Item")
//            .setItems(productNames) { _, which ->
//                val selectedProduct = availableProducts[which]
//                showQuantityDialog(selectedProduct)
//            }
//            .setNegativeButton("Batal", null)
//            .show()
//    }

    private fun showQuantityDialog(product: Product) {
        val quantities = arrayOf("1", "2", "3", "4", "5", "10")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Jumlah untuk ${product.name}")
            .setItems(quantities) { _, which ->
                val quantity = when (which) {
                    5 -> 10
                    else -> which + 1
                }
                addItemToTransaction(product, quantity)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun addItemToTransaction(product: Product, quantity: Int) {
        try {
            val existingItemIndex = currentItems.indexOfFirst { it.id == product.id }

            if (existingItemIndex != -1) {
                val existingItem = currentItems[existingItemIndex]
                currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
                transactionAdapter.notifyItemChanged(existingItemIndex)
            } else {
                val newItem = product.copy(quantity = quantity)
                currentItems.add(newItem)
                transactionAdapter.notifyItemInserted(currentItems.size - 1)
            }

            updateUI()
            Snackbar.make(requireView(), "${product.name} ditambahkan ke transaksi", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Gagal menambahkan item", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showRemoveItemDialog(product: Product, position: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Item")
            .setMessage("Hapus ${product.name} dari transaksi?")
            .setPositiveButton("Hapus") { _, _ ->
                removeItem(product, position)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun removeItem(product: Product, position: Int) {
        try {
            if (position in 0 until currentItems.size) {
                val removedItem = currentItems.removeAt(position)
                transactionAdapter.notifyItemRemoved(position)
                updateUI()

                Snackbar.make(requireView(), "${removedItem.name} dihapus", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        currentItems.add(position, removedItem)
                        transactionAdapter.notifyItemInserted(position)
                        updateUI()
                    }
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Gagal menghapus item", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showPaymentConfirmationDialog() {
        val total = calculateTotal()

        val message = """
            Total: ${formatCurrency(total)}
            
            Lanjutkan pembayaran?
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Pembayaran")
            .setMessage(message)
            .setPositiveButton("Bayar Sekarang") { _, _ ->
                processTransaction()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showClearConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Bersihkan Transaksi")
            .setMessage("Hapus semua item dari transaksi ini?")
            .setPositiveButton("Bersihkan") { _, _ ->
                clearTransaction()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun clearTransaction() {
        try {
            currentItems.clear()
            transactionAdapter.notifyDataSetChanged()
            updateUI()
            Snackbar.make(requireView(), "Transaksi dibersihkan", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Gagal membersihkan transaksi", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        try {
            val total = calculateTotal()
            val itemCount = currentItems.size
            val totalQuantity = currentItems.sumOf { it.quantity }

            totalTextView.text = formatCurrency(total)
            itemCountTextView.text = "$itemCount item${if (itemCount != 1) "s" else ""} ($totalQuantity qty)"

            if (currentItems.isEmpty()) {
                emptyStateView.visibility = View.VISIBLE
                billContainer.visibility = View.GONE
                summaryContainer.visibility = View.GONE
                payButton.isEnabled = false
                clearButton.isEnabled = false
            } else {
                emptyStateView.visibility = View.GONE
                billContainer.visibility = View.VISIBLE
                summaryContainer.visibility = View.VISIBLE
                payButton.isEnabled = true
                clearButton.isEnabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            totalTextView.text = formatCurrency(0.0)
            itemCountTextView.text = "0 items"
        }
    }

    private fun calculateTotal(): Double {
        return currentItems.sumOf { it.price.toDouble() * it.quantity.toDouble() }
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(amount)
    }

    private fun processTransaction() {
        try {
            val total = calculateTotal()

            // Save transaction to dummy data or database here

            currentItems.clear()
            transactionAdapter.notifyDataSetChanged()
            updateUI()

            Snackbar.make(requireView(),
                "Pembayaran berhasil! Total: ${formatCurrency(total)}",
                Snackbar.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Pembayaran gagal. Silakan coba lagi.", Snackbar.LENGTH_SHORT).show()
        }
    }
}