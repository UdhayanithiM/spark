package com.example.sparkapp.ui.screens.referral

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.ReferralRequest
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SubmissionStatus { IDLE, LOADING, SUCCESS, ERROR }

class ReferralViewModel(application: Application) : AndroidViewModel(application) {

    // State for all text fields
    val uniqueId = mutableStateOf("") // <-- NEW
    val name = mutableStateOf("")
    val age = mutableStateOf("")
    val standard = mutableStateOf("")
    val address = mutableStateOf("")
    val reason = mutableStateOf("")
    val behavior = mutableStateOf("")
    val academic = mutableStateOf("")
    val disciplinary = mutableStateOf("")
    val specialNeed = mutableStateOf("")

    private val _submissionStatus = MutableStateFlow(SubmissionStatus.IDLE)
    val submissionStatus: StateFlow<SubmissionStatus> = _submissionStatus

    fun sendReferral() {
        // Validate Unique ID is present
        if (uniqueId.value.isBlank()) return

        viewModelScope.launch {
            _submissionStatus.value = SubmissionStatus.LOADING
            try {
                val context = getApplication<Application>().applicationContext
                val prefs = context.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
                val currentUserId = prefs.getString("user_id", "0") ?: "0"

                val request = ReferralRequest(
                    counselorId = currentUserId,
                    uniqueId = uniqueId.value.trim(), // <-- Pass Unique ID
                    name = name.value.trim(),
                    age = age.value.trim().toIntOrNull() ?: 0,
                    standard = standard.value.trim(),
                    address = address.value.trim(),
                    reason = reason.value.trim(),
                    behavior = behavior.value.trim(),
                    academic = academic.value.trim(),
                    disciplinary = disciplinary.value.trim(),
                    specialNeed = specialNeed.value.trim()
                )

                val response = RetrofitClient.instance.submitReferral(request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    _submissionStatus.value = SubmissionStatus.SUCCESS
                    clearFields()
                } else {
                    _submissionStatus.value = SubmissionStatus.ERROR
                }
            } catch (e: Exception) {
                _submissionStatus.value = SubmissionStatus.ERROR
            }
        }
    }

    fun clearFields() {
        uniqueId.value = "" // <-- Clear
        name.value = ""
        age.value = ""
        standard.value = ""
        address.value = ""
        reason.value = ""
        behavior.value = ""
        academic.value = ""
        disciplinary.value = ""
        specialNeed.value = ""
    }

    fun resetStatus() {
        _submissionStatus.value = SubmissionStatus.IDLE
    }
}