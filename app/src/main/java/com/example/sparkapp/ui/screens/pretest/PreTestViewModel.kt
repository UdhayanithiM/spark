package com.example.sparkapp.ui.screens.pretest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.data.PreTestQuestions
import com.example.sparkapp.data.Question
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// This sealed interface represents the 4 states of your screen
sealed interface PreTestUiState {
    object Loading : PreTestUiState
    object Completed : PreTestUiState
    data class Error(val message: String) : PreTestUiState
    data class Quiz(val questions: List<Question>) : PreTestUiState
}

// This sealed class is for one-time events
sealed class PreTestUiEvent {
    data class ShowScoreDialog(val score: Int, val total: Int) : PreTestUiEvent()
}

class PreTestViewModel : ViewModel() {

    // This holds the main screen state (Loading, Completed, Error, Quiz)
    private val _uiState = MutableStateFlow<PreTestUiState>(PreTestUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // This is for one-time events like the score dialog
    private val _uiEvent = MutableSharedFlow<PreTestUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // This is your 'userAnswers' list, stored as a map
    val userAnswers = mutableStateMapOf<Int, Int?>()

    private val questions = PreTestQuestions.allQuestions
    private var username: String = ""

    // This replaces your 'initState()'
    fun loadTestStatus(context: Context) {
        checkTestStatus(context)
    }

    // This is your '_checkTestStatus()' function
    fun checkTestStatus(context: Context) {
        _uiState.value = PreTestUiState.Loading
        viewModelScope.launch {
            username = getUsername(context)
            if (username.isEmpty()) {
                _uiState.value = PreTestUiState.Error("Could not find logged in user.")
                return@launch
            }

            try {
                // Call the new API endpoint
                // --- FIX 1: Renamed 'checkTestCompletion' to 'checkTestStatus' (matches ApiService.kt) ---
                val response = RetrofitClient.instance.checkTestStatus(
                    testType = "pretest",
                    userKey = username
                )

                if (response.isSuccessful && response.body() != null) {
                    // --- FIX 2: Accessing the '.status' property directly (matches TestStatusResponse.kt) ---
                    when (response.body()!!.status) {
                        "completed" -> _uiState.value = PreTestUiState.Completed
                        "not_completed" -> _uiState.value = PreTestUiState.Quiz(questions)
                        else -> _uiState.value = PreTestUiState.Error("Invalid status from server.")
                    }
                } else {
                    _uiState.value = PreTestUiState.Error("Server error: ${response.code()}")
                }

            } catch (e: IOException) {
                _uiState.value = PreTestUiState.Error("Network Error: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = PreTestUiState.Error("Unknown Error: ${e.message}")
            }
        }
    }

    // This is your 'onChanged' for the RadioListTile
    fun onAnswerSelected(questionIndex: Int, answerIndex: Int) {
        userAnswers[questionIndex] = answerIndex
    }

    // This is your 'submitQuiz()' function
    // --- FIX 3: Removed unused 'context' parameter to fix the warning ---
    fun submitQuiz() {
        viewModelScope.launch {
            // 1. Calculate score
            var score = 0
            for (i in questions.indices) {
                val correctAnswer = questions[i].answerIndex
                val userAnswer = userAnswers[i]
                if (userAnswer != null && userAnswer == correctAnswer) {
                    score++
                }
            }

            // 2. Save score to backend (your 'saveScore()' function)
            try {
                val response = RetrofitClient.instance.submitPreTestScore(
                    mapOf(
                        "user_name" to username,
                        "score" to score,
                        "total" to questions.size
                    )
                )
                if (response.isSuccessful && response.body()?.get("status") == "success") {
                    Log.i("PreTestViewModel", "Score saved successfully")
                } else {
                    Log.e("PreTestViewModel", "Failed to save score")
                }
            } catch (e: Exception) {
                Log.e("PreTestViewModel", "Error saving score: ${e.message}")
            }

            // 3. Show the score dialog
            _uiEvent.emit(PreTestUiEvent.ShowScoreDialog(score, questions.size))
        }
    }

    // Helper to get username from SharedPreferences
    private fun getUsername(context: Context): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("logged_in_user", "") ?: ""
    }
}