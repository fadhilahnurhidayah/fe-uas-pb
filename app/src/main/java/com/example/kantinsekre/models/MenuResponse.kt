package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class MenuResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("data")
	val data: List<Menu>
)

data class Menu(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("jenis")
	val jenis: String,

	@field:SerializedName("harga")
	val harga: String
)
