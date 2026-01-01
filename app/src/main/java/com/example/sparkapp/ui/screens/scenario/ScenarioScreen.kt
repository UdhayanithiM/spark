package com.example.sparkapp.ui.screens.scenario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioScreen(
    navController: NavController,
    viewModel: ScenarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Case Based Scenarios") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        // This 'when' block matches the logic in your Flutter file's Builder
        when (uiState.testStatus) {
            "loading" -> LoadingView(modifier = Modifier.padding(padding))
            "completed" -> CompletedView(
                modifier = Modifier.padding(padding),
                onNavigate = { navController.navigate("checklist") { popUpTo("counselor_home") } }
            )
            "error" -> ErrorView(
                modifier = Modifier.padding(padding),
                onRetry = { viewModel.checkTestStatus() }
            )
            "not_completed" -> ScenarioQuizView(
                modifier = Modifier.padding(padding),
                uiState = uiState,
                viewModel = viewModel,
                onNavigate = { navController.navigate("checklist") { popUpTo("counselor_home") } }
            )
        }
    }
}

/**
 * The main quiz UI, shown when testStatus == "not_completed"
 */
@Composable
private fun ScenarioQuizView(
    modifier: Modifier = Modifier,
    uiState: ScenarioUiState,
    viewModel: ScenarioViewModel,
    onNavigate: () -> Unit
) {
    val currentScenario = uiState.scenarios[uiState.currentScenarioIndex]
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            "Case Scenario ${uiState.currentScenarioIndex + 1} of ${uiState.scenarios.size}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            currentScenario.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loop through the 4 questions and 4 responses
        currentScenario.questions.forEachIndexed { index, question ->
            Text(question, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = currentScenario.responses[index],
                onValueChange = { viewModel.onResponseChanged(index, it) },
                label = { Text("Response:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    viewModel.submitCurrentScenario(
                        onAllScenariosFinished = onNavigate
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                val isLastScenario = uiState.currentScenarioIndex == uiState.scenarios.size - 1
                Text(if (isLastScenario) "Submit" else "Next", fontSize = 18.sp)
            }
        }

        uiState.submissionMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * The UI for testStatus == "loading"
 */
@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Checking test status...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/**
 * The UI for testStatus == "completed"
 */
@Composable
private fun CompletedView(modifier: Modifier = Modifier, onNavigate: () -> Unit) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, "Completed", modifier = Modifier.size(80.dp), tint = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(20.dp))
            Text("Test Already Completed", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Text("You have already submitted your case scenarios.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = onNavigate) {
                Text("Go to Checklist", fontSize = 18.sp)
            }
        }
    }
}

/**
 * The UI for testStatus == "error"
 */
@Composable
private fun ErrorView(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Error, "Error", modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Error", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Could not verify your test status. Please check your internet connection and try again.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = onRetry) {
                Text("Try Again", fontSize = 18.sp)
            }
        }
    }
}