package com.example.sparkapp.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TestStatusResponse(
    @field:Json(name = "status") val status: String // "completed" or "not_completed"
)