package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // <-- ADDED IMPORT
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
// --- UPDATED IMPORT ---
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sparkapp.network.ScoreboardResponse

// This is the ScoreDetailPage from the Flutter file
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScoreDetailScreen(
    score: ScoreboardResponse,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scenario Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null) // <-- UPDATED ICON
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {
            Card(
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp), // This now works
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "User: ${score.username ?: "Unknown"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Scenario:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold // Changed to SemiBold as Bold is used above
                    )
                    Text(score.scenario ?: "No scenario available")
                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    if (!score.answer1.isNullOrEmpty()) Text("Answer 1: ${score.answer1}")
                    if (!score.answer2.isNullOrEmpty()) Text("Answer 2: ${score.answer2}")
                    if (!score.answer3.isNullOrEmpty()) Text("Answer 3: ${score.answer3}")
                    if (!score.answer4.isNullOrEmpty()) Text("Answer 4: ${score.answer4}")

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Score: ${score.score}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50) // Green color
                    )
                }
            }
        }
    }
}