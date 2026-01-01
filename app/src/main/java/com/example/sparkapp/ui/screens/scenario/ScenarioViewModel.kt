package com.example.sparkapp.ui.screens.scenario

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.network.RetrofitClient
import com.example.sparkapp.network.ScenarioRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// This holds the data for one scenario (from your Flutter list)
data class Scenario(
    val title: String,
    val questions: List<String> = listOf(
        "1. Describe the features of the psychological problem in the student",
        "2. What are the concerns by teacher and parent?",
        "3. What potential psychological issue could the student have?",
        "4. What intervention or support strategies could be done?"
    ),
    // This will hold the user's text input
    var responses: MutableList<String> = mutableListOf("", "", "", "")
)

// This defines the entire screen's state
data class ScenarioUiState(
    val testStatus: String = "loading", // "loading", "completed", "not_completed", "error"
    val scenarios: List<Scenario> = emptyList(),
    val currentScenarioIndex: Int = 0,
    val isLoading: Boolean = false,
    val submissionMessage: String? = null
)

class ScenarioViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScenarioUiState())
    val uiState: StateFlow<ScenarioUiState> = _uiState.asStateFlow()

    // This is your hardcoded list of 7 scenarios
    private val allScenarios = listOf(
        Scenario(title = "Trina, a 9-year-old child, faces an exceptional memory for trivia and academic facts..."),
        Scenario(title = "Krishna, an 11-year-old, often submits incomplete homework, struggles to stay organized..."),
        Scenario(title = "Priya, a 9-year-old, impresses teachers with her ability to explain complex concepts..."),
        Scenario(title = "Ajay, a 15-year-old, has been suspended twice this term for vandalizing school property..."),
        Scenario(title = "Kartik, a 14-year-old, has recently started avoiding participation in debates..."),
        Scenario(title = "Meena, an 8-year-old, has been mispronouncing certain words and avoids reading aloud..."),
        Scenario(title = "Mohan, a 10-year-old, enjoys sports but has been getting into frequent fights...")
    )

    init {
        // Load scenarios into state
        _uiState.update { it.copy(scenarios = allScenarios) }
        // Check if the user has already completed this test
        checkTestStatus()
    }

    fun checkTestStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(testStatus = "loading") }

            val prefs = getApplication<Application>().getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
            val username = prefs.getString("logged_in_user", null) // This is the user's email

            if (username == null) {
                _uiState.update { it.copy(testStatus = "error") }
                return@launch
            }

            try {
                val response = RetrofitClient.instance.checkTestStatus(
                    testType = "scenario", // Tell PHP which test we are checking
                    userKey = username     // Send the user's email as the key
                )

                if (response.isSuccessful) {
                    _uiState.update { it.copy(testStatus = response.body()?.status ?: "error") }
                } else {
                    _uiState.update { it.copy(testStatus = "error") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(testStatus = "error") }
            }
        }
    }

    // Called when the user types in a text field
    fun onResponseChanged(questionIndex: Int, text: String) {
        _uiState.value.scenarios[_uiState.value.currentScenarioIndex].responses[questionIndex] = text
        // We just updated the list, but Compose won't recompose.
        // We "trick" it by creating a new state object.
        _uiState.update { it.copy(submissionMessage = null) }
    }

    // Called when the "Next" or "Submit" button is pressed
    fun submitCurrentScenario(onAllScenariosFinished: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, submissionMessage = null) }

            val prefs = getApplication<Application>().getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
            val username = prefs.getString("logged_in_user", "unknown") ?: "unknown"

            val currentScenario = _uiState.value.scenarios[_uiState.value.currentScenarioIndex]

            val request = ScenarioRequest(
                username = username,
                scenario = currentScenario.title,
                responses = currentScenario.responses
            )

            try {
                val response = RetrofitClient.instance.submitScenarioResponse(request)
                if (response.isSuccessful && response.body()?.status == "success") {
                    // Success!
                    val nextIndex = _uiState.value.currentScenarioIndex + 1
                    if (nextIndex < _uiState.value.scenarios.size) {
                        // Go to next scenario
                        _uiState.update {
                            it.copy(
                                currentScenarioIndex = nextIndex,
                                isLoading = false,
                                submissionMessage = "Scenario ${it.currentScenarioIndex + 1} saved!"
                            )
                        }
                    } else {
                        // All scenarios are done
                        _uiState.update { it.copy(isLoading = false, testStatus = "completed") }
                        onAllScenariosFinished() // This will trigger navigation
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, submissionMessage = "Error: ${response.message()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, submissionMessage = "Network Error: ${e.message}") }
            }
        }
    }
}