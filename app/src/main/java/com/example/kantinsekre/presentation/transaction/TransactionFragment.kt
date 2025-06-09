package com.example.kantinsekre.presentation.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kantinsekre.R
import com.example.kantinsekre.adapters.TransactionAdapter
import com.example.kantinsekre.models.Transaksi
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.presentation.viewmodel.TransactionViewModel
import com.example.kantinsekre.presentation.viewmodel.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var searchEditText: TextInputEditText
    private lateinit var toolbar: MaterialToolbar
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactions = mutableListOf<Transaksi>()
    private val filteredTransactions = mutableListOf<Transaksi>()

    // ViewModel dengan ViewModelFactory
    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

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
        observeViewModel()

        // Load data menggunakan ViewModel
        transactionViewModel.fetchAllTransactions()
    }

    /**
     * Observe ViewModel untuk perubahan data
     */
    private fun observeViewModel() {
        // Observe transactions list
        transactionViewModel.transactions.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Tampilkan loading indicator jika diperlukan
                }
                is UiState.Success -> {
                    transactions.clear()
                    transactions.addAll(sortTransactionsByDate(uiState.data))
                    filterTransactions(searchEditText.text.toString())
                }
                is UiState.Error -> {
                    Snackbar.make(requireView(), uiState.message, Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }



        // Observe update transaction result
        transactionViewModel.updateTransactionResult.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    // Tampilkan loading indicator jika diperlukan
                }
                is UiState.Success -> {
                    Snackbar.make(requireView(), "Status transaksi berhasil diupdate", Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    Snackbar.make(requireView(), "Gagal mengupdate status: ${uiState.message}", Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Idle -> {
                    // State awal, tidak perlu action
                }
            }
        }
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
        // Menu removed - using FAB for add transaction functionality
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



    @SuppressLint("NotifyDataSetChanged")
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

    private fun cancelTransaction(transaction: Transaksi) {
        // Gunakan updateTransaksi untuk mengubah status ke dibatalkan
        transactionViewModel.updateTransaksi(transaction.id.toString(), "dibatalkan")
    }

    private fun completeTransaction(transaction: Transaksi) {
        // Gunakan updateTransaksi untuk mengubah status ke selesai
        transactionViewModel.updateTransaksi(transaction.id.toString(), "selesai")
    }

    /**
     * Sort transaksi berdasarkan tanggal (terbaru ke terlama)
     */
    private fun sortTransactionsByDate(transactionList: List<Transaksi>): List<Transaksi> {
        return transactionList.sortedWith { t1, t2 ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date1 = dateFormat.parse(t1.tanggal)
                val date2 = dateFormat.parse(t2.tanggal)

                // Sort descending (terbaru ke terlama)
                when {
                    date1 == null && date2 == null -> 0
                    date1 == null -> 1
                    date2 == null -> -1
                    else -> date2.compareTo(date1)
                }
            } catch (_: Exception) {
                // Jika parsing gagal, sort berdasarkan ID (terbaru ke terlama)
                t2.id.compareTo(t1.id)
            }
        }
    }
}
