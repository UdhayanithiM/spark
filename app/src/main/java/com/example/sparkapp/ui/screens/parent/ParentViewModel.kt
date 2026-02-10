package com.example.sparkapp.ui.screens.parent

import android.app.Application
import android.content.Context
import android.util.Log
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
    val error: String? = null,
    val isStudentFixed: Boolean = false
)

// State for the Profile Screen
data class ParentProfileState(
    val details: ParentDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ParentViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient.instance

    // 1. Prefs for local settings (Student Persistence)
    private val prefs = application.getSharedPreferences("SparkParentPrefs", Context.MODE_PRIVATE)

    // 2. Prefs for Authentication (Where Login stores 'user_id')
    private val authPrefs = application.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)

    var dashboardState by mutableStateOf(ParentDashboardState())
        private set

    var profileState by mutableStateOf(ParentProfileState())
        private set

    init {
        checkSavedStudent()
    }

    // --- Dashboard Logic ---

    private fun checkSavedStudent() {
        val savedId = prefs.getString("saved_student_id", null)
        if (!savedId.isNullOrEmpty()) {
            dashboardState = dashboardState.copy(uniqueIdInput = savedId)
            searchStudent(isAutoLoad = true)
        }
    }

    fun onUniqueIdChange(newId: String) {
        dashboardState = dashboardState.copy(uniqueIdInput = newId)
    }

    fun searchStudent(isAutoLoad: Boolean = false) {
        val idToSearch = dashboardState.uniqueIdInput.trim()

        if (idToSearch.isBlank()) {
            dashboardState = dashboardState.copy(error = "Please enter a Student ID")
            return
        }

        viewModelScope.launch {
            dashboardState = dashboardState.copy(isLoading = true, error = null)
            try {
                val request = mapOf("unique_id" to idToSearch)
                val response = apiService.searchStudent(request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    if (!isAutoLoad) {
                        prefs.edit().putString("saved_student_id", idToSearch).apply()
                    }

                    dashboardState = dashboardState.copy(
                        isLoading = false,
                        studentData = response.body()?.data,
                        isStudentFixed = true
                    )
                } else {
                    dashboardState = dashboardState.copy(
                        isLoading = false,
                        error = response.body()?.message ?: "Student not found",
                        isStudentFixed = false
                    )
                }
            } catch (e: Exception) {
                dashboardState = dashboardState.copy(
                    isLoading = false,
                    error = "Connection error: ${e.message}",
                    isStudentFixed = false
                )
            }
        }
    }

    fun logout() {
        prefs.edit().remove("saved_student_id").apply()
        dashboardState = ParentDashboardState()
    }

    // --- Profile Logic ---

    fun fetchParentProfile() {
        viewModelScope.launch {
            profileState = profileState.copy(isLoading = true, error = null)

            // Safely get User ID
            var userId = -1
            if (authPrefs.contains("user_id")) {
                try {
                    userId = authPrefs.getInt("user_id", -1)
                } catch (e: Exception) {
                    userId = authPrefs.getString("user_id", "-1")?.toIntOrNull() ?: -1
                }
            }

            if (userId == -1) {
                profileState = profileState.copy(
                    isLoading = false,
                    error = "Session invalid (ID not found). Please logout and login."
                )
                return@launch
            }

            try {
                // Ensure this points to the correct endpoint in ApiService
                val response = apiService.getParentProfile(mapOf("id" to userId))

                if (response.isSuccessful && response.body()?.status == "success") {
                    profileState = profileState.copy(
                        isLoading = false,
                        details = response.body()?.parentDetails
                    )
                } else {
                    val msg = response.body()?.message ?: "Failed to load profile"
                    profileState = profileState.copy(
                        isLoading = false,
                        error = msg
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