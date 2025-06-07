package com.example.kantinsekre.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kantinsekre.models.Menu

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Menu>>()
    val products: LiveData<List<Menu>> = _products

    fun setProducts(menuList: List<Menu>) {
        _products.value = menuList
    }
}