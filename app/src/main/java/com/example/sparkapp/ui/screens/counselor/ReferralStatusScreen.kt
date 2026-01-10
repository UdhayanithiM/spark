package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.network.ReferralResponse
import com.google.gson.Gson
import java.net.URLEncoder

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Referral Status",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.myReferrals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You haven't referred any students yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.myReferrals) { student ->
                    ReferralStatusCard(student) {
                        // Serialize student object to pass to detail screen
                        val json = Gson().toJson(student)
                        val encodedJson = URLEncoder.encode(json, "UTF-8")
                        // Ensure this route matches your MainNavigation
                        mainNavController.navigate("counselor_student_detail/$encodedJson")
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralStatusCard(student: ReferralResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = "Reason: ${student.reason ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            // Status Badge
            val hasReplied = !student.doctorSuggestion.isNullOrEmpty()
            Surface(
                color = if (hasReplied) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (hasReplied) "Replied" else "Pending",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = if (hasReplied) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}