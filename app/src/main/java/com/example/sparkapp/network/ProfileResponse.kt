package com.example.sparkapp.network

import com.squareup.moshi.Json

data class ProfileResponse(
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: ProfileData?
)

data class ProfileData(
    @Json(name = "id") val id: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "phone") val phone: String?
)