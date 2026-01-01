package com.example.sparkapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sparkapp.data.ScoreEntry
import com.example.sparkapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI state for the screen
enum class HistoryUiState { LOADING, SUCCESS, ERROR }

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState.LOADING)
    val uiState: StateFlow<HistoryUiState> = _uiState

    private val _scores = MutableStateFlow<List<ScoreEntry>>(emptyList())
    val scores: StateFlow<List<ScoreEntry>> = _scores

    init {
        fetchScores()
    }

    fun fetchScores() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.LOADING
            try {
                val response = RetrofitClient.instance.getScoreHistory()
                if (response.isSuccessful && response.body()?.status == "success") {
                    _scores.value = response.body()?.data ?: emptyList()
                    _uiState.value = HistoryUiState.SUCCESS
                } else {
                    _uiState.value = HistoryUiState.ERROR
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.ERROR
            }
        }
    }
}