package com.example.kantinsekre.presentation.transaction

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.adapters.TransactionAdapter
import com.example.kantinsekre.models.Transaksi
import com.example.kantinsekre.models.TransaksiRequest
import com.example.kantinsekre.models.ItemRequest
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.presentation.SharedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var searchEditText: TextInputEditText
    private lateinit var toolbar: MaterialToolbar
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<Transaksi>()
    private val filteredTransactions = mutableListOf<Transaksi>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        loadTransactions()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.transaction_recycler_view)
        emptyStateText = view.findViewById(R.id.empty_state_text)
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction)
        searchEditText = view.findViewById(R.id.search_edit_text)
        toolbar = view.findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        toolbar.title = "Transaksi"
        toolbar.inflateMenu(R.menu.transaction_menu)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_struk -> {
                    findNavController().navigate(R.id.action_transactionFragment_to_newTransactionFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            transactions = filteredTransactions,
            onCancelClick = { transaction -> cancelTransaction(transaction) },
            onCompleteClick = { transaction -> completeTransaction(transaction) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = transactionAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterTransactions(s.toString())
            }
        })
    }

    private fun setupFab() {
        fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_transactionFragment_to_newTransactionFragment)
        }
    }

    private fun filterTransactions(query: String) {
        filteredTransactions.clear()
        if (query.isEmpty()) {
            filteredTransactions.addAll(transactions)
        } else {
            filteredTransactions.addAll(
                transactions.filter {
                    it.namaPembeli?.contains(query, ignoreCase = true) == true ||
                    it.tanggal.contains(query, ignoreCase = true)
                }
            )
        }
        transactionAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (filteredTransactions.isEmpty()) {
            emptyStateText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadTransactions() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val response = apiService.getAllTransaksi()
                if (response.isSuccessful && response.body()?.success == true) {
                    transactions.clear()
                    response.body()?.data?.filterNotNull()?.let { transactionList ->
                        transactions.addAll(transactionList)
                    }
                    filterTransactions(searchEditText.text.toString())
                } else {
                    Snackbar.make(requireView(), "Gagal memuat transaksi", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelTransaction(transaction: Transaksi) {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val currentUser = sharedViewModel.getCurrentUser()
                val request = TransaksiRequest(
                    namaPembeli = transaction.namaPembeli ?: "",
                    namaUser = currentUser?.nama ?: "admin",
                    tanggal = transaction.tanggal,
                    items = emptyList() // TODO: Get actual items from transaction
                )
                val response = apiService.updateTransaksi(
                    id = transaction.id.toString(),
                    request = request
                )
                if (response.isSuccessful) {
                    loadTransactions()
                    Snackbar.make(requireView(), "Transaksi dibatalkan", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Gagal membatalkan transaksi: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun completeTransaction(transaction: Transaksi) {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.create(requireContext())
                val currentUser = sharedViewModel.getCurrentUser()
                val request = TransaksiRequest(
                    namaPembeli = transaction.namaPembeli ?: "",
                    namaUser = currentUser?.nama ?: "admin",
                    tanggal = transaction.tanggal,
                    items = emptyList() // TODO: Get actual items from transaction
                )
                val response = apiService.updateTransaksi(
                    id = transaction.id.toString(),
                    request = request
                )
                if (response.isSuccessful) {
                    loadTransactions()
                    Snackbar.make(requireView(), "Transaksi selesai", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Snackbar.make(requireView(), "Gagal menyelesaikan transaksi: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
