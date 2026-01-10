package com.example.sparkapp.ui.screens.parent

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.ApiService
import com.example.sparkapp.network.ParentDetails
import com.example.sparkapp.network.ReferralResponse
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.launch

// State for the Dashboard
data class ParentDashboardState(
    val uniqueIdInput: String = "",
    val studentData: ReferralResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// State for the Profile Screen (UPDATED with error field)
data class ParentProfileState(
    val details: ParentDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ParentViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient.instance

    // --- Dashboard State ---
    var dashboardState by mutableStateOf(ParentDashboardState())
        private set

    // --- Profile State ---
    var profileState by mutableStateOf(ParentProfileState())
        private set

    // 1. Dashboard Functions
    fun onUniqueIdChange(newId: String) {
        dashboardState = dashboardState.copy(uniqueIdInput = newId)
    }

    fun searchStudent() {
        if (dashboardState.uniqueIdInput.isBlank()) {
            dashboardState = dashboardState.copy(error = "Please enter a Student ID")
            return
        }

        viewModelScope.launch {
            dashboardState = dashboardState.copy(isLoading = true, error = null, studentData = null)
            try {
                val request = mapOf("unique_id" to dashboardState.uniqueIdInput.trim())
                val response = apiService.searchStudent(request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    dashboardState = dashboardState.copy(
                        isLoading = false,
                        studentData = response.body()?.data
                    )
                } else {
                    dashboardState = dashboardState.copy(
                        isLoading = false,
                        error = response.body()?.message ?: "Student not found"
                    )
                }
            } catch (e: Exception) {
                dashboardState = dashboardState.copy(isLoading = false, error = "Connection error: ${e.message}")
            }
        }
    }

    // 2. Profile Functions
    fun fetchParentProfile() {
        viewModelScope.launch {
            // Reset state to loading
            profileState = profileState.copy(isLoading = true, error = null)

            // Hardcoded ID for testing (replace with dynamic ID in production)
            val hardcodedParentId = 10

            try {
                val response = apiService.getParentProfile(mapOf("id" to hardcodedParentId))
                if (response.isSuccessful && response.body()?.status == "success") {
                    profileState = profileState.copy(
                        isLoading = false,
                        details = response.body()?.parentDetails
                    )
                } else {
                    profileState = profileState.copy(
                        isLoading = false,
                        error = response.body()?.message ?: "Failed to load profile"
                    )
                }
            } catch (e: Exception) {
                profileState = profileState.copy(
                    isLoading = false,
                    error = "Connection error: ${e.message}"
                )
            }
        }
    }

    fun clearProfileError() {
        profileState = profileState.copy(error = null)
    }
}