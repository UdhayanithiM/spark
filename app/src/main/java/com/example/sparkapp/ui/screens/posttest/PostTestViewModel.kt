package com.example.sparkapp.ui.screens.posttest

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.data.PostTestQuestion
import com.example.sparkapp.data.PostTestQuestions
import com.example.sparkapp.network.PostTestAnswer
import com.example.sparkapp.network.PostTestRequest
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// This mirrors the `_testStatus` string in Flutter
enum class TestStatus { LOADING, COMPLETED, NOT_COMPLETED, ERROR }

// This handles the submission dialogs
enum class SubmissionStatus { IDLE, LOADING, SUCCESS, ERROR }

class PostTestViewModel(application: Application) : AndroidViewModel(application) {

    // Main status for the screen
    private val _testStatus = MutableStateFlow(TestStatus.LOADING)
    val testStatus: StateFlow<TestStatus> = _testStatus

    // Status for the submission button/dialog
    private val _submissionStatus = MutableStateFlow(SubmissionStatus.IDLE)
    val submissionStatus: StateFlow<SubmissionStatus> = _submissionStatus

    val questions: List<PostTestQuestion> = PostTestQuestions.allQuestions
    val selectedOptions = mutableStateMapOf<Int, Int>()

    // Sections for the Pager
    val section1 = questions.filter { it.section == "Section 1: Knowledge" }
    val section2 = questions.filter { it.section == "Section 2: Attitude" }
    val section3 = questions.filter { it.section == "Section 3: Practice" }

    // --- THIS IS THE FIX: Removed 'private' ---
    var userId: String = "0"
        private set // Keep the setter private, but the getter is now public

    init {
        checkTestStatus()
    }

    fun checkTestStatus() {
        viewModelScope.launch {
            _testStatus.value = TestStatus.LOADING
            try {
                val prefs = getApplication<Application>().getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
                userId = prefs.getString("user_id", "0") ?: "0"

                if (userId == "0") {
                    _testStatus.value = TestStatus.ERROR
                    return@launch
                }

                val response = RetrofitClient.instance.checkTestStatus(
                    testType = "posttest",
                    userKey = userId
                )

                if (response.isSuccessful && response.body() != null) {
                    _testStatus.value = when (response.body()!!.status) {
                        "completed" -> TestStatus.COMPLETED
                        "not_completed" -> TestStatus.NOT_COMPLETED
                        else -> TestStatus.ERROR
                    }
                } else {
                    _testStatus.value = TestStatus.ERROR
                }
            } catch (e: Exception) {
                _testStatus.value = TestStatus.ERROR
            }
        }
    }

    fun onOptionSelected(questionIndex: Int, optionIndex: Int) {
        selectedOptions[questionIndex] = optionIndex
    }

    fun submitPostTest() {
        viewModelScope.launch {
            _submissionStatus.value = SubmissionStatus.LOADING
            try {
                val score = calculateScore()
                val responses = mutableListOf<PostTestAnswer>()

                questions.forEachIndexed { index, question ->
                    val selectedIndex = selectedOptions[index]
                    val answerText = selectedIndex?.let { question.options[it] } ?: ""
                    responses.add(PostTestAnswer(question.questionText, answerText))
                }

                val request = PostTestRequest(
                    userId = userId.toIntOrNull() ?: 0,
                    totalScore = score,
                    responses = responses
                )

                val response = RetrofitClient.instance.submitPostTest(request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    _submissionStatus.value = SubmissionStatus.SUCCESS
                } else {
                    _submissionStatus.value = SubmissionStatus.ERROR
                }

            } catch (e: Exception) {
                _submissionStatus.value = SubmissionStatus.ERROR
            }
        }
    }

    private fun calculateScore(): Int {
        var score = 0
        questions.forEachIndexed { index, question ->
            if (selectedOptions[index] == question.answerIndex) {
                score++
            }
        }
        return score
    }

    fun resetSubmissionStatus() {
        _submissionStatus.value = SubmissionStatus.IDLE
    }
}