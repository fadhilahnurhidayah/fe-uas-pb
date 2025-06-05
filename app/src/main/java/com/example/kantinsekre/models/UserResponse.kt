package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: List<UserItem>
)

data class UserItem(

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("password_hash")
	val passwordHash: String? = null,

	@field:SerializedName("role")
	val role: String? = null
)
