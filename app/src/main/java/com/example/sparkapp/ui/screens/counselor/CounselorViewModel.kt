package com.example.sparkapp.ui.screens.counselor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CounselorViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    var myReferrals by mutableStateOf<List<ReferralResponse>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set

    // [FIX] Live Student Data
    var activeStudent by mutableStateOf<ReferralResponse?>(null)
        private set

    var currentMessages by mutableStateOf<List<MessageResponse>>(emptyList())
        private set

    private var chatPollingJob: Job? = null
    private var studentPollingJob: Job? = null

    // Fetch List
    fun fetchMyReferrals(counselorId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getMyReferrals(counselorId)
                if (response.isSuccessful) myReferrals = response.body() ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // [FIX] Live Sync for Suggestions
    fun startLiveStudentSync(counselorId: String, studentId: String) {
        studentPollingJob?.cancel()
        studentPollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val response = apiService.getMyReferrals(counselorId)
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()
                        val updated = list.find { it.id == studentId }
                        if (updated != null) activeStudent = updated
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(4000) // Check for doctor suggestion updates every 4s
            }
        }
    }

    fun stopLiveStudentSync() {
        studentPollingJob?.cancel()
        studentPollingJob = null
        activeStudent = null
    }

    // [FIX] Chat Polling (Same as Doctor)


    fun stopChatPolling() {
        chatPollingJob?.cancel()
        chatPollingJob = null
        currentMessages = emptyList()
    }

    // [FIX] Send Message
    fun startChatPolling(referralId: String) {
        println("🟢 Starting chat polling for referral: $referralId")

        chatPollingJob?.cancel()
        chatPollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    println("🔵 Polling messages for referral: $referralId")

                    val response = apiService.getMessages(null, null, referralId)

                    println("🔵 Response code: ${response.code()}")
                    println("🔵 Response body: ${response.body()}")

                    if (response.isSuccessful) {
                        val newMsgs = response.body() ?: emptyList()
                        println("🔵 Received ${newMsgs.size} messages")

                        if (newMsgs != currentMessages) {
                            currentMessages = newMsgs
                            println("✅ Messages updated!")
                        }
                    } else {
                        println("🔴 Error response: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    println("🔴 Exception: ${e.message}")
                    e.printStackTrace()
                }
                delay(2000)
            }
        }
    }

    fun sendMessage(
        counselorId: String,
        referralId: String,
        text: String
    ) {
        if (text.isBlank()) return

        println("🟢 Sending message:")
        println("   Sender: $counselorId")
        println("   Referral: $referralId")
        println("   Message: $text")

        viewModelScope.launch {
            try {
                val request = SendMessageRequest(
                    senderId = counselorId,
                    receiverId = "11",  // loki
                    message = text,
                    referralId = referralId
                )

                println("🔵 Request: $request")

                val response = apiService.sendMessage(request)

                println("🔵 Send response: ${response.code()}")
                println("🔵 Response body: ${response.body()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    println("✅ Message sent successfully!")
                } else {
                    println("🔴 Send failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("🔴 Send error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}