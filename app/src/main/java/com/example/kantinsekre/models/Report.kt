package com.example.kantinsekre.models

data class Report(
    val id: Int,
    val tanggal: Int,
    val userId: Int,
    val bulan: Int,
    val tahun: Int,
    val total_keuntungan: Int,
    val total_transaksi: Int
)