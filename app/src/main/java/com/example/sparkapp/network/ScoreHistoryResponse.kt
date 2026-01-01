package com.example.sparkapp.network

import com.example.sparkapp.data.ScoreEntry
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScoreHistoryResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "data") val data: List<ScoreEntry>?
)