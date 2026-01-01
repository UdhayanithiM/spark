package com.example.sparkapp.ui.screens.doctor

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    var selectedTab by remember { mutableStateOf(0) }
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
                }
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
    var isSaving by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Basic Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            Text("Age: ${student.age ?: "N/A"}")
            Text("Standard: ${student.standard ?: "N/A"}")

            Spacer(Modifier.height(16.dp))
            Text("Doctor Suggestion", fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = suggestionText,
                onValueChange = { suggestionText = it },
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    isSaving = true
                    viewModel.submitSuggestion(student.id, suggestionText) {
                        isSaving = false
                        Toast.makeText(
                            context,
                            if (it) "Saved" else "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}

/* -------------------------------------------------- */
/* CHAT TAB */
/* -------------------------------------------------- */
@Composable
fun StudentChatTab(student: ReferralResponse, viewModel: DoctorViewModel) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)

    val doctorId = prefs.getString("user_id", "0") ?: "0"
    val counselorId = prefs.getString("counselor_id", "0") ?: "0"

    var messageText by remember { mutableStateOf("") }
    val messages = viewModel.currentStudentMessages

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == doctorId
                Text(
                    msg.message ?: "",
                    color = if (isMe) MaterialTheme.colorScheme.primary else Color.Black
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                if (messageText.isNotBlank()) {
                    viewModel.sendStudentMessage(
                        referralId = student.id,
                        text = messageText,
                        doctorId = doctorId,
                        counselorId = counselorId
                    )
                    messageText = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
            }
        }
    }
}
