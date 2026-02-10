package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.sparkapp.network.CounselorProfile
import com.example.sparkapp.network.KnowledgeTestResult
import com.example.sparkapp.network.ScenarioResult
import java.text.SimpleDateFormat
import java.util.Locale

// Define Theme Color locally if not available globally
private val PrimaryPurple = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorDetailScreen(
    counselor: CounselorProfile,
    viewModel: DoctorViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Assessment Scores", "Case Scenarios")
    val details = viewModel.selectedCounselorDetails

    LaunchedEffect(counselor.id) {
        viewModel.fetchCounselorDetails(counselor.id, counselor.email ?: "")
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Counselor Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // --- 1. PROFILE HEADER ---
            ProfileHeader(counselor)

            // --- 2. TABS ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryPurple
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // --- 3. CONTENT AREA ---
            if (details == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            } else {
                Box(modifier = Modifier.background(BackgroundGray).fillMaxSize()) {
                    when (selectedTab) {
                        0 -> AssessmentList(details.tests)
                        1 -> ScenarioList(details.scenarios)
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------- */
/* UI COMPONENTS */
/* ---------------------------------------------------- */

@Composable
fun ProfileHeader(counselor: CounselorProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar (Initials)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = counselor.name?.take(1)?.uppercase() ?: "?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name & School
                Column {
                    Text(
                        text = counselor.name ?: "Unknown Name",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = counselor.school ?: "No School Assigned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Contact Info
            ContactRow(Icons.Default.Email, counselor.email ?: "N/A")
            Spacer(modifier = Modifier.height(8.dp))
            ContactRow(Icons.Default.Phone, counselor.phone ?: "N/A")
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = PrimaryPurple)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}

/* ---------------------------------------------------- */
/* ASSESSMENT LIST */
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
            val score = test.score?.toIntOrNull() ?: 0
            val total = test.total?.toIntOrNull() ?: 1
            val percentage = (score.toFloat() / total.toFloat()) * 100

            // Determine Color based on score
            val badgeColor = when {
                percentage >= 80 -> Color(0xFF4CAF50) // Green
                percentage >= 50 -> Color(0xFFFF9800) // Orange
                else -> Color(0xFFF44336) // Red
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assignment, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Assessment Result",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatDate(test.date),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Score Badge
                    Surface(
                        color = badgeColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${test.score ?: "0"} / ${test.total ?: "0"}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = badgeColor
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Description, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Case Scenario Response",
                            color = PrimaryPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = item.scenario ?: "No content available",
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.DarkGray,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Submitted on ${formatDate(item.date)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------- */
/* HELPERS */
/* ---------------------------------------------------- */

@Composable
fun EmptyState(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Description,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.size(60.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(text, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "Unknown Date"
    return try {
        // Adjust these patterns to match your PHP backend output
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        output.format(input.parse(dateString)!!)
    } catch (e: Exception) {
        dateString
    }
}