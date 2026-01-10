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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ParentViewModel = viewModel()
) {
    val state = viewModel.dashboardState
    val deepPurple = Color(0xFF673AB7)
    val grey200 = Color(0xFFEEEEEE)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Parent Portal") },
                navigationIcon = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, "Profile")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = deepPurple,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. SEARCH SECTION ---
            Text(
                "Find Student Details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.uniqueIdInput,
                onValueChange = { viewModel.onUniqueIdChange(it) },
                label = { Text("Enter Student Unique ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { viewModel.searchStudent() }),
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchStudent() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            // --- 2. CONTENT SECTION ---
            if (state.isLoading) {
                CircularProgressIndicator(color = deepPurple)
            } else if (state.error != null) {
                Text(
                    text = state.error,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            } else if (state.studentData != null) {
                val student = state.studentData

                // --- Student Header Card ---
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = grey200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name?.firstOrNull()?.toString() ?: "?",
                                fontSize = 30.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = student.name ?: "Unknown",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Class: ${student.standard}", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Basic Info Card ---
                InfoCard(title = "Basic Information", backgroundColor = grey200) {
                    InfoRow("Age", student.age)
                    InfoRow("Gender", student.gender)
                    InfoRow("Address", student.address)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Academic / Behavior Card ---
                InfoCard(title = "Report Details", backgroundColor = grey200) {
                    InfoRow("Academic Performance", student.academic)
                    InfoRow("Behavioral Issues", student.behavior)
                    InfoRow("Disciplinary History", student.disciplinary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- DOCTOR SUGGESTIONS (Highlighted) ---
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Light Green
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Text(
                            "Doctor's Feedback",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (student.doctorSuggestion.isNullOrEmpty() && student.precautions.isNullOrEmpty()) {
                            Text("No feedback from doctor yet.", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        } else {
                            if (!student.doctorSuggestion.isNullOrEmpty()) {
                                Text("Suggestion:", fontWeight = FontWeight.Bold)
                                Text(student.doctorSuggestion)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (!student.precautions.isNullOrEmpty()) {
                                Text("Precautions:", fontWeight = FontWeight.Bold, color = Color.Red)
                                Text(student.precautions)
                            }
                        }
                    }
                }
            } else {
                // Idle state text
                Text(
                    "Enter a Student ID above to view details.",
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun InfoCard(title: String, backgroundColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "$label: ",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(140.dp)
            )
            Text(
                text = value,
                modifier = Modifier.weight(1f)
            )
        }
    }
}