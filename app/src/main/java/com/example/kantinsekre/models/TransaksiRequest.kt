package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName


data class TransaksiRequest(

	@field:SerializedName("nama_pembeli")
	val namaPembeli: String,

	@field:SerializedName("nama_user")
	val namaUser: String,

	@field:SerializedName("tanggal")
	val tanggal: String,

	@field:SerializedName("items")
	val items: List<ItemRequest?>

)
	data class ItemRequest(

	@field:SerializedName("nama_menu")
	val namaMenu: String? = null,

	@field:SerializedName("jumlah_menu")
	val jumlahMenu: String? = null,)
