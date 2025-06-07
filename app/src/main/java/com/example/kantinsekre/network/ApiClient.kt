package com.example.kantinsekre.network

import android.content.Context
import com.example.kantinsekre.util.AuthInterceptor
import com.example.kantinsekre.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object untuk mengelola konfigurasi dan pembuatan instance ApiService
 */
object ApiClient {
    private const val BASE_URL = "https://beuaspb.up.railway.app/"
    private const val TIMEOUT_SECONDS = 30L

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Membuat instance ApiService dengan konfigurasi yang sesuai
     * @param context Context aplikasi untuk mengakses TokenManager
     * @return Instance ApiService yang sudah dikonfigurasi
     */
    fun create(context: Context): ApiService {
        val tokenManager = TokenManager(context)
        val httpClient = createHttpClient(tokenManager)
        
        return createRetrofitInstance(httpClient)
    }

    /**
     * Membuat instance OkHttpClient dengan interceptor yang diperlukan
     * @param tokenManager TokenManager untuk autentikasi
     * @return Instance OkHttpClient yang sudah dikonfigurasi
     */
    private fun createHttpClient(tokenManager: TokenManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Membuat instance Retrofit dengan konfigurasi yang sesuai
     * @param httpClient OkHttpClient yang sudah dikonfigurasi
     * @return Instance ApiService yang sudah dikonfigurasi
     */
    private fun createRetrofitInstance(httpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
