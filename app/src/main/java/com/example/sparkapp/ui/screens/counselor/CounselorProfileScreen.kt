package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.BorderStroke // <-- ADDED IMPORT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // <-- ADDED IMPORT
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember // <-- ADDED IMPORT
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // <-- ADDED IMPORT
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorProfileScreen(
    onLogout: () -> Unit, // Callback to navigate
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() } // 'remember' is now imported

    // Listen for snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.snackbarShown()
        }
    }

    // Listen for navigation events (logout)
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            if (event == "logout") {
                onLogout()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Matches blueAccent
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            // Loading Indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp) // Matches Flutter padding
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Icon",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary // Matches blueAccent
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Name TextField
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    readOnly = !uiState.isEditing, // Toggles readOnly
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email TextField
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    readOnly = true, // Always read-only
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    enabled = false, // Visually indicates it's non-editable
                    colors = OutlinedTextFieldDefaults.colors( // <-- CORRECTED
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Phone TextField
                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone") },
                    readOnly = !uiState.isEditing, // Toggles readOnly
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                // Spacer to push button to bottom
                Spacer(modifier = Modifier.weight(1f))

                // Edit/Save Button
                Button(
                    onClick = { viewModel.onEditToggle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp), // Matches Flutter's padding
                    shape = RoundedCornerShape(12.dp) // 'RoundedCornerShape' is now imported
                ) {
                    // This logic exactly matches the Flutter button
                    val icon = if (uiState.isEditing) Icons.Default.Save else Icons.Default.Edit
                    val text = if (uiState.isEditing) "Save Profile" else "Edit Profile"

                    Icon(imageVector = icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text, fontSize = 18.sp) // 'sp' is now imported
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button (Added from my previous correct analysis)
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error) // 'BorderStroke' is now imported
                ) {
                    Text("Logout", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}