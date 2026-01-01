package com.example.sparkapp.network

import com.squareup.moshi.Json

// This class matches the JSON from your profile.php
// {"status":"success","data":{"id":"12","name":"lalith","email":"lalith@gmail.com","phone":"9361911043"}}

data class ProfileResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "data") val data: ProfileData?
)

data class ProfileData(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phone") val phone: String
)