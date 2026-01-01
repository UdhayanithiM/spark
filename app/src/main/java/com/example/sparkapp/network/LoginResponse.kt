package com.example.sparkapp.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "role") val role: String?,
    @field:Json(name = "user_id") val userId: Int?
)