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

// This data class holds the state for ALL fields
data class SignUpUiState(
    val selectedRole: String = "Doctor",
    val roleOptions: List<String> = listOf("Doctor", "Counselor", "Parent"),
    val isLoading: Boolean = false,

    // Common
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",

    // Counselor & Parent
    val age: String = "", // For Counselor OR Parent
    val qualification: String = "",

    // Counselor only
    val school: String = "",
    val yearInSchool: String = "",

    // Parent only
    val fatherOcc: String = "",
    val motherOcc: String = "",
    val fatherPhone: String = "",
    val motherPhone: String = "",

    // Parent's Student only
    val studentName: String = "",
    val standard: String = "",
    val studentAge: String = "", // Fixed: Separate age field for student
    val registerNumber: String = ""
)

// One-time events
sealed class SignUpUiEvent {
    data class SignUpSuccess(val message: String) : SignUpUiEvent()
    data class ShowError(val message: String) : SignUpUiEvent()
}

class SignUpViewModel : ViewModel() {

    var uiState by mutableStateOf(SignUpUiState())
        private set

    private val _uiEvent = MutableSharedFlow<SignUpUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // --- Event Handlers for all fields ---
    fun onRoleChanged(role: String) { uiState = uiState.copy(selectedRole = role) }
    fun onNameChanged(name: String) { uiState = uiState.copy(name = name) }
    fun onEmailChanged(email: String) { uiState = uiState.copy(email = email) }
    fun onPasswordChanged(password: String) { uiState = uiState.copy(password = password) }
    fun onPhoneChanged(phone: String) { uiState = uiState.copy(phone = phone) }
    fun onAgeChanged(age: String) { uiState = uiState.copy(age = age) }
    fun onQualificationChanged(q: String) { uiState = uiState.copy(qualification = q) }
    fun onSchoolChanged(school: String) { uiState = uiState.copy(school = school) }
    fun onYearInSchoolChanged(year: String) { uiState = uiState.copy(yearInSchool = year) }
    fun onFatherOccChanged(occ: String) { uiState = uiState.copy(fatherOcc = occ) }
    fun onMotherOccChanged(occ: String) { uiState = uiState.copy(motherOcc = occ) }
    fun onFatherPhoneChanged(phone: String) { uiState = uiState.copy(fatherPhone = phone) }
    fun onMotherPhoneChanged(phone: String) { uiState = uiState.copy(motherPhone = phone) }
    fun onStudentNameChanged(name: String) { uiState = uiState.copy(studentName = name) }
    fun onStandardChanged(std: String) { uiState = uiState.copy(standard = std) }
    fun onStudentAgeChanged(age: String) { uiState = uiState.copy(studentAge = age) } // Fixed
    fun onRegisterNumberChanged(reg: String) { uiState = uiState.copy(registerNumber = reg) }

    // --- This is your '_submitSignup()' function ---
    fun onSignUpClicked() {
        if (uiState.isLoading) return
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // 1. Build the data map (this is your 'Map<String, dynamic> data')
                val data = buildDataMap()

                // 2. Make the API call
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
                Log.e("SignUpViewModel", "Network error: ${e.message}")
                _uiEvent.emit(SignUpUiEvent.ShowError("⚠️ Error connecting to server: ${e.message}"))
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Unknown error: ${e.message}")
                _uiEvent.emit(SignUpUiEvent.ShowError("An unknown error occurred."))
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    // This function replicates your logic for building the data map
    private fun buildDataMap(): Map<String, Any> {
        val data = mutableMapOf<String, Any>(
            "role" to uiState.selectedRole,
            "name" to uiState.name.trim(),
            "email" to uiState.email.trim(),
            "password" to uiState.password.trim(), // Note: Sending plaintext, as per Flutter app
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
                data["age"] = uiState.age.trim() // Parent's Age
                data["qualification"] = uiState.qualification.trim()
                data["father_occ"] = uiState.fatherOcc.trim()
                data["mother_occ"] = uiState.motherOcc.trim()
                data["father_phone"] = uiState.fatherPhone.trim()
                data["mother_phone"] = uiState.motherPhone.trim()
                data["student_name"] = uiState.studentName.trim()
                data["standard"] = uiState.standard.trim()
                // Note: The original Flutter app has a bug where it doesn't send student_age.
                // We are sending it here. Your PHP script already accepts it.
                data["student_age"] = uiState.studentAge.trim()
                data["register_number"] = uiState.registerNumber.trim()
            }
        }
        return data.filterValues { (it as? String)?.isNotEmpty() ?: true } // Remove empty strings
    }
}