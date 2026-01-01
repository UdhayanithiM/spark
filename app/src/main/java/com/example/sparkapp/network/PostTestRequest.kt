package com.example.sparkapp.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Represents a single question-answer pair
@JsonClass(generateAdapter = true)
data class PostTestAnswer(
    @field:Json(name = "question") val question: String,
    @field:Json(name = "answer") val answer: String
)

// This is the main request body
@JsonClass(generateAdapter = true)
data class PostTestRequest(
    @field:Json(name = "user_id") val userId: Int,
    @field:Json(name = "total_score") val totalScore: Int,
    @field:Json(name = "responses") val responses: List<PostTestAnswer>
)