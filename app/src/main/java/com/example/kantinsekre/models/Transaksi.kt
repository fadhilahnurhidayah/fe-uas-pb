package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class TransaksiResponse(

	@field:SerializedName("data")
	val data: List<Transaksi?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Transaksi(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("nama_pembeli")
	val namaPembeli: String? = null,

	@field:SerializedName("total_harga")
	val totalHarga: String? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("tanggal")
	val tanggal: String,

	@field:SerializedName("status")
	val status: String,

)

data class DetailTransaction(
	val subtotal: Int,

	@field:SerializedName("nama_pembeli")
	val namaPembeli: String,

	@field:SerializedName("nama_user")
	val namaUser: String,

	@field:SerializedName("tanggal")
	val tanggal: String,

	@field:SerializedName("items")
	val items: List<ItemsItem?>

)
	data class ItemsItem(

	@field:SerializedName("nama_menu")
	val namaMenu: String? = null,

	@field:SerializedName("jumlah_menu")
	val jumlahMenu: String? = null,)
