package com.example.sparkapp.ui.screens.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.LoginRequest
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

// This data class holds all the state for our UI
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val selectedRole: String = "Doctor", // Default role
    val isLoading: Boolean = false,
    val roleOptions: List<String> = listOf("Doctor", "Counselor")
)

// This sealed class represents one-time "events" to send to the UI
sealed class LoginUiEvent {
    // --- FIX 1: Add the userId parameter ---
    data class LoginSuccess(val role: String, val userId: Int) : LoginUiEvent()
    data class ShowError(val message: String) : LoginUiEvent()
}

class LoginViewModel : ViewModel() {

    // This holds the UI state. Compose will watch this for changes.
    var uiState by mutableStateOf(LoginUiState())
        private set

    // This is for sending one-time events (like navigation or errors) to the UI
    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // --- Event Handlers (Called by the UI) ---

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun onRoleChanged(role: String) {
        uiState = uiState.copy(selectedRole = role)
    }

    // --- This is your 'login()' function ---
    fun onLoginClicked(context: Context) {
        if (uiState.isLoading) return

        // 1. Set isLoading = true
        uiState = uiState.copy(isLoading = true)

        // Launch a coroutine for the network call
        viewModelScope.launch {
            try {
                // 2. Create the request
                val request = LoginRequest(
                    email = uiState.email.trim(),
                    password = uiState.password.trim(),
                    role = uiState.selectedRole
                )

                // 3. Make the API call (this is your 'http.post')
                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // 4. Check for 'status == "success"'
                    if (loginResponse.status == "success" && loginResponse.role != null) {

                        // --- FIX 2: Check if userId is null, which is an error state ---
                        val userId = loginResponse.userId
                        if (userId == null) {
                            _uiEvent.emit(LoginUiEvent.ShowError("Login success but no user ID returned."))
                            return@launch
                        }

                        // 5. Save to SharedPreferences
                        saveUserData(
                            context = context,
                            username = uiState.email.trim(),
                            userId = userId.toString() // Save as string per original logic
                        )

                        // 6. Send success event to UI, now with the userId
                        _uiEvent.emit(LoginUiEvent.LoginSuccess(loginResponse.role, userId))

                    } else {
                        // Show error from server (e.g., "Invalid password")
                        _uiEvent.emit(LoginUiEvent.ShowError(loginResponse.message))
                    }
                } else {
                    // Show server error (e.g., "500 Internal Server Error")
                    _uiEvent.emit(LoginUiEvent.ShowError("Server error: ${response.code()}"))
                }

            } catch (e: IOException) {
                // Show network error (e.g., "Failed to connect")
                Log.e("LoginViewModel", "Network error: ${e.message}")
                _uiEvent.emit(LoginUiEvent.ShowError("⚠️ Error connecting to server: ${e.message}"))
            } catch (e: Exception) {
                // Show any other error
                Log.e("LoginViewModel", "Unknown error: ${e.message}")
                _uiEvent.emit(LoginUiEvent.ShowError("An unknown error occurred."))
            } finally {
                // 7. Set isLoading = false
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    // --- This is your 'SharedPreferences' logic ---
    private fun saveUserData(context: Context, username: String, userId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("logged_in_user", username)
            putString("user_id", userId)
            apply()
        }
    }
}