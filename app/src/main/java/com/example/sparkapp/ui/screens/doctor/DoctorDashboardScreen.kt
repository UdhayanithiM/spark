package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.sparkapp.network.ReferralResponse
import com.example.sparkapp.ui.theme.SparkAppPurple // Ensure this exists or use Color(0xFF673AB7)
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    viewModel: DoctorViewModel = viewModel(),
    onNavigateToStudent: (ReferralResponse) -> Unit,
    onNavigateToCounselors: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState = viewModel.uiState
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)
    val backgroundColor = Color(0xFFF5F7FA) // Light Gray Background

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Doctor Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SparkAppPurple, // Or Color(0xFF673AB7)
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
        ) {
            // ===============================
            // QUICK ACTIONS SECTION
            // ===============================
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Button(
                    onClick = onNavigateToCounselors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple)
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("View Counselor Directory", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ===============================
            // REFERRAL LIST HEADER
            // ===============================
            PaddingLabel("Incoming Referrals")

            // ===============================
            // REFERRAL LIST
            // ===============================
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.fetchReferrals() },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading && uiState.referrals.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = SparkAppPurple)
                        }
                    }
                    uiState.hasError -> {
                        EmptyStateView(
                            icon = Icons.Default.ErrorOutline,
                            message = "Failed to load referrals",
                            color = Color.Red
                        )
                    }
                    uiState.referrals.isEmpty() -> {
                        EmptyStateView(
                            icon = Icons.Default.Inbox,
                            message = "No pending referrals found",
                            color = Color.Gray
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.referrals) { student ->
                                ReferralCard(student = student, onClick = { onNavigateToStudent(student) })
                            }
                            item { Spacer(Modifier.height(16.dp)) } // Bottom padding
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReferralCard(
    student: ReferralResponse,
    onClick: () -> Unit
) {
    // Determine status based on if doctor suggestion exists
    val isReplied = !student.doctorSuggestion.isNullOrEmpty() || !student.precautions.isNullOrEmpty()
    val statusColor = if (isReplied) Color(0xFF4CAF50) else Color(0xFFFF9800) // Green vs Orange
    val statusText = if (isReplied) "Replied" else "Pending"

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Avatar, Name, Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SparkAppPurple.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = SparkAppPurple
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Name & ID
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ID: ${student.uniqueId ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Info Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(icon = Icons.Default.Cake, text = "${student.age} Yrs")
                InfoChip(icon = Icons.Default.School, text = student.standard ?: "N/A")
            }

            Spacer(Modifier.height(8.dp))

            // Reason Preview
            Text(
                text = "Reason: ${student.reason ?: "No reason provided"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- Helpers ---

@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF5F7FA), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PaddingLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@Composable
fun EmptyStateView(icon: ImageVector, message: String, color: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = color.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(8.dp))
            Text(message, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}