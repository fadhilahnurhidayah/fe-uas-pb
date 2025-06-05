package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class LaporanHarianResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: List<LaporanHarian?>? = null
)

data class LaporanHarian(

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("total_transaksi")
	val totalTransaksi: Int? = null,

	@field:SerializedName("total_pendapatan")
	val totalPendapatan: String? = null,

	@field:SerializedName("transaksi_selesai")
	val transaksiSelesai: String? = null,

	@field:SerializedName("transaksi_dibatalkan")
	val transaksiDibatalkan: String? = null
)
