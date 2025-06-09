package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName


data class User(
    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("role")
    val role: String = ""
) {
    // Constructor untuk login (hanya nama dan password)
    constructor(nama: String, password: String) : this(
        nama = nama,
        password = password,
        role = ""
    )
}

fun UserItem.toDisplayUser(): DisplayUser {
    return DisplayUser(
        idUser = this.idUser ?: 0,
        nama = this.nama ?: "",
        role = this.role ?: ""
    )
}

fun CurrentUserData.toDisplayUser(): DisplayUser {
    return DisplayUser(
        idUser = 0,
        nama = this.nama ?: "",
        role = this.role ?: ""
    )
}


fun List<UserItem>.toDisplayUserList(): List<DisplayUser> {
    return this.map { it.toDisplayUser() }
}


data class DisplayUser(
    val idUser: Int,
    val nama: String,
    val role: String
) {
    // Helper untuk membuat User request dari DisplayUser
    fun toUserRequest(password: String): User {
        return User(nama = this.nama, password = password, role = this.role)
    }
}
