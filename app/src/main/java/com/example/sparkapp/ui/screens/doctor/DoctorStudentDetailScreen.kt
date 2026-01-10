package com.example.sparkapp.ui.screens.doctor

import android.content.Context // <-- ADDED THIS IMPORT
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sparkapp.network.ReferralResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorStudentDetailScreen(
    student: ReferralResponse,
    onNavigateBack: () -> Unit,
    viewModel: DoctorViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Student Info", "Chat")

    LaunchedEffect(student.id) {
        viewModel.startChatPollingForStudent(student.id)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopChatPolling() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(student.name ?: "Student Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> StudentInfoTab(student, viewModel)
                1 -> StudentChatTab(student, viewModel)
            }
        }
    }
}

/* -------------------------------------------------- */
/* STUDENT INFO TAB */
/* -------------------------------------------------- */
@Composable
fun StudentInfoTab(student: ReferralResponse, viewModel: DoctorViewModel) {
    val context = LocalContext.current
    var suggestionText by remember { mutableStateOf(student.doctorSuggestion ?: "") }
    var precautionsText by remember { mutableStateOf(student.precautions ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Personal Info ---
        item {
            SectionHeader("Personal Information")
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Unique ID Display
                    DetailRow("Student ID", student.uniqueId)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    DetailRow("Name", student.name)
                    DetailRow("Age", student.age)
                    DetailRow("Gender", student.gender)
                    DetailRow("Standard", student.standard)
                    DetailRow("Address", student.address)
                }
            }
        }

        // --- Referral Details ---
        item {
            SectionHeader("Referral Details")
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
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

        // --- Doctor Actions ---
        item {
            SectionHeader("Doctor's Action")

            // Suggestion Box
            Text("Suggestions / Prescription", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            OutlinedTextField(
                value = suggestionText,
                onValueChange = { suggestionText = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Enter suggestion...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(Modifier.height(16.dp))

            // Precaution Box
            Text("Precautions / Safety Measures", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            OutlinedTextField(
                value = precautionsText,
                onValueChange = { precautionsText = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Enter precautions...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.error,
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    isSaving = true
                    viewModel.submitSuggestion(student.id, suggestionText, precautionsText) {
                        isSaving = false
                        Toast.makeText(context, if (it) "Saved" else "Failed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Save All Details")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/* -------------------------------------------------- */
/* CHAT TAB (WhatsApp Style) */
/* -------------------------------------------------- */
@Composable
fun StudentChatTab(student: ReferralResponse, viewModel: DoctorViewModel) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
    val doctorId = prefs.getString("user_id", "0") ?: "0"
    val counselorId = student.counselorId ?: "0"

    var messageText by remember { mutableStateOf("") }
    val messages = viewModel.currentStudentMessages
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
                val isMe = msg.senderId == doctorId

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
                        viewModel.sendStudentMessage(
                            referralId = student.id,
                            text = messageText,
                            doctorId = doctorId,
                            counselorId = counselorId
                        )
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
            Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}