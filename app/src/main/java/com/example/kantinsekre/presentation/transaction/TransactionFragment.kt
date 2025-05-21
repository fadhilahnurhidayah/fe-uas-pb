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
import java.text.NumberFormat
import java.util.*

class TransactionFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val currentItems = mutableListOf<Product>()
    private lateinit var totalTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalTextView = view.findViewById(R.id.total_text_view)

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.transaction_recycler_view)
        transactionAdapter = TransactionAdapter(currentItems) { product, position ->
            // Handle item removal
            currentItems.removeAt(position)
            transactionAdapter.notifyItemRemoved(position)
            updateTotal()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }

        // Load transactions from dummy data
        loadTransactionsFromDummy()

        // Pay Button
        view.findViewById<Button>(R.id.pay_button).setOnClickListener {
            // Simulasi proses transaksi baru
            processTransaction()
        }

        // Clear Button
        view.findViewById<Button>(R.id.clear_button).setOnClickListener {
            currentItems.clear()
            transactionAdapter.notifyDataSetChanged()
            updateTotal()
        }
    }

    private fun loadTransactionsFromDummy() {
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
        updateTotal()
    }

    private fun updateTotal() {
        val total = currentItems.sumOf { it.price * it.quantity }
        val formatted = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(total)
        totalTextView.text = "Total: $formatted"
    }

    private fun processTransaction() {
        // Di demo ini, transaksi tidak disimpan ulang ke dummy
        currentItems.clear()
        transactionAdapter.notifyDataSetChanged()
        updateTotal()
    }
}
