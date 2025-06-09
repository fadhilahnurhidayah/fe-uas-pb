package com.example.kantinsekre.models
import com.google.gson.annotations.SerializedName

data class DailyReportResponse(
	@field:SerializedName("success")
	val success: Boolean,
	@field:SerializedName("data")
	val data: List<DailyReport>,
	@field:SerializedName("message")
	val message: String
)

data class DailyReport(
	@field:SerializedName("tanggal")
	val tanggal: String,
	@field:SerializedName("total_transaksi")
	val total_transaksi: Int,
	@field:SerializedName("total_pendapatan")
	val total_pendapatan: String,
	@field:SerializedName("transaksi_selesai")
	val transaksi_selesai: String,
	@field:SerializedName("transaksi_dibatalkan")
	val transaksi_dibatalkan: String
)
