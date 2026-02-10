package com.example.sparkapp.ui.screens.referral

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// --- THEME COLORS ---
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    navController: NavController,
    viewModel: ReferralViewModel = viewModel()
) {
    val submissionStatus by viewModel.submissionStatus.collectAsState()
    val scrollState = rememberScrollState()

    // --- Status Dialogs ---
    if (submissionStatus == SubmissionStatus.LOADING) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Submitting...") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLightBlue)
                }
            },
            confirmButton = {},
            containerColor = Color.White
        )
    } else if (submissionStatus == SubmissionStatus.SUCCESS) {
        AlertDialog(
            onDismissRequest = { viewModel.resetStatus() },
            title = { Text("Referral Sent", fontWeight = FontWeight.Bold) },
            text = { Text("The student referral has been successfully submitted to the doctor.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetStatus()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightBlue)
                ) {
                    Text("Done")
                }
            },
            containerColor = Color.White
        )
    } else if (submissionStatus == SubmissionStatus.ERROR) {
        AlertDialog(
            onDismissRequest = { viewModel.resetStatus() },
            title = { Text("Submission Failed", color = Color.Red) },
            text = { Text("Could not connect to the server. Please check your internet and try again.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetStatus() }) {
                    Text("Try Again", color = Color.Red)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "New Referral",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- Section 1: Student Identity ---
            FormSection(title = "Student Identity") {
                ModernTextField(
                    value = viewModel.uniqueId.value,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) viewModel.uniqueId.value = input
                    },
                    label = "Unique ID (Number)",
                    icon = Icons.Default.Badge,
                    keyboardType = KeyboardType.Number
                )
            }

            // --- Section 2: Basic Details ---
            FormSection(title = "Personal Information") {
                ModernTextField(
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.value = it },
                    label = "Full Name",
                    icon = Icons.Default.Person
                )
                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(0.4f)) {
                        ModernTextField(
                            value = viewModel.age.value,
                            onValueChange = { viewModel.age.value = it },
                            label = "Age",
                            icon = Icons.Default.Cake,
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(0.6f)) {
                        ModernTextField(
                            value = viewModel.standard.value,
                            onValueChange = { viewModel.standard.value = it },
                            label = "Class/Standard",
                            icon = Icons.Default.School
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                ModernTextField(
                    value = viewModel.address.value,
                    onValueChange = { viewModel.address.value = it },
                    label = "Address",
                    icon = Icons.Default.LocationOn,
                    singleLine = false,
                    maxLines = 2
                )
            }

            // --- Section 3: Referral Data ---
            FormSection(title = "Clinical Assessment") {
                ModernTextField(
                    value = viewModel.reason.value,
                    onValueChange = { viewModel.reason.value = it },
                    label = "Reason for Referral",
                    icon = Icons.Default.ReportProblem,
                    singleLine = false,
                    maxLines = 3
                )
                Spacer(Modifier.height(12.dp))
                ModernTextField(
                    value = viewModel.behavior.value,
                    onValueChange = { viewModel.behavior.value = it },
                    label = "Behavioral Observations",
                    icon = Icons.Default.Face, // or Psychology if available
                    singleLine = false,
                    maxLines = 3
                )
                Spacer(Modifier.height(12.dp))
                ModernTextField(
                    value = viewModel.academic.value,
                    onValueChange = { viewModel.academic.value = it },
                    label = "Academic Performance",
                    icon = Icons.Default.MenuBook,
                    singleLine = false,
                    maxLines = 3
                )
                Spacer(Modifier.height(12.dp))
                ModernTextField(
                    value = viewModel.disciplinary.value,
                    onValueChange = { viewModel.disciplinary.value = it },
                    label = "Disciplinary History",
                    icon = Icons.Default.Warning,
                    singleLine = false,
                    maxLines = 3
                )
                Spacer(Modifier.height(12.dp))
                ModernTextField(
                    value = viewModel.specialNeed.value,
                    onValueChange = { viewModel.specialNeed.value = it },
                    label = "Special Needs Concern",
                    icon = Icons.Default.Healing,
                    singleLine = false,
                    maxLines = 3
                )
            }

            Spacer(Modifier.height(16.dp))

            // --- Submit Button ---
            Button(
                onClick = { viewModel.sendReferral() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryLightBlue),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text(
                    text = "SUBMIT REFERRAL",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = PrimaryLightBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryLightBlue)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (maxLines > 1) ImeAction.Default else ImeAction.Next
        ),
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryLightBlue,
            focusedLabelColor = PrimaryLightBlue,
            unfocusedContainerColor = Color(0xFFFAFAFA),
            focusedContainerColor = Color.White
        )
    )
}