package com.example.sparkapp.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Auth ---
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // --- Forgot Password ---
    @POST("forgot_password.php")
    suspend fun resetPassword(@Body data: Map<String, String>): Response<Map<String, String>>
    // --- Parent Search ---
    @POST("parent_search.php")
    suspend fun searchStudent(@Body request: Map<String, String>): Response<ParentSearchResponse>

    @POST("signup.php")
    suspend fun signup(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, String>>

    // --- Profile ---
    @GET("profile.php")
    suspend fun getProfile(@Query("email") email: String): Response<ProfileResponse>

    @POST("profile.php")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<Map<String, String>>

    @POST("parentprofile.php")
    suspend fun getParentProfile(@Body request: Map<String, Int>): Response<ParentProfileResponse>

    // --- Counselor Actions ---
    @POST("refferal.php")
    suspend fun submitReferral(@Body request: ReferralRequest): Response<GenericResponse>

    @POST("score.php")
    suspend fun submitPreTestScore(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, String>>

    // ⚠️ FIX: Returns SendMessageResponse (contains status), not MessageResponse
    @POST("send.php")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>

    @POST("response.php")
    suspend fun submitScenarioResponse(@Body request: ScenarioRequest): Response<GenericResponse>

    @POST("knowledge_response.php")
    suspend fun submitPostTest(@Body request: PostTestRequest): Response<GenericResponse>

    @GET("check_completion.php")
    suspend fun checkTestStatus(
        @Query("test_type") testType: String,
        @Query("user_key") userKey: String
    ): Response<TestStatusResponse>

    @GET("score_display.php")
    suspend fun getScoreHistory(): Response<ScoreHistoryResponse>

    // --- Doctor Actions ---
    @GET("doc_referal.php")
    suspend fun getDoctorReferrals(): Response<List<ReferralResponse>>

    @GET("get_score.php")
    suspend fun getScoreboard(): Response<List<ScoreboardResponse>>

    @POST("save_suggestion.php")
    suspend fun saveSuggestion(@Body data: Map<String, String>): Response<Map<String, String>>

    // --- Chat ---
    // ⚠️ FIX: Returns List<MessageResponse> (no status field inside list items)
    @GET("get_message.php")
    suspend fun getMessages(
        @Query("sender_id") senderId: String?,
        @Query("receiver_id") receiverId: String?,
        @Query("referral_id") referralId: String?
    ): Response<List<MessageResponse>>

    // --- New Counselor Features ---
    @GET("get_counselors.php")
    suspend fun getCounselors(): Response<List<CounselorProfile>>

    @GET("get_my_referrals.php")
    suspend fun getMyReferrals(@Query("counselor_id") id: String): Response<List<ReferralResponse>>

    @GET("get_counselor_details.php")
    suspend fun getCounselorDetails(
        @Query("user_id") userId: String,
        @Query("email") email: String
    ): Response<CounselorDetailResponse>
}