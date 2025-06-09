package com.example.kantinsekre.repository

import android.content.Context
import com.example.kantinsekre.models.StatusUpdateRequest
import com.example.kantinsekre.models.TransaksiRequest
import com.example.kantinsekre.models.TransaksiResponse
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.network.ApiService
import retrofit2.Response

class TransactionRepository(private val context: Context) {

    private val apiService: ApiService by lazy {
        ApiClient.create(context)
    }

    suspend fun getAllTransaksi(): Response<TransaksiResponse> {
        return try {
            apiService.getAllTransaksi()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addTransaksi(request: TransaksiRequest): Response<Unit> {
        return try {
            apiService.addTransaksi(request)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateTransaksi(id: String, statusRequest: StatusUpdateRequest): Response<Unit> {
        return try {
            apiService.updateTransaksi(id, statusRequest)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteTransaksi(id: String): Response<Unit> {
        return try {
            apiService.deleteTransaksi(id)
        } catch (e: Exception) {
            throw e
        }
    }
}
