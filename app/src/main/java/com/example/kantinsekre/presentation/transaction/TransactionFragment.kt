package com.example.kantinsekre.presentation.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.adapters.TransactionAdapter
import com.example.kantinsekre.models.Product
import com.example.kantinsekre.util.DummyDataProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class TransactionFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val currentItems = mutableListOf<Product>()
    private lateinit var totalTextView: TextView
    private lateinit var itemCountTextView: TextView
    private lateinit var addItemButton: MaterialButton
    private lateinit var payButton: MaterialButton
    private lateinit var clearButton: MaterialButton
    private lateinit var emptyStateView: View
    private lateinit var transactionContainer: MaterialCardView

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
        setupButtons()
        loadTransactionsFromDummy()
        updateUI()
    }

    private fun initializeViews(view: View) {
        totalTextView = view.findViewById(R.id.total_text_view) ?: run { return }
        itemCountTextView = view.findViewById(R.id.item_count_text_view) ?: run { return }
        addItemButton = view.findViewById(R.id.add_item_button) ?: run { return }
        payButton = view.findViewById(R.id.pay_button) ?: run { return }
        clearButton = view.findViewById(R.id.clear_button) ?: run { return }
        emptyStateView = view.findViewById(R.id.empty_state_view) ?: run { return }
        transactionContainer = view.findViewById(R.id.transaction_container) ?: run { return }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.transaction_recycler_view) ?: return

        transactionAdapter = TransactionAdapter(currentItems) { product, position ->
            removeItem(product, position)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
            // Add item decoration for spacing
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(
                context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            ))
        }
    }

    private fun setupButtons() {
        addItemButton.setOnClickListener {
            showAddItemDialog()
        }

        payButton.setOnClickListener {
            if (currentItems.isNotEmpty()) {
                showPaymentConfirmationDialog()
            } else {
                Snackbar.make(requireView(), "No items to pay for", Snackbar.LENGTH_SHORT).show()
            }
        }

        clearButton.setOnClickListener {
            if (currentItems.isNotEmpty()) {
                showClearConfirmationDialog()
            }
        }
    }

    private fun showAddItemDialog() {
        val availableProducts = DummyDataProvider.productList
        if (availableProducts.isEmpty()) {
            Snackbar.make(requireView(), "No products available", Snackbar.LENGTH_SHORT).show()
            return
        }

        val productNames = availableProducts.map { "${it.name} - ${formatCurrency(it.price.toDouble())}" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Item to Transaction")
            .setItems(productNames) { _, which ->
                val selectedProduct = availableProducts[which]
                showQuantityDialog(selectedProduct)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showQuantityDialog(product: Product) {
        val quantities = arrayOf("1", "2", "3", "4", "5", "Custom")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Quantity for ${product.name}")
            .setItems(quantities) { _, which ->
                val quantity = when (which) {
                    5 -> {
                        // Custom quantity - could show number picker dialog
                        1 // Default to 1 for now
                    }
                    else -> which + 1
                }
                addItemToTransaction(product, quantity)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addItemToTransaction(product: Product, quantity: Int) {
        try {
            // Check if item already exists
            val existingItemIndex = currentItems.indexOfFirst { it.id == product.id }

            if (existingItemIndex != -1) {
                // Update existing item quantity
                val existingItem = currentItems[existingItemIndex]
                currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
                transactionAdapter.notifyItemChanged(existingItemIndex)
            } else {
                // Add new item
                val newItem = product.copy(quantity = quantity)
                currentItems.add(newItem)
                transactionAdapter.notifyItemInserted(currentItems.size - 1)
            }

            updateUI()
            Snackbar.make(requireView(), "${product.name} added to transaction", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Failed to add item", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun removeItem(product: Product, position: Int) {
        try {
            if (position in 0 until currentItems.size) {
                val removedItem = currentItems.removeAt(position)
                transactionAdapter.notifyItemRemoved(position)
                updateUI()

                // Show undo snackbar
                Snackbar.make(requireView(), "${removedItem.name} removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        currentItems.add(position, removedItem)
                        transactionAdapter.notifyItemInserted(position)
                        updateUI()
                    }
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Failed to remove item", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showPaymentConfirmationDialog() {
        val total = calculateTotal()
        val totalFormatted = formatCurrency(total)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Payment")
            .setMessage("Total amount: $totalFormatted\n\nProceed with payment?")
            .setPositiveButton("Pay Now") { _, _ ->
                processTransaction()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear Transaction")
            .setMessage("Are you sure you want to clear all items from this transaction?")
            .setPositiveButton("Clear") { _, _ ->
                clearTransaction()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearTransaction() {
        try {
            currentItems.clear()
            transactionAdapter.notifyDataSetChanged()
            updateUI()
            Snackbar.make(requireView(), "Transaction cleared", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Failed to clear transaction", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadTransactionsFromDummy() {
        try {
            if (DummyDataProvider.transactionList.isEmpty()) {
                currentItems.clear()
                transactionAdapter.notifyDataSetChanged()
                updateUI()
                return
            }

            val lastTransactionId = DummyDataProvider.transactionList.lastOrNull()?.id ?: return

            val items = DummyDataProvider.detailTransactionList
                .filter { it.transaksi_id == lastTransactionId }
                .mapNotNull { detail ->
                    val product = DummyDataProvider.productList.find { it.id == detail.menu_id }
                    product?.copy(quantity = detail.qty)
                }

            currentItems.clear()
            currentItems.addAll(items)
            transactionAdapter.notifyDataSetChanged()
            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
            currentItems.clear()
            transactionAdapter.notifyDataSetChanged()
            updateUI()
        }
    }

    private fun updateUI() {
        try {
            val total = calculateTotal()
            val itemCount = currentItems.size
            val totalQuantity = currentItems.sumOf { it.quantity }

            totalTextView.text = formatCurrency(total)
            itemCountTextView.text = "$itemCount items ($totalQuantity qty)"

            // Show/hide empty state
            if (currentItems.isEmpty()) {
                emptyStateView.visibility = View.VISIBLE
                transactionContainer.visibility = View.GONE
                payButton.isEnabled = false
                clearButton.isEnabled = false
            } else {
                emptyStateView.visibility = View.GONE
                transactionContainer.visibility = View.VISIBLE
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
            // Simulate payment processing
            val total = calculateTotal()

            // Clear transaction items
            currentItems.clear()
            transactionAdapter.notifyDataSetChanged()
            updateUI()

            // Show success message
            Snackbar.make(requireView(),
                "Payment successful! Total: ${formatCurrency(total)}",
                Snackbar.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(requireView(), "Payment failed. Please try again.", Snackbar.LENGTH_SHORT).show()
        }
    }
}