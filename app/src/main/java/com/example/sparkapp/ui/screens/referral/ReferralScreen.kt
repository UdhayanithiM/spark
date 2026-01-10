package com.example.sparkapp.ui.screens.referral

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.ui.theme.ReferralAppBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    navController: NavController,
    viewModel: ReferralViewModel = viewModel()
) {
    val submissionStatus by viewModel.submissionStatus.collectAsState()
    val scrollState = rememberScrollState()

    // --- Handle Status Dialogs ---
    if (submissionStatus == SubmissionStatus.LOADING) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Sending...") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = {}
        )
    } else if (submissionStatus == SubmissionStatus.SUCCESS) {
        AlertDialog(
            onDismissRequest = { viewModel.resetStatus() },
            title = { Text("Success") },
            text = { Text("Referral submitted successfully.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetStatus()
                    navController.popBackStack()
                }) {
                    Text("OK")
                }
            }
        )
    } else if (submissionStatus == SubmissionStatus.ERROR) {
        AlertDialog(
            onDismissRequest = { viewModel.resetStatus() },
            title = { Text("Error") },
            text = { Text("Failed to send referral. Please try again.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetStatus() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Student Referral", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ReferralAppBarColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {

            // --- Section 1: Student Identity ---
            SectionLabel("Student Identity")

            OutlinedTextField(
                value = viewModel.uniqueId.value,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        viewModel.uniqueId.value = input
                    }
                },
                label = { Text("Unique ID (Number Only)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Section 2: Basic Details ---
            SectionLabel("Basic Details")

            StandardTextField(value = viewModel.name.value, onValueChange = { viewModel.name.value = it }, label = "Full Name")
            StandardTextField(value = viewModel.age.value, onValueChange = { viewModel.age.value = it }, label = "Age", keyboardType = KeyboardType.Number)
            StandardTextField(value = viewModel.standard.value, onValueChange = { viewModel.standard.value = it }, label = "Class/Standard")
            StandardTextField(value = viewModel.address.value, onValueChange = { viewModel.address.value = it }, label = "Address", singleLine = false, maxLines = 3)

            Spacer(modifier = Modifier.height(24.dp))

            // --- Section 3: Referral Information ---
            SectionLabel("Referral Information")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StandardTextField(value = viewModel.reason.value, onValueChange = { viewModel.reason.value = it }, label = "Reason for Referral", singleLine = false, maxLines = 3)
                    StandardTextField(value = viewModel.behavior.value, onValueChange = { viewModel.behavior.value = it }, label = "Behavioral Observations", singleLine = false, maxLines = 3)
                    StandardTextField(value = viewModel.academic.value, onValueChange = { viewModel.academic.value = it }, label = "Academic Performance", singleLine = false, maxLines = 3)
                    StandardTextField(value = viewModel.disciplinary.value, onValueChange = { viewModel.disciplinary.value = it }, label = "Disciplinary History", singleLine = false, maxLines = 3)
                    StandardTextField(value = viewModel.specialNeed.value, onValueChange = { viewModel.specialNeed.value = it }, label = "Special Needs Concern", singleLine = false, maxLines = 3)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Submit Button ---
            Button(
                onClick = { viewModel.sendReferral() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ReferralAppBarColor),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text = "SUBMIT REFERRAL",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- Helper Composables for Professional UI ---

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
private fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}