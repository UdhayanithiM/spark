package com.example.sparkapp.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// --- THEME COLORS ---
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)
private val SuccessGreen = Color(0xFF4CAF50)
private val PendingOrange = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ParentViewModel = viewModel()
) {
    val state = viewModel.dashboardState

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Parent Portal",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Clear saved data on logout
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Logout", tint = Color.White)
                    }
                },
                actions = {
                    // --- PROFILE ICON REMOVED AS REQUESTED ---
                    // Uncomment below to show it again in the future
                    /*
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, "Profile", tint = Color.White)
                    }
                    */
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryLightBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            // --- 1. SEARCH SECTION (Only show if student NOT fixed) ---
            if (!state.isStudentFixed) {
                Text(
                    "Find Your Child",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.uniqueIdInput,
                    onValueChange = { viewModel.onUniqueIdChange(it) },
                    label = { Text("Enter Student Unique ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.searchStudent() }),
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.searchStudent() },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = PrimaryLightBlue)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryLightBlue,
                        focusedLabelColor = PrimaryLightBlue,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                Spacer(Modifier.height(24.dp))
            }

            // --- 2. CONTENT SECTION ---
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLightBlue)
                }
            } else if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color.Red)
                        Spacer(Modifier.width(12.dp))
                        Text(text = state.error, color = Color.Red, fontWeight = FontWeight.Medium)
                    }
                }
            } else if (state.studentData != null) {
                val student = state.studentData

                // --- Student Header Card ---
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PrimaryLightBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                                fontSize = 28.sp,
                                color = PrimaryLightBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Name & Class
                        Column {
                            Text(
                                text = student.name ?: "Unknown",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Class: ${student.standard ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Basic Info Card ---
                ModernInfoCard(title = "Profile Details") {
                    ModernInfoRow(Icons.Default.Cake, "Age", "${student.age} Years")
                    ModernInfoRow(Icons.Default.Person, "Gender", student.gender ?: "N/A")
                    ModernInfoRow(Icons.Default.LocationOn, "Address", student.address ?: "N/A")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Report Card ---
                ModernInfoCard(title = "Referral Report") {
                    ModernInfoRow(Icons.Default.School, "Academic", student.academic ?: "N/A")
                    ModernInfoRow(Icons.Default.Psychology, "Behavior", student.behavior ?: "N/A")
                    ModernInfoRow(Icons.Default.Warning, "Disciplinary", student.disciplinary ?: "N/A")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- DOCTOR FEEDBACK (Highlighted) ---
                val hasFeedback = !student.doctorSuggestion.isNullOrEmpty() || !student.precautions.isNullOrEmpty()
                val feedbackColor = if (hasFeedback) SuccessGreen else PendingOrange
                val feedbackBg = if (hasFeedback) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = feedbackBg),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasFeedback) Icons.Default.MedicalServices else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = feedbackColor
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Doctor's Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = feedbackColor
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = feedbackColor.copy(alpha = 0.2f))

                        if (!hasFeedback) {
                            Text(
                                "Waiting for doctor's review.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        } else {
                            if (!student.doctorSuggestion.isNullOrEmpty()) {
                                Text("Suggestion:", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text(student.doctorSuggestion, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (!student.precautions.isNullOrEmpty()) {
                                Text("Precautions:", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                Text(student.precautions, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            } else {
                // --- Empty State ---
                EmptySearchState()
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun ModernInfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryLightBlue
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun ModernInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
    }
}

@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No Student Selected",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Enter a Unique ID above to view details.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )
    }
}