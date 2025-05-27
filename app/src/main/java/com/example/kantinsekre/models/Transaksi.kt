package com.example.kantinsekre.models

data class Transaksi(
    val id: Int,
    val kasir_id: Int,
    val tanggal: String,
    val total: Int,
    val bayar: Int,
    val kembalian: Int
)

