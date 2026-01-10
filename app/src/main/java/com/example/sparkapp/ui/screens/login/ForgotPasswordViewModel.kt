package com.example.sparkapp.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    var email by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun resetPassword(onSuccess: () -> Unit) {
        if (email.isBlank() || newPassword.isBlank()) {
            viewModelScope.launch { _uiEvent.emit("Please fill all fields") }
            return
        }

        if (newPassword != confirmPassword) {
            viewModelScope.launch { _uiEvent.emit("Passwords do not match") }
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val body = mapOf(
                    "email" to email.trim(),
                    "new_password" to newPassword.trim()
                )

                val response = RetrofitClient.instance.resetPassword(body)

                if (response.isSuccessful && response.body()?.get("status") == "success") {
                    _uiEvent.emit("Password reset successful!")
                    onSuccess()
                } else {
                    val msg = response.body()?.get("message") ?: "Failed to reset password"
                    _uiEvent.emit(msg)
                }
            } catch (e: Exception) {
                _uiEvent.emit("Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}