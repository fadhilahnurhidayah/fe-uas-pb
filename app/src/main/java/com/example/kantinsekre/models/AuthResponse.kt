package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: Token
)
data class User(
	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("password")
	val password: String,
)
data class Token(

	@field:SerializedName("token")
	val token: String? = null
)
