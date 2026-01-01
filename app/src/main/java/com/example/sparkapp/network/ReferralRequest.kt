package com.example.sparkapp.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralRequest(
    @field:Json(name = "counselor_id") val counselorId: String, // <-- NEW
    @field:Json(name = "name") val name: String,
    @field:Json(name = "age") val age: Int,
    @field:Json(name = "standard") val standard: String,
    @field:Json(name = "address") val address: String,
    @field:Json(name = "reason") val reason: String,
    @field:Json(name = "behavior") val behavior: String,
    @field:Json(name = "academic") val academic: String,
    @field:Json(name = "disciplinary") val disciplinary: String,
    @field:Json(name = "special_need") val specialNeed: String
)
