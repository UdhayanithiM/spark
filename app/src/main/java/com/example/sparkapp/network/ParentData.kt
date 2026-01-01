package com.example.sparkapp.network

import com.squareup.moshi.Json

// This models: {"status":"success", "parent_details":{...}}
data class ParentProfileResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "parent_details") val parentDetails: ParentDetails?,
    @field:Json(name = "message") val message: String?
)

// This models the "parent_details" object
data class ParentDetails(
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "father_occ") val fatherOccupation: String?,
    @field:Json(name = "mother_occ") val motherOccupation: String?,
    @field:Json(name = "father_phone") val fatherPhone: String?,
    @field:Json(name = "mother_phone") val motherPhone: String?,
    @field:Json(name = "email") val email: String?
)