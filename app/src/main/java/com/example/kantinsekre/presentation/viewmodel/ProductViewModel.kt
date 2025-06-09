package com.example.kantinsekre.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kantinsekre.models.CreateMenu
import com.example.kantinsekre.models.Menu
import com.example.kantinsekre.presentation.state.UiState
import com.example.kantinsekre.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<UiState<List<Menu>>>()
    val products: LiveData<UiState<List<Menu>>> = _products

    private val _addProductResult = MutableLiveData<UiState<Unit>>()
    val addProductResult: LiveData<UiState<Unit>> = _addProductResult

    private val _deleteProductResult = MutableLiveData<UiState<Unit>>()
    val deleteProductResult: LiveData<UiState<Unit>> = _deleteProductResult

    fun fetchAllMenu() {
        viewModelScope.launch {
            _products.value = UiState.Loading
            try {
                val response = repository.getAllMenu()
                if (response.isSuccessful && response.body()?.success == true) {
                    val menuList = response.body()?.data ?: emptyList()
                    _products.value = UiState.Success(menuList)
                } else {
                    _products.value = UiState.Error("Gagal memuat menu")
                }
            } catch (e: Exception) {
                _products.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun addMenu(menu: CreateMenu) {
        viewModelScope.launch {
            _addProductResult.value = UiState.Loading
            try {
                val response = repository.addMenu(menu)
                if (response.isSuccessful) {
                    _addProductResult.value = UiState.Success(Unit)
                    fetchAllMenu()
                } else {
                    _addProductResult.value = UiState.Error("Gagal menambah menu")
                }
            } catch (e: Exception) {
                _addProductResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteMenu(id: String) {
        viewModelScope.launch {
            _deleteProductResult.value = UiState.Loading
            try {
                val response = repository.deleteMenu(id)
                if (response.isSuccessful) {
                    _deleteProductResult.value = UiState.Success(Unit)
                    fetchAllMenu()
                } else {
                    _deleteProductResult.value = UiState.Error("Gagal menghapus menu")
                }
            } catch (e: Exception) {
                _deleteProductResult.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetAddProductResult() {
        _addProductResult.value = UiState.Idle
    }

    fun resetDeleteProductResult() {
        _deleteProductResult.value = UiState.Idle
    }
}