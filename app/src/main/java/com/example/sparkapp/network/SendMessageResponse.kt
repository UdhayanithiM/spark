package com.example.sparkapp.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMessageResponse(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "message_id") val messageId: Int?,
    @field:Json(name = "sender_id") val senderId: Int?,
    @field:Json(name = "receiver_id") val receiverId: Int?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "timestamp") val timestamp: String?
)