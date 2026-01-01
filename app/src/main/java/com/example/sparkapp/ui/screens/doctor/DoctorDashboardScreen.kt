package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sparkapp.network.ReferralResponse
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
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = uiState.isLoading
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Referred Students") },
                navigationIcon = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = Color.White)
                    }
                },
                // ❌ REMOVED actions (scoreboard + notifications)
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
            // VIEW COUNSELORS BUTTON
            // ===============================
            Button(
                onClick = onNavigateToCounselors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("View All Counselors", fontSize = 18.sp)
            }

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
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.hasError -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error loading referrals", color = Color.Red)
                        }
                    }

                    uiState.referrals.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No referrals found")
                        }
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.referrals) { student ->
                                ReferralCard(
                                    student = student,
                                    onClick = { onNavigateToStudent(student) }
                                )
                            }
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
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = student.name ?: "Unknown",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text("Age: ${student.age ?: "N/A"}")
            Text("Standard: ${student.standard ?: "N/A"}")
            Text("Behavior: ${student.behavior ?: "N/A"}")
        }
    }
}
