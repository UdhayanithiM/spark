package com.example.sparkapp.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralRequest(
    @Json(name = "counselor_id") val counselorId: String,
    @Json(name = "unique_id") val uniqueId: String, // <-- NEW
    @Json(name = "name") val name: String,
    @Json(name = "age") val age: Int,
    @Json(name = "standard") val standard: String,
    @Json(name = "address") val address: String,
    @Json(name = "reason") val reason: String,
    @Json(name = "behavior") val behavior: String,
    @Json(name = "academic") val academic: String,
    @Json(name = "disciplinary") val disciplinary: String,
    @Json(name = "special_need") val specialNeed: String
)