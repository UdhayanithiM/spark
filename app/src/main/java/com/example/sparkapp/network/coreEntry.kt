package com.example.sparkapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScoreEntry(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "user_name") val userName: String,
    @field:Json(name = "score") val score: Int,
    @field:Json(name = "total") val total: Int
)