package com.example.sparkapp.ui.screens.parent

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.ApiService
import com.example.sparkapp.network.ParentDetails
import com.example.sparkapp.network.RetrofitClient
// import com.example.sparkapp.ui.screens.login.DataStoreViewModel // <-- REMOVED UNUSED IMPORT
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ParentProfileUiState(
    val details: ParentDetails? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ParentViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ParentProfileUiState())
        private set

    private val apiService: ApiService = RetrofitClient.instance
    // private val dataStore = DataStoreViewModel(application) // <-- REMOVED UNUSED VARIABLE

    init {
        fetchParentDetails()
    }

    private fun fetchParentDetails() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            // FIXME: This is hardcoded to match the Flutter code's bug.
            // This should use the logged-in parent's ID from DataStore.
            // val parentId = dataStore.userId.first() ?: 0
            val parentId = 10 // Hardcoded from StudentDetailsPage.dart, line 157

            try {
                val response = apiService.getParentProfile(mapOf("id" to parentId))
                if (response.isSuccessful && response.body()?.status == "success") {
                    uiState = uiState.copy(
                        details = response.body()?.parentDetails,
                        isLoading = false
                    )
                } else {
                    val error = response.body()?.message ?: "Failed to load profile"
                    uiState = uiState.copy(isLoading = false, errorMessage = error)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}