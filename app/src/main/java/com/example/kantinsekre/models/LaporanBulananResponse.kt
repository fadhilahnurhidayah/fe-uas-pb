package com.example.kantinsekre.models
import com.google.gson.annotations.SerializedName

data class MonthlyReportResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: List<MonthlyReport?>? = null
)

data class MonthlyReport(

	@field:SerializedName("bulan")
	val bulan: String? = null,

	@field:SerializedName("total_transaksi")
	val totalTransaksi: Int? = null,

	@field:SerializedName("total_pendapatan")
	val totalPendapatan: Double? = null,

	@field:SerializedName("transaksi_selesai")
	val transaksiSelesai: String? = null,

	@field:SerializedName("transaksi_dibatalkan")
	val transaksiDibatalkan: String? = null
)
