package com.example.kantinsekre.util

import com.example.kantinsekre.models.DetailTransaction
import com.example.kantinsekre.models.Product
import com.example.kantinsekre.models.Report
import com.example.kantinsekre.models.Transaction
import com.example.kantinsekre.models.User
import java.time.LocalDate

object DummyDataProvider {

    private val users = listOf(
        User(1, "admin", "admin123", "admin"),
        User(2, "kasir", "kasir123", "kasir")
    )

    fun login(username: String, password: String): User? {
        return users.find { it.username == username && it.password == password }
    }

    private val _productList = mutableListOf(
        Product(1, "Nasi Goreng", 15000, "Makanan", 10000),
        Product(2, "Mie Goreng", 12000, "Makanan", 8000),
        Product(3, "Es Teh", 5000, "Minuman", 2000),
        Product(4, "Es Jeruk", 6000, "Minuman", 3000),
        Product(5, "Ayam Goreng", 18000, "Makanan", 12000),
        Product(6, "Soto Ayam", 16000, "Makanan", 11000),
        Product(7, "Kopi Hitam", 7000, "Minuman", 3500),
        Product(8, "Kopi Susu", 9000, "Minuman", 5000)
    )
    val productList: List<Product> get() = _productList

    private val _transactionList = mutableListOf(
        Transaction(1, 2, "2025-05-07", 33000, 50000, 17000),
        Transaction(2, 2, "2025-05-08", 27000, 30000, 3000)
    )
    val transactionList: List<Transaction> get() = _transactionList

    private val _detailTransactionList = mutableListOf(
        DetailTransaction(1, 1, 1, 1, 15000),
        DetailTransaction(2, 1, 3, 2, 10000),
        DetailTransaction(3, 1, 8, 1, 8000),
        DetailTransaction(4, 2, 2, 1, 12000),
        DetailTransaction(5, 2, 3, 3, 15000)
    )
    val detailTransactionList: List<DetailTransaction> get() = _detailTransactionList

    private val _reportList = mutableListOf(
        Report(1, 2, 5, 2025, 60000, 3)
    )
    val reportList: List<Report> get() = _reportList

    fun getProductById(id: Int): Product? = _productList.find { it.id == id }

    fun getDetailByTransactionId(transactionId: Int): List<DetailTransaction> =
        _detailTransactionList.filter { it.transaksi_id == transactionId }


    fun createDetailTransaction(transactionId: Int, productId: Int, qty: Int, subtotal: Int): DetailTransaction {
        val newId = if (_detailTransactionList.isEmpty()) 1 else _detailTransactionList.maxOf { it.id } + 1
        val detail = DetailTransaction(newId, transactionId, productId, qty, subtotal)
        _detailTransactionList.add(detail)
        return detail
    }

    fun getProductByCategory(category: String): List<Product> =
        _productList.filter { it.category.equals(category, true) }

    fun searchProduct(keyword: String): List<Product> =
        _productList.filter { it.name.contains(keyword, true) }

    fun calculateProfit(productId: Int, qty: Int): Int =
        getProductById(productId)?.let { (it.price - it.costPrice) * qty } ?: 0

    fun addProduct(product: Product) {
        val newId = if (_productList.isEmpty()) 1 else _productList.maxOf { it.id } + 1
        _productList.add(product.copy(id = newId))
    }

    fun deleteProduct(id: Int) {
        _productList.removeIf { it.id == id }
    }

    fun deleteTransaction(id: Int) {
        _transactionList.removeIf { it.id == id }
        _detailTransactionList.removeIf { it.transaksi_id == id }
    }
}
