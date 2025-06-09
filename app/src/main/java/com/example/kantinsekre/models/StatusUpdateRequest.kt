package com.example.kantinsekre.models

import com.google.gson.annotations.SerializedName

data class StatusUpdateRequest(
    @field:SerializedName("status")
    val status: String
)
