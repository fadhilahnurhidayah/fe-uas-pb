package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class MenuRequest(

	@field:SerializedName("data")
	val data: Any? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class createmenu(
	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("jenis")
	val jenis: String,

	@field:SerializedName("harga")
	val harga: String
)