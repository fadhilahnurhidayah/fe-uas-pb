package com.example.kantinsekre.repository

import android.content.Context
import com.example.kantinsekre.models.CreateMenu
import com.example.kantinsekre.models.ProductResponse
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.network.ApiService
import retrofit2.Response

class ProductRepository(private val context: Context) {

    private val apiService: ApiService by lazy {
        ApiClient.create(context)
    }

    suspend fun getAllMenu(): Response<ProductResponse> {
        return try {
            apiService.getAllMenu()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addMenu(menu: CreateMenu): Response<Unit> {
        return try {
            apiService.addMenu(menu)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteMenu(id: String): Response<Unit> {
        return try {
            apiService.deleteMenu(id)
        } catch (e: Exception) {
            throw e
        }
    }
}
