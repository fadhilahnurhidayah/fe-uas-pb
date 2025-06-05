package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class AuthRequest(

	@field:SerializedName("nama")
	val nama: String? = null,
	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("role")
	val role: String? = null
)
