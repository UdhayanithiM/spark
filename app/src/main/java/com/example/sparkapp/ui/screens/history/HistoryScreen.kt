package com.example.sparkapp.ui.screens.history

import androidx.compose.foundation.clickable // <-- 1. ADDED IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle // <-- 2. ADDED IMPORT
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.data.ScoreEntry

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scores by viewModel.scores.collectAsState()

    // Note: No Scaffold or TopAppBar here, as CounselorDashboardScreen will provide it.

    when (uiState) {
        HistoryUiState.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        HistoryUiState.ERROR -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("⚠️ Error fetching scores")
            }
        }
        HistoryUiState.SUCCESS -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(scores) { score ->
                    ScoreCard(score = score, onClick = {
                        // Navigate to the detail screen
                        navController.navigate(
                            "score_detail/${score.userName}/${score.score}/${score.total}"
                        )
                    })
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(score: ScoreEntry, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick), // <-- Fixed by import
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = score.userName,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold) // <-- Fixed by import
                )
            },
            supportingContent = {
                Text("Score: ${score.score}/${score.total}")
            },
            leadingContent = {
                // <-- 3. FIXED: Removed the non-existent 'CircleAvatar' wrapper.
                // The Icon composable goes directly into the leadingContent slot.
                Icon(
                    Icons.Default.Person,
                    contentDescription = "User",
                    modifier = Modifier.size(28.dp)
                )
            }
        )
    }
}