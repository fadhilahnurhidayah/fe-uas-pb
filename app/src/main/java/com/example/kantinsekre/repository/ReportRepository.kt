package com.example.kantinsekre.repository

import android.content.Context
import com.example.kantinsekre.models.DailyReportResponse
import com.example.kantinsekre.models.MonthlyReportResponse
import com.example.kantinsekre.network.ApiClient
import com.example.kantinsekre.network.ApiService
import retrofit2.Response

class ReportRepository(private val context: Context) {

    private val apiService: ApiService by lazy {
        ApiClient.create(context)
    }

    suspend fun getLaporanHarian(): Response<DailyReportResponse> {
        return try {
            apiService.getLaporanHarian()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getLaporanHarianByDate(tanggal: String): Response<DailyReportResponse> {
        return try {
            apiService.getLaporanHarianByDate(tanggal)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getLaporanBulanan(): Response<MonthlyReportResponse> {
        return try {
            apiService.getLaporanBulanan()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getLaporanBulananByMonth(bulan: String): Response<MonthlyReportResponse> {
        return try {
            apiService.getLaporanBulananByMonth(bulan)
        } catch (e: Exception) {
            throw e
        }
    }
}
