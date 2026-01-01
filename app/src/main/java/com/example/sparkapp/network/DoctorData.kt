package com.example.sparkapp.network

import com.squareup.moshi.Json

// 1. Referral Response
data class ReferralResponse(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "age") val age: String?,
    @field:Json(name = "gender") val gender: String?,
    @field:Json(name = "standard") val standard: String?,
    @field:Json(name = "address") val address: String?,
    @field:Json(name = "disciplinary") val disciplinary: String?,
    @field:Json(name = "special_need") val specialNeed: String?,
    @field:Json(name = "doctor_suggestion") val doctorSuggestion: String?,
    @field:Json(name = "reason") val reason: String?,
    @field:Json(name = "behavior") val behavior: String?,
    @field:Json(name = "academic") val academic: String?
)

// 2. Scoreboard Response
data class ScoreboardResponse(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "username") val username: String?,
    @field:Json(name = "scenario") val scenario: String?,
    @field:Json(name = "answer1") val answer1: String?,
    @field:Json(name = "answer2") val answer2: String?,
    @field:Json(name = "answer3") val answer3: String?,
    @field:Json(name = "answer4") val answer4: String?,
    @field:Json(name = "score") val score: String?
)

// 3. Message Response
data class MessageResponse(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "sender_id") val senderId: String?,
    @field:Json(name = "message") val message: String?
)

// 4. Send Message Request
data class SendMessageRequest(
    @field:Json(name = "sender_id") val senderId: String,
    @field:Json(name = "receiver_id") val receiverId: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "referral_id") val referralId: String
)

// 5. Counselor Profile
data class CounselorProfile(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "phone") val phone: String?,
    @field:Json(name = "qualification") val qualification: String?,
    @field:Json(name = "school") val school: String?
)

// 6. Counselor Detail Responses
data class CounselorDetailResponse(
    @field:Json(name = "tests") val tests: List<KnowledgeTestResult>,
    @field:Json(name = "scenarios") val scenarios: List<ScenarioResult>
)

// --- CRITICAL FIX HERE ---
data class KnowledgeTestResult(
    @field:Json(name = "id") val id: String,
    // The scores table sends "score" and "total"
    @field:Json(name = "score") val score: String?,
    @field:Json(name = "total") val total: String?,
    // Scenarios use 'created_at', but scores might not have it. Make it nullable.
    @field:Json(name = "created_at") val date: String?
)

data class ScenarioResult(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "scenario") val scenario: String?,
    @field:Json(name = "answer1") val answer1: String?,
    @field:Json(name = "answer2") val answer2: String?,
    @field:Json(name = "created_at") val date: String?
)