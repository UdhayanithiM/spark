package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sparkapp.network.CounselorProfile
import com.example.sparkapp.network.KnowledgeTestResult
import com.example.sparkapp.network.ScenarioResult
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorDetailScreen(
    counselor: CounselorProfile,
    viewModel: DoctorViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Assessment Scores", "Case Scenarios")
    val details = viewModel.selectedCounselorDetails

    LaunchedEffect(counselor.id) {
        viewModel.fetchCounselorDetails(counselor.id, counselor.email ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(counselor.name ?: "Counselor Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Email: ${counselor.email ?: "N/A"}")
                    Text("Phone: ${counselor.phone ?: "N/A"}")
                    Text("School: ${counselor.school ?: "N/A"}")
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (details == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    0 -> AssessmentList(details.tests)
                    1 -> ScenarioList(details.scenarios)
                }
            }
        }
    }
}

/* ---------------------------------------------------- */
/* ASSESSMENT LIST (Using Scores Table Data) */
/* ---------------------------------------------------- */
@Composable
fun AssessmentList(tests: List<KnowledgeTestResult>) {
    if (tests.isEmpty()) {
        EmptyState("No assessment scores found")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tests) { test ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Assessment Result", // You can change this to "Pre-Test" or "Post-Test" if available
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        // If date is not available in 'scores' table, we hide it or show ID order
                    }

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = MaterialTheme.shapes.small
                    ) {
                        // Display Score / Total (e.g., 13 / 20)
                        Text(
                            text = "${test.score ?: "0"} / ${test.total ?: "0"}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------- */
/* SCENARIO LIST */
/* ---------------------------------------------------- */
@Composable
fun ScenarioList(scenarios: List<ScenarioResult>) {
    if (scenarios.isEmpty()) {
        EmptyState("No case scenarios submitted")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(scenarios) { item ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Case Scenario", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        item.scenario ?: "Unknown Scenario",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            formatDate(item.date),
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Text(text, color = Color.Gray, fontSize = 16.sp)
    }
}

fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Unknown Date"
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        output.format(input.parse(dateString)!!)
    } catch (e: Exception) {
        dateString
    }
}