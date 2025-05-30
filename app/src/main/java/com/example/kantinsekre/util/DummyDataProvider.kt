//package com.example.kantinsekre.util
//
//import com.example.kantinsekre.models.DetailTransaction
//import com.example.kantinsekre.models.Product
//import com.example.kantinsekre.models.Report
//import com.example.kantinsekre.models.Transaksi
//import com.example.kantinsekre.models.User
//
//object DummyDataProvider {
//
//    private val _transactionList = mutableListOf(
//        Transaksi(1, 2, "2025-05-07", 33000, 50000, 17000),
//        Transaksi(2, 2, "2025-05-08", 27000, 30000, 3000)
//    )
//    val transactionList: List<Transaksi> get() = _transactionList
//
//    private val _detailTransactionList = mutableListOf(
//        DetailTransaction(1, 1, 1, 1, 15000),
//        DetailTransaction(2, 1, 3, 2, 10000),
//        DetailTransaction(3, 1, 8, 1, 8000),
//        DetailTransaction(4, 2, 2, 1, 12000),
//        DetailTransaction(5, 2, 3, 3, 15000)
//    )
//    val detailTransactionList: List<DetailTransaction> get() = _detailTransactionList
//
//    private val _reportList = mutableListOf<Report>()
//    val reportList: List<Report> get() = _reportList
//
//    fun getProductById(id: Int): Product? = _productList.find { it.id == id }
//
//    fun getDetailByTransactionId(transactionId: Int): List<DetailTransaction> =
//        _detailTransactionList.filter { it.transaksi_id == transactionId }
//
//    fun createDetailTransaction(transactionId: Int, productId: Int, qty: Int, subtotal: Int): DetailTransaction {
//        val newId = if (_detailTransactionList.isEmpty()) 1 else _detailTransactionList.maxOf { it.id } + 1
//        val detail = DetailTransaction(newId, transactionId, productId, qty, subtotal)
//        _detailTransactionList.add(detail)
//        return detail
//    }
//
//    fun getProductByCategory(category: String): List<Product> =
//        _productList.filter { it.category.equals(category, true) }
//
//    fun searchProduct(keyword: String): List<Product> =
//        _productList.filter { it.name.contains(keyword, true) }
//
//    fun calculateProfit(productId: Int, qty: Int): Int =
//        getProductById(productId)?.let { (it.price - it.costPrice) * qty } ?: 0
//
//    fun addProduct(product: Product) {
//        val newId = if (_productList.isEmpty()) 1 else _productList.maxOf { it.id } + 1
//        _productList.add(product.copy(id = newId))
//    }
//
//    fun deleteProduct(id: Int) {
//        _productList.removeIf { it.id == id }
//    }
//
//    fun deleteTransaction(id: Int) {
//        _transactionList.removeIf { it.id == id }
//        _detailTransactionList.removeIf { it.transaksi_id == id }
//    }
//}
