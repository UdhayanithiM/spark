package com.example.sparkapp.ui.screens.referral

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- ADDED IMPORT
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // <-- ADDED IMPORT
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment // <-- ADDED IMPORT
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.R
import com.example.sparkapp.ui.theme.ReferralAppBarColor
import com.example.sparkapp.ui.theme.ChecklistItemBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(
    navController: NavController,
    viewModel: ReferralViewModel = viewModel()
) {
    val submissionStatus by viewModel.submissionStatus.collectAsState()

    // Handle Loading/Success/Error states
    when (submissionStatus) {
        SubmissionStatus.LOADING -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        SubmissionStatus.SUCCESS -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetStatus() },
                title = { Text("Success") },
                text = { Text("Referral submitted successfully.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetStatus()
                        navController.popBackStack() // Go back on success
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        SubmissionStatus.ERROR -> {
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
        SubmissionStatus.IDLE -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("REFERRAL", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReferralAppBarColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        // --- UPDATED FROM LAZYCOLUMN TO COLUMN ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally // <-- Alignment added
        ) {
            Image(
                painter = painterResource(id = R.drawable.classroom),
                contentDescription = "Classroom Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Name, Age, Standard, Address fields
            TransparentTextField(
                value = viewModel.name.value,
                onValueChange = { viewModel.name.value = it },
                label = "Name"
            )
            Spacer(modifier = Modifier.height(10.dp))
            TransparentTextField(
                value = viewModel.age.value,
                onValueChange = { viewModel.age.value = it },
                label = "Age"
            )
            Spacer(modifier = Modifier.height(10.dp))
            TransparentTextField(
                value = viewModel.standard.value,
                onValueChange = { viewModel.standard.value = it },
                label = "Standard"
            )
            Spacer(modifier = Modifier.height(10.dp))
            TransparentTextField(
                value = viewModel.address.value,
                onValueChange = { viewModel.address.value = it },
                label = "Address"
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Grey container for detailed inputs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChecklistItemBg) // Re-using this color
                    .padding(12.dp)
            ) {
                TransparentTextField(
                    value = viewModel.reason.value,
                    onValueChange = { viewModel.reason.value = it },
                    label = "Reason for Referral"
                )
                Spacer(modifier = Modifier.height(10.dp))
                TransparentTextField(
                    value = viewModel.behavior.value,
                    onValueChange = { viewModel.behavior.value = it },
                    label = "Behavioural observation in school"
                )
                Spacer(modifier = Modifier.height(10.dp))
                TransparentTextField(
                    value = viewModel.academic.value,
                    onValueChange = { viewModel.academic.value = it },
                    label = "Academic performance"
                )
                Spacer(modifier = Modifier.height(10.dp))
                TransparentTextField(
                    value = viewModel.disciplinary.value,
                    onValueChange = { viewModel.disciplinary.value = it },
                    label = "Past disciplinary issues"
                )
                Spacer(modifier = Modifier.height(10.dp))
                TransparentTextField(
                    value = viewModel.specialNeed.value,
                    onValueChange = { viewModel.specialNeed.value = it },
                    label = "Special need concern"
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Send Referral Button
            Button(
                onClick = { viewModel.sendReferral() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ReferralAppBarColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Send Referral",
                    style = TextStyle(fontSize = 18.sp, color = Color.White)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * This composable replicates the `_buildTransparentTextField` widget.
 */
@Composable
private fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = TextStyle(color = Color.Gray)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        textStyle = TextStyle(color = Color.Black)
    )
}