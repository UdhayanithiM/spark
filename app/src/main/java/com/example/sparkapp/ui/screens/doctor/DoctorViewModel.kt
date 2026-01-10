package com.example.sparkapp.ui.screens.doctor

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

// ------------------------------------
// UI STATE
// ------------------------------------
data class DoctorUiState(
    val referrals: List<ReferralResponse> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)

data class ScoreboardUiState(
    val scores: List<ScoreboardResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DoctorViewModel : ViewModel() {

    // ------------------------------------
    // CORE DASHBOARD STATE
    // ------------------------------------
    var uiState by mutableStateOf(DoctorUiState())
        private set

    var scoreboardUiState by mutableStateOf(ScoreboardUiState())
        private set

    // ------------------------------------
    // CHAT STATE
    // ------------------------------------
    var currentStudentMessages by mutableStateOf<List<MessageResponse>>(emptyList())
        private set

    // ------------------------------------
    // COUNSELOR STATE  ✅ (ADDED)
    // ------------------------------------
    var counselorList by mutableStateOf<List<CounselorProfile>>(emptyList())
        private set

    var selectedCounselorDetails by mutableStateOf<CounselorDetailResponse?>(null)
        private set

    // ------------------------------------
    // INTERNALS
    // ------------------------------------
    private val apiService: ApiService = RetrofitClient.instance
    private var chatPollingJob: Job? = null

    init {
        fetchReferrals()
    }

    // ------------------------------------
    // REFERRALS
    // ------------------------------------
    fun fetchReferrals() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, hasError = false)
            try {
                val response = apiService.getDoctorReferrals()
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        referrals = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, hasError = true)
                }
            } catch (_: Exception) {
                uiState = uiState.copy(isLoading = false, hasError = true)
            }
        }
    }

    // ------------------------------------
    // CHAT POLLING (REFERRAL-BASED)
    // ------------------------------------
    fun startChatPollingForStudent(referralId: String) {
        chatPollingJob?.cancel()
        chatPollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val response = apiService.getMessages(
                        senderId = null,
                        receiverId = null,
                        referralId = referralId
                    )
                    if (response.isSuccessful) {
                        currentStudentMessages = response.body() ?: emptyList()
                    }
                } catch (_: Exception) {
                }
                delay(2000)
            }
        }
    }

    fun stopChatPolling() {
        chatPollingJob?.cancel()
        chatPollingJob = null
        currentStudentMessages = emptyList()
    }

    // ------------------------------------
    // SEND MESSAGE
    // ------------------------------------
    fun sendStudentMessage(
        referralId: String,
        text: String,
        doctorId: String,    // "11" for loki
        counselorId: String  // "12" for lalith
    ) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val request = SendMessageRequest(
                    senderId = doctorId,         // "11"
                    receiverId = counselorId,    // "12"
                    message = text,
                    referralId = referralId
                )
                apiService.sendMessage(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ------------------------------------
    // SUBMIT DOCTOR SUGGESTION
    // ------------------------------------
    fun submitSuggestion(
        referralId: String,
        suggestion: String,
        precautions: String, // <-- Added parameter
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val body = mapOf(
                    "referral_id" to referralId,
                    "doctor_suggestion" to suggestion,
                    "precautions" to precautions // <-- Added field
                )
                val response = apiService.saveSuggestion(body)
                if (response.isSuccessful) {
                    fetchReferrals() // Refresh list to show saved data
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }

    // ------------------------------------
    // COUNSELORS LIST  ✅
    // ------------------------------------
    fun fetchCounselors() {
        viewModelScope.launch {
            try {
                val response = apiService.getCounselors()
                if (response.isSuccessful) {
                    counselorList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ------------------------------------
    // COUNSELOR DETAILS  ✅
    // ------------------------------------
    fun fetchCounselorDetails(userId: String, email: String) {
        viewModelScope.launch {
            selectedCounselorDetails = null
            try {
                val response = apiService.getCounselorDetails(userId, email)
                if (response.isSuccessful) {
                    selectedCounselorDetails = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        chatPollingJob?.cancel()
        super.onCleared()
    }
}
