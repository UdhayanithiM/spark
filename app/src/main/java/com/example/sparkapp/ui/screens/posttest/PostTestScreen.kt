package com.example.sparkapp.ui.screens.posttest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// Import from OUTLINED, which you added in Step 1
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle // Import for TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// --- Import AppRoutes ---
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.data.PostTestQuestion
import com.example.sparkapp.ui.theme.ButtonColor // Import for ButtonColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTestScreen(navController: NavController) {
    val viewModel: PostTestViewModel = viewModel()
    val testStatus by viewModel.testStatus.collectAsState()
    val submissionStatus by viewModel.submissionStatus.collectAsState()

    // Handle Submission Dialogs
    when (submissionStatus) {
        SubmissionStatus.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        SubmissionStatus.SUCCESS -> {
            AlertDialog(
                onDismissRequest = { /* Disallow dismissing */ },
                title = { Text("Success") },
                text = { Text("Post-test submitted successfully!") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetSubmissionStatus()
                        // --- Build the correct route with the userId ---
                        val route = "${AppRoutes.COUNSELOR_HOME}/${viewModel.userId}"
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = true }
                        }
                    }) { Text("OK") }
                }
            )
        }
        SubmissionStatus.ERROR -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetSubmissionStatus() },
                title = { Text("Error") },
                text = { Text("Failed to submit. Please try again.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetSubmissionStatus() }) { Text("OK") }
                }
            )
        }
        SubmissionStatus.IDLE -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post-Test") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ButtonColor,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // This `when` statement replicates the Flutter `Builder`
            when (testStatus) {
                TestStatus.LOADING -> LoadingScreen()
                // --- Pass the viewModel to CompletedScreen ---
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
    // ... (This function is correct, no changes needed) ...
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false // Replicates NeverScrollableScrollPhysics
    ) { page ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (page == 0) {
                // Page 1: Section 1
                item {
                    Text(
                        "Section 1: Knowledge",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                itemsIndexed(viewModel.section1) { index, question ->
                    val globalIndex = viewModel.questions.indexOf(question)
                    QuestionCard(
                        question = question,
                        selectedOption = viewModel.selectedOptions[globalIndex],
                        onOptionSelected = { optionIndex ->
                            viewModel.onOptionSelected(globalIndex, optionIndex)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                    ) {
                        Text("Next")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            } else {
                // Page 2: Section 2 & 3
                item {
                    Text(
                        "Section 2: Attitude",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                itemsIndexed(viewModel.section2) { index, question ->
                    val globalIndex = viewModel.questions.indexOf(question)
                    QuestionCard(
                        question = question,
                        selectedOption = viewModel.selectedOptions[globalIndex],
                        onOptionSelected = { optionIndex ->
                            viewModel.onOptionSelected(globalIndex, optionIndex)
                        }
                    )
                }
                item {
                    Text(
                        "Section 3: Practice",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
                    )
                }
                itemsIndexed(viewModel.section3) { index, question ->
                    val globalIndex = viewModel.questions.indexOf(question)
                    QuestionCard(
                        question = question,
                        selectedOption = viewModel.selectedOptions[globalIndex],
                        onOptionSelected = { optionIndex ->
                            viewModel.onOptionSelected(globalIndex, optionIndex)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.submitPostTest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                    ) {
                        Text("Submit")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
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
    // ... (This function is correct, no changes needed) ...
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = question.questionText,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = question.answerIndex == index

                val backgroundColor = when {
                    isSelected && isCorrect -> Color.Green.copy(alpha = 0.2f)
                    isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
                    else -> Color.White
                }

                // Use if/else for Kotlin
                val borderColor = if (isSelected) {
                    if (isCorrect) Color.Green else Color.Red
                } else {
                    Color.Black.copy(alpha = 0.5f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOptionSelected(index) },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, borderColor),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    // ... (This function is correct, no changes needed) ...
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(20.dp))
            Text("Checking test status...", style = TextStyle(fontSize = 16.sp))
        }
    }
}

@Composable
private fun CompletedScreen(
    navController: NavController,
    // --- Accept the viewModel to get the userId ---
    viewModel: PostTestViewModel
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                Icons.Outlined.CheckCircleOutline, // Use Icons.Outlined
                contentDescription = "Completed",
                modifier = Modifier.size(80.dp),
                tint = Color.Green // Use Color.Green
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Test Already Completed",
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "You have already submitted your post-test.",
                style = TextStyle(fontSize = 16.sp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    // --- Build the correct route with the userId ---
                    val route = "${AppRoutes.COUNSELOR_HOME}/${viewModel.userId}"
                    navController.navigate(route) {
                        popUpTo(route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
            ) {
                Text("Go Back Home", style = TextStyle(fontSize = 18.sp))
            }
        }
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    // ... (This function is correct, no changes needed) ...
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                Icons.Outlined.ErrorOutline, // Use Icons.Outlined
                contentDescription = "Error",
                modifier = Modifier.size(80.dp),
                tint = Color.Red // Use Color.Red
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Error",
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                "Could not verify your test status. Please check your internet connection and try again.",
                style = TextStyle(fontSize = 16.sp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
            ) {
                Text("Try Again", style = TextStyle(fontSize = 18.sp))
            }
        }
    }
}