package com.example.sparkapp.ui.screens.posttest

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.data.PostTestQuestion
import com.example.sparkapp.ui.theme.ButtonColor
import kotlinx.coroutines.launch

// Define Theme Colors locally for consistency
private val PrimaryColor = ButtonColor // Using your existing ButtonColor
private val BackgroundGray = Color(0xFFF5F7FA)
private val CorrectGreen = Color(0xFF4CAF50)
private val WrongRed = Color(0xFFE53935)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTestScreen(navController: NavController) {
    val viewModel: PostTestViewModel = viewModel()
    val testStatus by viewModel.testStatus.collectAsState()
    val submissionStatus by viewModel.submissionStatus.collectAsState()

    // Handle Submission Dialogs
    if (submissionStatus == SubmissionStatus.LOADING) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Submitting") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            },
            confirmButton = {}
        )
    } else if (submissionStatus == SubmissionStatus.SUCCESS) {
        AlertDialog(
            onDismissRequest = { /* Disallow dismissing */ },
            title = { Text("Success") },
            text = { Text("Post-test submitted successfully!") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetSubmissionStatus()
                        val route = "${AppRoutes.COUNSELOR_HOME}/${viewModel.userId}"
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("OK") }
            }
        )
    } else if (submissionStatus == SubmissionStatus.ERROR) {
        AlertDialog(
            onDismissRequest = { viewModel.resetSubmissionStatus() },
            title = { Text("Error") },
            text = { Text("Failed to submit. Please try again.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetSubmissionStatus() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Post-Test Assessment", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (testStatus) {
                TestStatus.LOADING -> LoadingScreen()
                TestStatus.COMPLETED -> CompletedScreen(navController, viewModel)
                TestStatus.ERROR -> ErrorScreen(onRetry = { viewModel.checkTestStatus() })
                TestStatus.NOT_COMPLETED -> QuizPager(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QuizPager(viewModel: PostTestViewModel) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Progress Indicator ---
        LinearProgressIndicator(
            progress = { if (pagerState.currentPage == 0) 0.5f else 1.0f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = PrimaryColor,
            trackColor = Color.LightGray.copy(alpha = 0.5f),
        )

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (page == 0) {
                    // --- Page 1 ---
                    item { SectionHeader("Section 1: Knowledge") }
                    itemsIndexed(viewModel.section1) { _, question ->
                        val globalIndex = viewModel.questions.indexOf(question)
                        QuestionCard(
                            question = question,
                            selectedOption = viewModel.selectedOptions[globalIndex],
                            onOptionSelected = { viewModel.onOptionSelected(globalIndex, it) }
                        )
                    }
                    item {
                        NavigationButton(text = "Next Section") {
                            scope.launch { pagerState.animateScrollToPage(1) }
                        }
                    }
                } else {
                    // --- Page 2 ---
                    item { SectionHeader("Section 2: Attitude") }
                    itemsIndexed(viewModel.section2) { _, question ->
                        val globalIndex = viewModel.questions.indexOf(question)
                        QuestionCard(
                            question = question,
                            selectedOption = viewModel.selectedOptions[globalIndex],
                            onOptionSelected = { viewModel.onOptionSelected(globalIndex, it) }
                        )
                    }

                    item { SectionHeader("Section 3: Practice") }
                    itemsIndexed(viewModel.section3) { _, question ->
                        val globalIndex = viewModel.questions.indexOf(question)
                        QuestionCard(
                            question = question,
                            selectedOption = viewModel.selectedOptions[globalIndex],
                            onOptionSelected = { viewModel.onOptionSelected(globalIndex, it) }
                        )
                    }

                    item {
                        NavigationButton(text = "Submit Assessment") {
                            viewModel.submitPostTest()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: PostTestQuestion,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Question Text
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = question.answerIndex == index

                // Determine Visual State
                val showResult = selectedOption != null // Only show green/red if user clicked something

                // Logic:
                // 1. If I selected this, and it's correct -> Green
                // 2. If I selected this, and it's wrong -> Red
                // 3. (Optional) If I selected WRONG, highlight the CORRECT one -> Green (Optional UX)

                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isCorrect) CorrectGreen else WrongRed
                    } else Color.LightGray.copy(alpha = 0.5f),
                    animationSpec = tween(300)
                )

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isCorrect) CorrectGreen.copy(alpha = 0.1f) else WrongRed.copy(alpha = 0.1f)
                    } else Color.Transparent,
                    animationSpec = tween(300)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = selectedOption == null) { // Disable change after selection? Or keep enabled
                            onOptionSelected(index)
                        },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, borderColor),
                    color = backgroundColor
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Radio/Status Icon
                        if (isSelected) {
                            if (isCorrect) {
                                Icon(Icons.Default.CheckCircle, null, tint = CorrectGreen)
                            } else {
                                Icon(Icons.Default.Close, null, tint = WrongRed)
                            }
                        } else {
                            // Empty Radio Circle
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .border(1.5.dp, Color.Gray, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// --- Status Screens ---

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PrimaryColor)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Checking status...", color = Color.Gray)
        }
    }
}

@Composable
private fun CompletedScreen(navController: NavController, viewModel: PostTestViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(40.dp)
            ) {
                Icon(
                    Icons.Outlined.CheckCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = CorrectGreen
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Assessment Complete",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "You have successfully submitted your post-test.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val route = "${AppRoutes.COUNSELOR_HOME}/${viewModel.userId}"
                        navController.navigate(route) { popUpTo(route) { inclusive = true } }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return to Dashboard", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = WrongRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Connection Error",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Unable to verify test status.",
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Try Again")
            }
        }
    }
}

// --- Helpers ---

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = PrimaryColor,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun NavigationButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}