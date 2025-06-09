package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName


data class AuthResponse(
	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: Token? = null
)

data class Token(
	@field:SerializedName("token")
	val token: String? = null
)
