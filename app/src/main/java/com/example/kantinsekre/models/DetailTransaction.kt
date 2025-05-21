package com.example.kantinsekre.models

data class DetailTransaction(
    val id: Int,
    val transaksi_id: Int,
    val menu_id: Int,
    val qty: Int,
    val subtotal: Int
)