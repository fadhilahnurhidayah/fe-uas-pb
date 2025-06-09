package com.example.kantinsekre.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kantinsekre.models.Menu
import com.example.kantinsekre.models.StatusUpdateRequest
import com.example.kantinsekre.models.Transaksi
import com.example.kantinsekre.models.TransaksiRequest
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.repository.ProductRepository
import com.example.kantinsekre.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    // LiveData untuk daftar transaksi
    private val _transactions = MutableLiveData<UiState<List<Transaksi>>>()
    val transactions: LiveData<UiState<List<Transaksi>>> = _transactions

    // LiveData untuk daftar produk (untuk new transaction)
    private val _products = MutableLiveData<UiState<List<Menu>>>()
    val products: LiveData<UiState<List<Menu>>> = _products

    // LiveData untuk operasi add transaction
    private val _addTransactionResult = MutableLiveData<UiState<Unit>>()
    val addTransactionResult: LiveData<UiState<Unit>> = _addTransactionResult

    // LiveData untuk operasi delete transaction
    private val _deleteTransactionResult = MutableLiveData<UiState<Unit>>()
    val deleteTransactionResult: LiveData<UiState<Unit>> = _deleteTransactionResult



    // LiveData untuk operasi update transaction (status only)
    private val _updateTransactionResult = MutableLiveData<UiState<Unit>>()
    val updateTransactionResult: LiveData<UiState<Unit>> = _updateTransactionResult


    fun fetchAllTransactions() {
        viewModelScope.launch {
            _transactions.value = UiState.Loading
            try {
                val response = transactionRepository.getAllTransaksi()
                if (response.isSuccessful && response.body()?.success == true) {
                    val transactionList = response.body()?.data?.filterNotNull() ?: emptyList()
                    _transactions.value = UiState.Success(transactionList)
                } else {
                    _transactions.value = UiState.Error("Gagal memuat transaksi")
                }
            } catch (e: Exception) {
                _transactions.value = UiState.Error("Error: ${e.message}")
            }
        }
    }


    fun fetchAllProducts() {
        viewModelScope.launch {
            _products.value = UiState.Loading
            try {
                val response = productRepository.getAllMenu()
                if (response.isSuccessful && response.body()?.success == true) {
                    val productList = response.body()?.data ?: emptyList()
                    _products.value = UiState.Success(productList)
                } else {
                    _products.value = UiState.Error("Gagal memuat daftar menu")
                }
            } catch (e: Exception) {
                _products.value = UiState.Error("Error: ${e.message}")
            }
        }
    }


    fun addTransaction(request: TransaksiRequest) {
        viewModelScope.launch {
            _addTransactionResult.value = UiState.Loading
            try {
                val response = transactionRepository.addTransaksi(request)
                if (response.isSuccessful) {
                    _addTransactionResult.value = UiState.Success(Unit)
                    // Refresh data setelah berhasil menambah
                    fetchAllTransactions()
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Data transaksi tidak valid"
                        401 -> "Tidak memiliki izin untuk menambah transaksi"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal menyimpan transaksi: ${response.message()}"
                    }
                    _addTransactionResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _addTransactionResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }


    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            _deleteTransactionResult.value = UiState.Loading
            try {
                val response = transactionRepository.deleteTransaksi(id)
                if (response.isSuccessful) {
                    _deleteTransactionResult.value = UiState.Success(Unit)
                    // Refresh data setelah berhasil menghapus
                    fetchAllTransactions()
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Transaksi tidak ditemukan"
                        403 -> "Tidak memiliki izin untuk menghapus transaksi"
                        409 -> "Transaksi tidak dapat dihapus karena sudah diproses"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal menghapus transaksi: ${response.message()}"
                    }
                    _deleteTransactionResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _deleteTransactionResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateTransaksi(transactionId: String, status: String) {
        viewModelScope.launch {
            _updateTransactionResult.value = UiState.Loading
            try {
                val statusRequest = StatusUpdateRequest(status = status)
                val response = transactionRepository.updateTransaksi(
                    id = transactionId,
                    statusRequest = statusRequest
                )
                if (response.isSuccessful) {
                    _updateTransactionResult.value = UiState.Success(Unit)
                    // Refresh data setelah berhasil mengupdate
                    fetchAllTransactions()
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Transaksi tidak ditemukan"
                        403 -> "Tidak memiliki izin untuk mengupdate transaksi"
                        409 -> "Transaksi tidak dapat diupdate"
                        500 -> "Server error, coba lagi nanti"
                        else -> "Gagal mengupdate transaksi: ${response.message()}"
                    }
                    _updateTransactionResult.value = UiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _updateTransactionResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }


    fun resetAddTransactionResult() {
        _addTransactionResult.value = UiState.Idle
    }

    fun resetDeleteTransactionResult() {
        _deleteTransactionResult.value = UiState.Idle
    }


    fun resetUpdateTransactionResult() {
        _updateTransactionResult.value = UiState.Idle
    }


    fun resetAllResults() {
        resetAddTransactionResult()
        resetDeleteTransactionResult()
        resetUpdateTransactionResult()
    }
}
