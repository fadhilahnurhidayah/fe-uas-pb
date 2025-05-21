package com.example.kantinsekre.models

data class Transaction(
    val id: Int,
    val userId: Int,
    val date: String,
    val total: Int,
    val tunai: Int,
    val kembalian: Int
)
