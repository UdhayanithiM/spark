package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send // Fixed Import
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorStudentDetailScreen(
    student: ReferralResponse,
    counselorId: String,
    onNavigateBack: () -> Unit,
    viewModel: CounselorViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messages = viewModel.currentMessages
    val listState = rememberLazyListState()

    val displayStudent = viewModel.activeStudent ?: student

    LaunchedEffect(student.id) {
        viewModel.startChatPolling(student.id)
        viewModel.startLiveStudentSync(counselorId, student.id)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
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
                title = { Text(displayStudent.name ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // Fixed Deprecated Icon
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

            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Age: ${displayStudent.age} | Class: ${displayStudent.standard}")
                    Text("Reason: ${displayStudent.reason}")

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Doctor's Suggestion:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    if (displayStudent.doctorSuggestion.isNullOrEmpty()) {
                        Text("Waiting for doctor...", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    } else {
                        Text(displayStudent.doctorSuggestion, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Text(
                "Chat with Doctor",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(8.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == counselorId
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMe) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier.padding(4.dp).widthIn(max = 250.dp)
                        ) {
                            Text(
                                text = msg.message ?: "",
                                color = if (isMe) Color.White else Color.Black,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type...") }
                )
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(counselorId, displayStudent.id, messageText)
                        messageText = ""
                    }
                }) {
                    // Fixed Deprecated Icon
                    Icon(Icons.AutoMirrored.Filled.Send, "Send")
                }
            }
        }
    }
}