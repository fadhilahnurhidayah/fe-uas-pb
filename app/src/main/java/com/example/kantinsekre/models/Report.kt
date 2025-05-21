package com.example.kantinsekre.models

data class Report(
    val id: Int,
    val userId: Int,
    val month: Int,
    val year: Int,
    val income: Int,
    val transactionCount: Int
)