package com.example.kantinsekre.network

import com.example.kantinsekre.models.AuthResponse
import com.example.kantinsekre.models.CreateMenu
import com.example.kantinsekre.models.CurrentUser
import com.example.kantinsekre.models.DailyReportResponse
import com.example.kantinsekre.models.MonthlyReportResponse
import com.example.kantinsekre.models.ProductResponse
import com.example.kantinsekre.models.StatusUpdateRequest
import com.example.kantinsekre.models.TransaksiRequest
import com.example.kantinsekre.models.TransaksiResponse
import com.example.kantinsekre.models.User
import com.example.kantinsekre.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/user/login")
    suspend fun login(@Body request: User): Response<AuthResponse>

    @GET("/user")
    suspend fun getAllUsers(): Response<UserResponse>

    @GET("/user/current")
    suspend fun getCurrentUser(): Response<CurrentUser>

    @POST("user/register")
    suspend fun addUser(@Body user: User): Response<Unit>

    @PUT("user/update-user/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<Unit>

    @DELETE("user/delete-user/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    @GET("/menu")
    suspend fun getAllMenu(): Response<ProductResponse>

    @POST("menu/")
    suspend fun addMenu(@Body request: CreateMenu): Response<Unit>

    @DELETE("menu/{id}")
    suspend fun deleteMenu(@Path("id") id: String): Response<Unit>

    @GET("/transaksi")
    suspend fun getAllTransaksi(): Response<TransaksiResponse>

    @POST("transaksi/")
    suspend fun addTransaksi(@Body request: TransaksiRequest): Response<Unit>

    @PUT("transaksi/{id}")
    suspend fun updateTransaksi(@Path("id") id: String, @Body request: StatusUpdateRequest): Response<Unit>

    @DELETE("transaksi/{id}")
    suspend fun deleteTransaksi(@Path("id") id: String): Response<Unit>

    @GET("/laporan-harian")
    suspend fun getLaporanHarian(): Response<DailyReportResponse>

    @GET("/laporan-bulanan")
    suspend fun getLaporanBulanan(): Response<MonthlyReportResponse>

    @GET("/laporan-harian/{tanggal}")
    suspend fun getLaporanHarianByDate(@Path("tanggal") tanggal: String): Response<DailyReportResponse>

    @GET("/laporan-bulanan/{bulan}")
    suspend fun getLaporanBulananByMonth(@Path("bulan") bulan: String): Response<MonthlyReportResponse>

}
