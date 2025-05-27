package com.example.kantinsekre.repository

import com.example.kantinsekre.models.User
import com.example.kantinsekre.network.ApiClient

class UserRepository {
    private val api = ApiClient.instance

    suspend fun getAllUsers() = api.getAllUsers()
    suspend fun getUserById(id: String) = api.getUserById(id)
    suspend fun addUser(user: User) = api.addUser(user)
    suspend fun updateUser(id: String, user: User) = api.updateUser(id, user)
    suspend fun deleteUser(id: String) = api.deleteUser(id)
}
