package com.example.kantinsekre.repository

import android.content.Context
import com.example.kantinsekre.models.AuthResponse
import com.example.kantinsekre.models.CurrentUser
import com.example.kantinsekre.models.User
import com.example.kantinsekre.models.UserResponse
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.network.ApiService
import retrofit2.Response

class AuthRepository(private val context: Context) {

    private val apiService: ApiService by lazy {
        ApiClient.create(context)
    }

    suspend fun login(user: User): Response<AuthResponse> {
        return try {
            apiService.login(user)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getAllUsers(): Response<UserResponse> {
        return try {
            apiService.getAllUsers()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getUserById(id: String): Response<UserResponse> {
        return try {
            apiService.getUserById(id)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getCurrentUser(): Response<CurrentUser> {
        return try {
            apiService.getCurrentUser()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addUser(user: User): Response<Unit> {
        return try {
             apiService.addUser(user)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUser(id: String, user: User): Response<Unit> {
        return try {
            apiService.updateUser(id, user)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteUser(id: String): Response<Unit> {
        return try {
            apiService.deleteUser(id)
        } catch (e: Exception) {
            throw e
        }
    }
}
