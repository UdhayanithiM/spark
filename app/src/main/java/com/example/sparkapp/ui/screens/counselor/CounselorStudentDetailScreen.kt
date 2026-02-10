package com.example.sparkapp.ui.screens.counselor

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sparkapp.network.ReferralResponse

// Theme Colors
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)
private val ChatBubbleOwn = Color(0xFFE1F5FE) // Light Blue
private val ChatBubbleOther = Color(0xFFFFFFFF) // White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorStudentDetailScreen(
    student: ReferralResponse,
    counselorId: String,
    onNavigateBack: () -> Unit,
    viewModel: CounselorViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Student Info", "Doctor Chat")
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
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Referral Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryLightBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Modern Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryLightBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryLightBlue
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

            // Content
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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- 1. Header Card ---
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
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryLightBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name?.take(1)?.uppercase() ?: "?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLightBlue
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

        // --- 2. Personal Info ---
        item {
            InfoSectionCard(title = "Student Profile") {
                InfoRow(Icons.Default.Cake, "Age", "${student.age} Years")
                InfoRow(Icons.Default.Person, "Gender", student.gender ?: "N/A")
                InfoRow(Icons.Default.School, "Class", student.standard ?: "N/A")
                InfoRow(Icons.Default.LocationOn, "Address", student.address ?: "N/A")
            }
        }

        // --- 3. Referral Details ---
        item {
            InfoSectionCard(title = "Referral Report") {
                InfoRow(Icons.Default.ReportProblem, "Reason", student.reason ?: "N/A")
                InfoRow(Icons.Default.Psychology, "Behavior", student.behavior ?: "N/A")
                InfoRow(Icons.Default.MenuBook, "Academic", student.academic ?: "N/A")
                InfoRow(Icons.Default.Warning, "Disciplinary", student.disciplinary ?: "N/A")
                InfoRow(Icons.Default.Healing, "Special Needs", student.specialNeed ?: "N/A")
            }
        }

        // --- 4. Doctor Feedback (Actionable) ---
        item {
            val hasFeedback = !student.doctorSuggestion.isNullOrEmpty() || !student.precautions.isNullOrEmpty()
            val cardColor = if (hasFeedback) Color(0xFFE8F5E9) else Color(0xFFFFF3E0) // Green vs Orange tint
            val titleColor = if (hasFeedback) Color(0xFF2E7D32) else Color(0xFFEF6C00)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (hasFeedback) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            null,
                            tint = titleColor
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (hasFeedback) "Doctor's Feedback" else "Pending Review",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    if (!hasFeedback) {
                        Text(
                            "The doctor has not yet provided suggestions or precautions for this referral.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    } else {
                        if (!student.doctorSuggestion.isNullOrEmpty()) {
                            Text("Suggestion:", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(student.doctorSuggestion, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                        }
                        if (!student.precautions.isNullOrEmpty()) {
                            Text("Precautions:", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Text(student.precautions, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
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
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE5E9F0)) // Chat background
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == counselorId
                ChatBubble(message = msg.message ?: "", time = msg.timestamp ?: "", isMe = isMe)
            }
        }

        // Input Area
        Surface(color = Color.White, shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryLightBlue,
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
                            viewModel.sendMessage(counselorId, student.id, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).background(PrimaryLightBlue, CircleShape)
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

// --- HELPERS ---

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
                color = PrimaryLightBlue
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
                Text(text = message, color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = time, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}