package com.example.sparkapp.ui.screens.login

import android.content.Context
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
    val selectedRole: String = "Doctor",
    val isLoading: Boolean = false,
    val roleOptions: List<String> = listOf("Doctor", "Counselor" , "Parent")
)

// This sealed class represents one-time "events" to send to the UI
sealed class LoginUiEvent {
    data class LoginSuccess(val role: String, val userId: Int) : LoginUiEvent()
    data class ShowError(val message: String) : LoginUiEvent()
}

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun onRoleChanged(role: String) {
        uiState = uiState.copy(selectedRole = role)
    }

    fun onLoginClicked(context: Context) {
        if (uiState.isLoading) return

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Initialize DataStore
                val dataStore = DataStoreViewModel(context.applicationContext as android.app.Application)

                val request = LoginRequest(
                    email = uiState.email.trim(),
                    password = uiState.password.trim(),
                    role = uiState.selectedRole
                )

                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    if (loginResponse.status == "success" && loginResponse.role != null) {
                        val userId = loginResponse.userId
                        if (userId == null) {
                            _uiEvent.emit(LoginUiEvent.ShowError("Login success but no user ID returned."))
                            return@launch
                        }

                        // --- FIX: Save to DataStore AND SharedPreferences ---

                        // 1. Save to SharedPreferences (Legacy support)
                        val sharedPreferences = context.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("logged_in_user", uiState.email.trim())
                            putString("user_id", userId.toString())
                            apply()
                        }

                        // 2. Save to DataStore (For ProfileViewModel)
                        // ✅ FIXED: Now passing only (String, Int) to match DataStoreViewModel
                        dataStore.saveUserLogin(uiState.email.trim(), userId)

                        Log.d("LoginDebug", "Saved to DataStore: ${uiState.email.trim()}")

                        _uiEvent.emit(LoginUiEvent.LoginSuccess(loginResponse.role, userId))
                    } else {
                        _uiEvent.emit(LoginUiEvent.ShowError(loginResponse.message))
                    }
                } else {
                    _uiEvent.emit(LoginUiEvent.ShowError("Server error: ${response.code()}"))
                }

            } catch (e: IOException) {
                Log.e("LoginViewModel", "Network error: ${e.message}")
                _uiEvent.emit(LoginUiEvent.ShowError("⚠️ Error connecting to server: ${e.message}"))
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unknown error: ${e.message}")
                e.printStackTrace()
                _uiEvent.emit(LoginUiEvent.ShowError("An unknown error occurred."))
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}