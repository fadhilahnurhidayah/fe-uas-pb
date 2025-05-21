package com.example.kantinsekre.models

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val role: String
)