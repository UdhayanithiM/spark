package com.example.sparkapp.ui.screens.pretest

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
// --- FIX 2: Import from .outlined ---
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
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
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.data.Question
import com.example.sparkapp.ui.theme.SparkAppPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreTestScreen(
    navController: NavController,
    viewModel: PreTestViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Load the test status when the screen first appears
    LaunchedEffect(key1 = true) {
        viewModel.loadTestStatus(context)
    }

    // Listen for events (like showing the dialog)
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PreTestUiEvent.ShowScoreDialog -> {
                    showDialog = event.score to event.total
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pre-Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SparkAppPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        // This is your 'body: Builder(...)'
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is PreTestUiState.Loading -> LoadingScreen()
                is PreTestUiState.Completed -> CompletedScreen(navController)
                is PreTestUiState.Error -> ErrorScreen(state.message) {
                    viewModel.checkTestStatus(context) // "Try Again"
                }
                is PreTestUiState.Quiz -> QuizScreen(
                    questions = state.questions,
                    userAnswers = viewModel.userAnswers,
                    onAnswerSelected = { qIndex, aIndex ->
                        viewModel.onAnswerSelected(qIndex, aIndex)
                    },
                    onSubmit = {
                        // --- FIX 1: Removed 'context' parameter ---
                        viewModel.submitQuiz()
                    }
                )
            }
        }

        // This is your 'showDialog(...)'
        if (showDialog != null) {
            val (score, total) = showDialog!!
            ScoreDialog(
                score = score,
                total = total,
                onDismiss = {
                    showDialog = null
                    // Navigate to ModulePage
                    navController.navigate(AppRoutes.MODULE_PAGE) {
                        popUpTo(AppRoutes.PRE_TEST) { inclusive = true }
                    }
                }
            )
        }
    }
}

// --- The 4 UI States ---

// This is your '_buildLoadingScreen'
@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(20.dp))
            Text("Checking test status...", fontSize = 16.sp)
        }
    }
}

// This is your '_buildCompletedScreen'
@Composable
fun CompletedScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // --- FIX 2: Changed to .Outlined ---
            Icon(Icons.Outlined.CheckCircleOutline, contentDescription = "Test Completed", modifier = Modifier.size(80.dp), tint = Color.Green)
            Spacer(Modifier.height(20.dp))
            Text("Test Already Completed", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text("You have already submitted your pre-test. You can proceed to the learning materials.", fontSize = 16.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = {
                    navController.navigate(AppRoutes.MODULE_PAGE) {
                        popUpTo(AppRoutes.PRE_TEST) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 24.dp)
            ) {
                Text("Go to Learning Module", fontSize = 18.sp)
            }
        }
    }
}

// This is your '_buildErrorScreen'
@Composable
fun ErrorScreen(message: String, onTryAgain: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // --- FIX 2: Changed to .Outlined ---
            Icon(Icons.Outlined.ErrorOutline, contentDescription = "Error", modifier = Modifier.size(80.dp), tint = Color.Red)
            Spacer(Modifier.height(20.dp))
            Text("Error", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(message, fontSize = 16.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = onTryAgain,
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 24.dp)
            ) {
                Text("Try Again", fontSize = 18.sp)
            }
        }
    }
}

// This is your '_buildQuizScreen'
@Composable
fun QuizScreen(
    questions: List<Question>,
    userAnswers: Map<Int, Int?>,
    onAnswerSelected: (Int, Int) -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(questions) { index, question ->
            QuestionCard(
                questionIndex = index,
                question = question,
                selectedAnswer = userAnswers[index],
                onAnswerSelected = onAnswerSelected
            )
        }
        item {
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 24.dp)
            ) {
                Text("Submit", fontSize = 18.sp)
            }
        }
    }
}

// This is your 'Card' with 'RadioListTile'
@Composable
fun QuestionCard(
    questionIndex: Int,
    question: Question,
    selectedAnswer: Int?,
    onAnswerSelected: (Int, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = question.questionText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(10.dp))

            // This is your 'for' loop for options
            question.options.forEachIndexed { answerIndex, optionText ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedAnswer == answerIndex),
                            onClick = { onAnswerSelected(questionIndex, answerIndex) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedAnswer == answerIndex),
                        onClick = { onAnswerSelected(questionIndex, answerIndex) }
                    )
                    Text(text = optionText, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

// This is your 'AlertDialog'
@Composable
fun ScoreDialog(
    score: Int,
    total: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quiz Completed") },
        text = { Text("Your score is $score out of $total") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
