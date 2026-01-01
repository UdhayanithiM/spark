package com.example.sparkapp.ui.screens.counselor

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.ApiService
import com.example.sparkapp.network.RetrofitClient
import com.example.sparkapp.ui.screens.login.DataStoreViewModel // Now this import works
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Data class to hold the profile state
data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val snackbarMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    private val apiService: ApiService = RetrofitClient.instance
    private val dataStore = DataStoreViewModel(application) // This now compiles

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            val userEmail = dataStore.userEmail.first() // This now compiles
            if (userEmail != null) {
                uiState = uiState.copy(email = userEmail)
                fetchProfile(userEmail)
            } else {
                uiState = uiState.copy(isLoading = false, snackbarMessage = "User email not found.")
            }
        }
    }

    private fun fetchProfile(email: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val response = apiService.getProfile(email)
                if (response.isSuccessful && response.body()?.status == "success") {
                    val profileData = response.body()?.data
                    uiState = uiState.copy(
                        // FIX: Safely cast 'Any?' to String
                        name = profileData?.name as? String ?: "",
                        phone = profileData?.phone as? String ?: "",
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(snackbarMessage = "Failed to load profile", isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(snackbarMessage = "Error: ${e.message}", isLoading = false)
            }
        }
    }

    fun onNameChange(newName: String) {
        uiState = uiState.copy(name = newName)
    }

    fun onPhoneChange(newPhone: String) {
        uiState = uiState.copy(phone = newPhone)
    }

    fun onEditToggle() {
        if (uiState.isEditing) {
            updateProfile()
        } else {
            uiState = uiState.copy(isEditing = true)
        }
    }

    private fun updateProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val requestBody = mapOf(
                    "email" to uiState.email,
                    "name" to uiState.name,
                    "phone" to uiState.phone
                )
                val response = apiService.updateProfile(requestBody)
                if (response.isSuccessful && response.body()?.get("status") == "success") {
                    uiState = uiState.copy(
                        isLoading = false,
                        isEditing = false,
                        snackbarMessage = response.body()?.get("message") ?: "Profile Updated!"
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, snackbarMessage = "Update failed")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, snackbarMessage = "Error: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearUserLogin() // This now compiles
            _navigationEvent.emit("logout")
        }
    }

    fun snackbarShown() {
        uiState = uiState.copy(snackbarMessage = null)
    }
}