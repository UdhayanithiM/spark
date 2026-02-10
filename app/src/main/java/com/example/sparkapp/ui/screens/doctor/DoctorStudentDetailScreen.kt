package com.example.sparkapp.ui.screens.doctor

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sparkapp.network.ReferralResponse

// Theme Colors
private val PrimaryPurple = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)
private val ChatBubbleOwn = Color(0xFFE1BEE7) // Light Purple
private val ChatBubbleOther = Color(0xFFFFFFFF) // White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorStudentDetailScreen(
    student: ReferralResponse,
    onNavigateBack: () -> Unit,
    viewModel: DoctorViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Consultation Chat")

    LaunchedEffect(student.id) {
        viewModel.startChatPollingForStudent(student.id)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopChatPolling() }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Student Assessment", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Modern Tabs
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
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
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
/* TAB 1: STUDENT INFO (Modernized) */
/* -------------------------------------------------- */
@Composable
fun StudentInfoTab(student: ReferralResponse, viewModel: DoctorViewModel) {
    val context = LocalContext.current
    var suggestionText by remember { mutableStateOf(student.doctorSuggestion ?: "") }
    var precautionsText by remember { mutableStateOf(student.precautions ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Header Card ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
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
                            .background(PrimaryPurple.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name?.take(1)?.uppercase() ?: "?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = student.name ?: "Unknown",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ID: ${student.uniqueId ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // --- Personal & Academic Info ---
        item {
            InfoSectionCard(title = "Student Profile") {
                InfoRow(Icons.Default.Cake, "Age", "${student.age} Years")
                InfoRow(Icons.Default.Person, "Gender", student.gender ?: "N/A")
                InfoRow(Icons.Default.School, "Class", student.standard ?: "N/A")
                InfoRow(Icons.Default.LocationOn, "Address", student.address ?: "N/A")
            }
        }

        // --- Referral Details ---
        item {
            InfoSectionCard(title = "Referral Report") {
                InfoRow(Icons.Default.ReportProblem, "Reason", student.reason ?: "N/A")
                InfoRow(Icons.Default.Psychology, "Behavior", student.behavior ?: "N/A")
                InfoRow(Icons.Default.MenuBook, "Academic", student.academic ?: "N/A")
                InfoRow(Icons.Default.Warning, "Disciplinary", student.disciplinary ?: "N/A")
                InfoRow(Icons.Default.Healing, "Special Needs", student.specialNeed ?: "N/A")
            }
        }

        // --- Doctor Actions ---
        item {
            Text(
                "Medical Action",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Suggestion Input
                    Text("Doctor's Suggestion", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = suggestionText,
                        onValueChange = { suggestionText = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Write your diagnosis/suggestion...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // Precautions Input
                    Text("Precautions / Safety", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFFD32F2F))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = precautionsText,
                        onValueChange = { precautionsText = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Required safety measures...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD32F2F),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            isSaving = true
                            viewModel.submitSuggestion(student.id, suggestionText, precautionsText) {
                                isSaving = false
                                Toast.makeText(context, if (it) "Saved Successfully" else "Failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("SAVE RECORDS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

/* -------------------------------------------------- */
/* TAB 2: CHAT (Modern WhatsApp Style) */
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
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE5E9F0)) // Chat background color
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == doctorId
                ChatBubble(message = msg.message ?: "", time = msg.timestamp ?: "", isMe = isMe)
            }
        }

        // Input Area
        Surface(
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendStudentMessage(student.id, messageText, doctorId, counselorId)
                            messageText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).background(PrimaryPurple, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/* -------------------------------------------------- */
/* HELPERS & COMPONENTS */
/* -------------------------------------------------- */

@Composable
fun InfoSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
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
fun ChatBubble(message: String, time: String, isMe: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = if (isMe) ChatBubbleOwn else ChatBubbleOther),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = message,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = time,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}