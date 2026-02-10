package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sparkapp.network.ScoreboardResponse

// Define Theme Colors locally
private val PrimaryPurple = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)
private val ScoreGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScoreDetailScreen(
    score: ScoreboardResponse,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Assessment Result",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- 1. HEADER CARD (User & Score) ---
            StudentScoreHeader(score)

            // --- 2. SCENARIO CARD ---
            ScenarioCard(score.scenario)

            // --- 3. ANSWERS CARD ---
            AnswersCard(score)
        }
    }
}

// ==========================================
// SUB-COMPONENTS
// ==========================================

@Composable
fun StudentScoreHeader(score: ScoreboardResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.username?.take(1)?.uppercase() ?: "?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Student Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = score.username ?: "Unknown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Score Badge
            Surface(
                color = ScoreGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ScoreGreen.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ScoreGreen
                    )
                    Text(
                        text = score.score ?: "0",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = ScoreGreen
                    )
                }
            }
        }
    }
}

@Composable
fun ScenarioCard(scenarioText: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, null, tint = PrimaryPurple)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Scenario Context",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = scenarioText ?: "No scenario details available.",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun AnswersCard(score: ScoreboardResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assignment, null, tint = PrimaryPurple)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Student Responses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            if (!score.answer1.isNullOrEmpty()) AnswerItem(1, score.answer1)
            if (!score.answer2.isNullOrEmpty()) AnswerItem(2, score.answer2)
            if (!score.answer3.isNullOrEmpty()) AnswerItem(3, score.answer3)
            if (!score.answer4.isNullOrEmpty()) AnswerItem(4, score.answer4)

            if (score.answer1.isNullOrEmpty() && score.answer2.isNullOrEmpty() &&
                score.answer3.isNullOrEmpty() && score.answer4.isNullOrEmpty()) {
                Text(
                    "No textual answers recorded.",
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AnswerItem(number: Int, text: String) {
    Row(
        modifier = Modifier.padding(bottom = 12.dp),
        // ✅ FIXED: Changed crossAxisAlignment to verticalAlignment
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF8BC34A), // Light Green
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Response $number",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}