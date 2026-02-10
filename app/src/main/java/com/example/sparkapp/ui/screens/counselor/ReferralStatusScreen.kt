package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.network.ReferralResponse
import com.google.gson.Gson
import java.net.URLEncoder

// --- THEME COLORS ---
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)
private val SuccessGreen = Color(0xFF4CAF50)
private val PendingOrange = Color(0xFFFF9800)

@Composable
fun ReferralStatusScreen(
    counselorId: String,
    mainNavController: NavController,
    viewModel: CounselorViewModel = viewModel()
) {
    // Fetch referrals when screen loads
    LaunchedEffect(counselorId) {
        viewModel.fetchMyReferrals(counselorId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 16.dp)
    ) {
        // --- Header ---
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Referral History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            "Track status and doctor responses",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))

        // --- Content ---
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLightBlue)
            }
        } else if (viewModel.myReferrals.isEmpty()) {
            EmptyReferralState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(viewModel.myReferrals) { student ->
                    ReferralStatusCard(student) {
                        // Serialize student object to pass to detail screen
                        val json = Gson().toJson(student)
                        val encodedJson = URLEncoder.encode(json, "UTF-8")
                        mainNavController.navigate("counselor_student_detail/$encodedJson")
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralStatusCard(student: ReferralResponse, onClick: () -> Unit) {
    val hasReplied = !student.doctorSuggestion.isNullOrEmpty() || !student.precautions.isNullOrEmpty()

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PrimaryLightBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = PrimaryLightBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name and ID
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name ?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "ID: ${student.uniqueId ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Status Badge
                StatusBadge(isReplied = hasReplied)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Reason Snippet
            Text(
                text = "Reason: ${student.reason ?: "No reason provided"}",
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun StatusBadge(isReplied: Boolean) {
    val backgroundColor = if (isReplied) SuccessGreen.copy(alpha = 0.1f) else PendingOrange.copy(alpha = 0.1f)
    val contentColor = if (isReplied) SuccessGreen else PendingOrange
    val text = if (isReplied) "Replied" else "Pending"
    val icon = if (isReplied) Icons.Default.CheckCircle else Icons.Default.Schedule

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50), // Pill shape
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyReferralState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No referrals yet",
            color = Color.Gray,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Create a new referral from Home",
            color = Color.LightGray,
            fontSize = 14.sp
        )
    }
}