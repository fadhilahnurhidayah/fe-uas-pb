package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class CreateMenu(
	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("jenis")
	val jenis: String,

	@field:SerializedName("harga")
	val harga: String
)