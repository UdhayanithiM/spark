package com.example.sparkapp.ui.screens.pretest

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// Theme Colors
private val BackgroundGray = Color(0xFFF5F7FA)
private val PrimaryPurple = SparkAppPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreTestScreen(
    navController: NavController,
    viewModel: PreTestViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.loadTestStatus(context)
    }

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
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pre-Test Assessment", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is PreTestUiState.Loading -> LoadingScreen()
                is PreTestUiState.Completed -> CompletedScreen(navController)
                is PreTestUiState.Error -> ErrorScreen(state.message) {
                    viewModel.checkTestStatus(context)
                }
                is PreTestUiState.Quiz -> QuizScreen(
                    questions = state.questions,
                    userAnswers = viewModel.userAnswers,
                    onAnswerSelected = { qIndex, aIndex ->
                        viewModel.onAnswerSelected(qIndex, aIndex)
                    },
                    onSubmit = { viewModel.submitQuiz() }
                )
            }
        }

        if (showDialog != null) {
            val (score, total) = showDialog!!
            ScoreDialog(
                score = score,
                total = total,
                onDismiss = {
                    showDialog = null
                    navController.navigate(AppRoutes.MODULE_PAGE) {
                        popUpTo(AppRoutes.PRE_TEST) { inclusive = true }
                    }
                }
            )
        }
    }
}

// --- STATES ---

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PrimaryPurple)
            Spacer(Modifier.height(16.dp))
            Text("Checking test status...", color = Color.Gray)
        }
    }
}

@Composable
fun CompletedScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.CheckCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF4CAF50)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Assessment Complete",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "You have already submitted your pre-test. Proceed to learning modules.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        navController.navigate(AppRoutes.MODULE_PAGE) {
                            popUpTo(AppRoutes.PRE_TEST) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Go to Learning Module", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, onTryAgain: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE53935)
            )
            Spacer(Modifier.height(16.dp))
            Text("Something went wrong", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(message, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onTryAgain,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}

// --- QUIZ LIST ---

@Composable
fun QuizScreen(
    questions: List<Question>,
    userAnswers: Map<Int, Int?>,
    onAnswerSelected: (Int, Int) -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Answer all questions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

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
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("SUBMIT ASSESSMENT", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

// --- MODERN QUESTION CARD ---

@Composable
fun QuestionCard(
    questionIndex: Int,
    question: Question,
    selectedAnswer: Int?,
    onAnswerSelected: (Int, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Icon + Question
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Outlined.Quiz,
                    contentDescription = null,
                    tint = PrimaryPurple,
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

            Spacer(Modifier.height(20.dp))

            // Options List
            question.options.forEachIndexed { answerIndex, optionText ->
                val isSelected = selectedAnswer == answerIndex

                // Animation for smooth selection
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) PrimaryPurple.copy(alpha = 0.1f) else Color.Transparent,
                    animationSpec = tween(300)
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) PrimaryPurple else Color.LightGray.copy(alpha = 0.5f),
                    animationSpec = tween(300)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAnswerSelected(questionIndex, answerIndex) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, borderColor),
                    color = backgroundColor
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) PrimaryPurple else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) PrimaryPurple else Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreDialog(
    score: Int,
    total: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Result", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Your Score", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Text(
                    "$score / $total",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Continue to Modules")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}