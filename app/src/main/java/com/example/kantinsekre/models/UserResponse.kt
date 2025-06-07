package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

/**
 * Response model untuk data user
 * @property success Status keberhasilan request
 * @property message Pesan dari server
 * @property data List data user
 */
data class UserResponse(
	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: List<UserItem>
)

/**
 * Model untuk data user individual
 * @property idUser ID unik user
 * @property nama Nama user
 * @property passwordHash Hash password user
 * @property role Role/peran user dalam sistem
 */
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
