package com.example.sparkapp.network

import com.squareup.moshi.Json

data class GenericResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "message") val message: String?
)