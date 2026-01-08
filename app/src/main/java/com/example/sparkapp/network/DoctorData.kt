package com.example.sparkapp.network

import com.squareup.moshi.Json

// 1. Referral Response
data class ReferralResponse(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String?,
    @Json(name = "age") val age: String?,
    @Json(name = "gender") val gender: String?,
    @Json(name = "standard") val standard: String?,
    @Json(name = "address") val address: String?,
    @Json(name = "disciplinary") val disciplinary: String?,
    @Json(name = "special_need") val specialNeed: String?,
    @Json(name = "doctor_suggestion") val doctorSuggestion: String?,
    @Json(name = "reason") val reason: String?,
    @Json(name = "behavior") val behavior: String?,
    @Json(name = "academic") val academic: String?,
    @Json(name = "counselor_id") val counselorId: String?
)

// 2. Scoreboard Response
data class ScoreboardResponse(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String?,
    @Json(name = "scenario") val scenario: String?,
    @Json(name = "answer1") val answer1: String?,
    @Json(name = "answer2") val answer2: String?,
    @Json(name = "answer3") val answer3: String?,
    @Json(name = "answer4") val answer4: String?,
    @Json(name = "score") val score: String?
)

// 3. Message Response (Used for Fetching Chat History)
// ⚠️ FIX: Removed 'status' and 'message_id' because get_message.php does not send them.
data class MessageResponse(
    @Json(name = "id") val id: String,
    @Json(name = "sender_id") val senderId: String?,
    @Json(name = "receiver_id") val receiverId: String?,
    @Json(name = "message") val message: String?,
    @Json(name = "referral_id") val referralId: String?,
    @Json(name = "timestamp") val timestamp: String?
)

// 4. Send Message Request
// ⚠️ FIX: Removed 'field:' to ensure keys like "sender_id" are sent correctly.
data class SendMessageRequest(
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "receiver_id") val receiverId: String,
    @Json(name = "message") val message: String,
    @Json(name = "referral_id") val referralId: String
)

// 5. Send Message Response (Used for the result of Sending)
data class SendMessageResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message_id") val messageId: Int?
)

// 6. Counselor Profile
data class CounselorProfile(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "phone") val phone: String?,
    @Json(name = "qualification") val qualification: String?,
    @Json(name = "school") val school: String?
)

// 7. Counselor Detail Responses
data class CounselorDetailResponse(
    @Json(name = "tests") val tests: List<KnowledgeTestResult>,
    @Json(name = "scenarios") val scenarios: List<ScenarioResult>
)

data class KnowledgeTestResult(
    @Json(name = "id") val id: String,
    @Json(name = "score") val score: String?,
    @Json(name = "total") val total: String?,
    @Json(name = "created_at") val date: String?
)

data class ScenarioResult(
    @Json(name = "id") val id: String,
    @Json(name = "scenario") val scenario: String?,
    @Json(name = "answer1") val answer1: String?,
    @Json(name = "answer2") val answer2: String?,
    @Json(name = "created_at") val date: String?
)