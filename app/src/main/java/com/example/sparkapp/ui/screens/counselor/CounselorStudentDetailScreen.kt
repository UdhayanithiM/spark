package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sparkapp.network.ReferralResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorStudentDetailScreen(
    student: ReferralResponse,
    counselorId: String,
    onNavigateBack: () -> Unit,
    viewModel: CounselorViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Student Info", "Chat")
    val displayStudent = viewModel.activeStudent ?: student

    LaunchedEffect(student.id) {
        viewModel.startChatPolling(student.id)
        viewModel.startLiveStudentSync(counselorId, student.id)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopChatPolling()
            viewModel.stopLiveStudentSync()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(displayStudent.name ?: "Student Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- TABS ---
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // --- CONTENT ---
            when (selectedTab) {
                0 -> StudentInfoTab(displayStudent)
                1 -> StudentChatTab(displayStudent, counselorId, viewModel)
            }
        }
    }
}

// --- INFO TAB ---
@Composable
fun StudentInfoTab(student: ReferralResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader("Personal Information")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Unique ID Display
                    DetailRow("Student ID", student.uniqueId)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow("Age", student.age)
                    DetailRow("Gender", student.gender)
                    DetailRow("Class/Standard", student.standard)
                    DetailRow("Address", student.address)
                }
            }
        }

        item {
            SectionHeader("Referral Details")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Reason", student.reason)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Behavior", student.behavior)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Academic", student.academic)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Disciplinary", student.disciplinary)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Special Needs", student.specialNeed)
                }
            }
        }

        // --- Doctor Response Section ---
        item {
            SectionHeader("Doctor's Feedback")
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (student.doctorSuggestion.isNullOrEmpty() && student.precautions.isNullOrEmpty())
                        Color(0xFFFFF3E0)
                    else
                        Color(0xFFE8F5E9)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (student.doctorSuggestion.isNullOrEmpty() && student.precautions.isNullOrEmpty()) {
                        Text(
                            "Pending Doctor Review...",
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color(0xFFEF6C00)
                        )
                    } else {
                        // Show Suggestions
                        if (!student.doctorSuggestion.isNullOrEmpty()) {
                            Text("Suggestions:", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text(student.doctorSuggestion, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Show Precautions
                        if (!student.precautions.isNullOrEmpty()) {
                            Text("Precautions:", fontWeight = FontWeight.Bold, color = Color(0xFFC62828)) // Red for Caution
                            Text(student.precautions, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

// --- CHAT TAB ---
@Composable
fun StudentChatTab(
    student: ReferralResponse,
    counselorId: String,
    viewModel: CounselorViewModel
) {
    var messageText by remember { mutableStateOf("") }
    val messages = viewModel.currentMessages
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == counselorId

                // Chat Bubble Alignment
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 12.dp, topEnd = 12.dp,
                            bottomStart = if (isMe) 12.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) Color(0xFFDCF8C6) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = msg.message ?: "",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp ?: "",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.End),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        // Input Area
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(counselorId, student.id, messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// --- Helpers ---
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column(modifier = Modifier.padding(bottom = 6.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}