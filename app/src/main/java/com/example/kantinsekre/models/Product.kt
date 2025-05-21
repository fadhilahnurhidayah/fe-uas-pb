package com.example.kantinsekre.models

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val category: String,
    val costPrice: Int,
    var quantity: Int = 0
)
