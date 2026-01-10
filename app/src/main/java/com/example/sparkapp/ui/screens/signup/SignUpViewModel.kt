package com.example.sparkapp.ui.screens.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Updated State: Removed Student-specific fields for Parent Signup
data class SignUpUiState(
    val selectedRole: String = "Doctor",
    val roleOptions: List<String> = listOf("Doctor", "Counselor", "Parent"),
    val isLoading: Boolean = false,

    // Common Fields
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",

    // Counselor & Parent Specifics
    val age: String = "",
    val qualification: String = "",

    // Counselor Only
    val school: String = "",
    val yearInSchool: String = "",

    // Parent Only (Personal Info)
    val fatherOcc: String = "",
    val motherOcc: String = "",
    val fatherPhone: String = "",
    val motherPhone: String = ""
)

sealed class SignUpUiEvent {
    data class SignUpSuccess(val message: String) : SignUpUiEvent()
    data class ShowError(val message: String) : SignUpUiEvent()
}

class SignUpViewModel : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set

    private val _uiEvent = MutableSharedFlow<SignUpUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // --- Event Handlers ---
    fun onRoleChanged(role: String) { uiState = uiState.copy(selectedRole = role) }
    fun onNameChanged(name: String) { uiState = uiState.copy(name = name) }
    fun onEmailChanged(email: String) { uiState = uiState.copy(email = email) }
    fun onPasswordChanged(password: String) { uiState = uiState.copy(password = password) }
    fun onPhoneChanged(phone: String) { uiState = uiState.copy(phone = phone) }
    fun onAgeChanged(age: String) { uiState = uiState.copy(age = age) }
    fun onQualificationChanged(q: String) { uiState = uiState.copy(qualification = q) }

    // Counselor Fields
    fun onSchoolChanged(school: String) { uiState = uiState.copy(school = school) }
    fun onYearInSchoolChanged(year: String) { uiState = uiState.copy(yearInSchool = year) }

    // Parent Fields
    fun onFatherOccChanged(occ: String) { uiState = uiState.copy(fatherOcc = occ) }
    fun onMotherOccChanged(occ: String) { uiState = uiState.copy(motherOcc = occ) }
    fun onFatherPhoneChanged(phone: String) { uiState = uiState.copy(fatherPhone = phone) }
    fun onMotherPhoneChanged(phone: String) { uiState = uiState.copy(motherPhone = phone) }

    fun onSignUpClicked() {
        if (uiState.isLoading) return
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val data = buildDataMap()
                val response = RetrofitClient.instance.signup(data)

                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!
                    if (responseData["status"] == "success") {
                        _uiEvent.emit(SignUpUiEvent.SignUpSuccess(responseData["message"] ?: "Signed up successfully"))
                    } else {
                        _uiEvent.emit(SignUpUiEvent.ShowError(responseData["message"] ?: "Signup failed"))
                    }
                } else {
                    _uiEvent.emit(SignUpUiEvent.ShowError("Server error: ${response.code()}"))
                }

            } catch (e: IOException) {
                _uiEvent.emit(SignUpUiEvent.ShowError("⚠️ Connection error: ${e.message}"))
            } catch (e: Exception) {
                _uiEvent.emit(SignUpUiEvent.ShowError("Error: ${e.message}"))
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    private fun buildDataMap(): Map<String, Any> {
        val data = mutableMapOf<String, Any>(
            "role" to uiState.selectedRole,
            "name" to uiState.name.trim(),
            "email" to uiState.email.trim(),
            "password" to uiState.password.trim(),
            "phone" to uiState.phone.trim()
        )

        when (uiState.selectedRole) {
            "Counselor" -> {
                data["age"] = uiState.age.trim()
                data["qualification"] = uiState.qualification.trim()
                data["school"] = uiState.school.trim()
                data["year_in_school"] = uiState.yearInSchool.trim()
            }
            "Parent" -> {
                data["age"] = uiState.age.trim()
                data["qualification"] = uiState.qualification.trim()
                data["father_occ"] = uiState.fatherOcc.trim()
                data["mother_occ"] = uiState.motherOcc.trim()
                data["father_phone"] = uiState.fatherPhone.trim()
                data["mother_phone"] = uiState.motherPhone.trim()
                // Removed student details from here
            }
        }
        return data.filterValues { (it as? String)?.isNotEmpty() ?: true }
    }
}