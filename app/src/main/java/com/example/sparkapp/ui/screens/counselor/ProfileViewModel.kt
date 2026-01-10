package com.example.sparkapp.ui.screens.counselor

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.ApiService
import com.example.sparkapp.network.RetrofitClient
import com.example.sparkapp.ui.screens.login.DataStoreViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    private val dataStore = DataStoreViewModel(application)

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            Log.d("ProfileDebug", "Loading user profile...")

            // 1. Get Email from DataStore
            val userEmail = dataStore.userEmail.first()
            Log.d("ProfileDebug", "Email found in DataStore: $userEmail")

            if (!userEmail.isNullOrEmpty()) {
                uiState = uiState.copy(email = userEmail)
                fetchProfile(userEmail)
            } else {
                Log.e("ProfileDebug", "No email found! User needs to login.")
                uiState = uiState.copy(
                    isLoading = false,
                    snackbarMessage = "User not found. Please Logout and Login again."
                )
            }
        }
    }

    private fun fetchProfile(email: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                Log.d("ProfileDebug", "Fetching API for: $email")
                val response = apiService.getProfile(email)

                Log.d("ProfileDebug", "API Response Code: ${response.code()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    val profileData = response.body()?.data
                    Log.d("ProfileDebug", "API Success. Data: $profileData")

                    uiState = uiState.copy(
                        name = profileData?.name ?: "",
                        email = profileData?.email ?: email,
                        phone = profileData?.phone ?: "",
                        isLoading = false
                    )
                } else {
                    Log.e("ProfileDebug", "API Failed or Status Error: ${response.message()}")
                    uiState = uiState.copy(
                        snackbarMessage = "Failed to load profile",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileDebug", "Exception: ${e.message}")
                e.printStackTrace()
                uiState = uiState.copy(
                    snackbarMessage = "Connection error: ${e.message}",
                    isLoading = false
                )
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
                        snackbarMessage = response.body()?.get("message") ?: "Updated!"
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
            dataStore.clearUserLogin()
            _navigationEvent.emit("logout")
        }
    }

    fun snackbarShown() {
        uiState = uiState.copy(snackbarMessage = null)
    }
}