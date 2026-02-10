package com.example.sparkapp.ui.screens.scenario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// --- THEME COLORS ---
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioScreen(
    navController: NavController,
    viewModel: ScenarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Case Scenarios",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryLightBlue
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState.testStatus) {
                "loading" -> LoadingView()
                "completed" -> CompletedView(
                    onNavigate = { navController.navigate("checklist") { popUpTo("counselor_home") } }
                )
                "error" -> ErrorView(
                    onRetry = { viewModel.checkTestStatus() }
                )
                "not_completed" -> ScenarioQuizView(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigate = { navController.navigate("checklist") { popUpTo("counselor_home") } }
                )
            }
        }
    }
}

/**
 * The main quiz UI
 */
@Composable
private fun ScenarioQuizView(
    uiState: ScenarioUiState,
    viewModel: ScenarioViewModel,
    onNavigate: () -> Unit
) {
    val currentScenario = uiState.scenarios[uiState.currentScenarioIndex]
    val scrollState = rememberScrollState()
    val progress = (uiState.currentScenarioIndex + 1).toFloat() / uiState.scenarios.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // --- Progress Bar ---
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = PrimaryLightBlue,
            trackColor = Color.LightGray.copy(alpha = 0.5f),
        )

        Column(modifier = Modifier.padding(20.dp)) {

            // --- Header: Count ---
            Text(
                text = "Scenario ${uiState.currentScenarioIndex + 1} of ${uiState.scenarios.size}",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- Scenario Title Card ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Outlined.Psychology,
                        contentDescription = null,
                        tint = PrimaryLightBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = currentScenario.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Questions List ---
            Text(
                text = "Your Analysis",
                style = MaterialTheme.typography.titleSmall,
                color = PrimaryLightBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            currentScenario.questions.forEachIndexed { index, question ->
                QuestionResponseCard(
                    question = question,
                    response = currentScenario.responses[index],
                    onResponseChange = { viewModel.onResponseChanged(index, it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Submit / Next Button ---
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLightBlue)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.submitCurrentScenario(onAllScenariosFinished = onNavigate)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightBlue),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    val isLastScenario = uiState.currentScenarioIndex == uiState.scenarios.size - 1
                    Text(
                        text = if (isLastScenario) "SUBMIT SCENARIOS" else "NEXT SCENARIO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (!isLastScenario) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null)
                    }
                }
            }

            // Error Message
            uiState.submissionMessage?.let {
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun QuestionResponseCard(
    question: String,
    response: String,
    onResponseChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Question Text
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Response Input
            OutlinedTextField(
                value = response,
                onValueChange = onResponseChange,
                label = { Text("Write your response...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryLightBlue,
                    focusedLabelColor = PrimaryLightBlue,
                    unfocusedContainerColor = Color(0xFFFAFAFA),
                    focusedContainerColor = Color.White
                ),
                leadingIcon = {
                    Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            )
        }
    }
}

// --- STATES ---

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PrimaryLightBlue)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Checking test status...", color = Color.Gray)
        }
    }
}

@Composable
private fun CompletedView(onNavigate: () -> Unit) {
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
                    contentDescription = "Completed",
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Assessment Complete",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "You have already submitted your case scenarios.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onNavigate,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightBlue),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed to Checklist", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ErrorView(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = "Error",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE53935)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Connection Error",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Could not verify your test status. Please check your internet connection.",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}