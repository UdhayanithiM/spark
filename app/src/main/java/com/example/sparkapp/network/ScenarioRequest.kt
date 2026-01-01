package com.example.sparkapp.network

import com.squareup.moshi.Json

data class ScenarioRequest(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "scenario") val scenario: String,
    @field:Json(name = "responses") val responses: List<String>
)