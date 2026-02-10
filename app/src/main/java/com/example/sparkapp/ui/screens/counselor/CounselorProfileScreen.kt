package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

// Define Theme Colors locally
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen for snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.snackbarShown()
        }
    }

    // Listen for logout
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            if (event == "logout") {
                onLogout()
            }
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Profile",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryLightBlue
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryLightBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- 1. PROFILE HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Profile Image Container
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 8.dp,
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(PrimaryLightBlue.copy(alpha = 0.1f))
                            ) {
                                Text(
                                    text = uiState.name.take(1).uppercase(),
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryLightBlue
                                )
                            }
                        }

                        // Edit Badge (Visual only)
                        if (uiState.isEditing) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryLightBlue,
                                border = BorderStroke(2.dp, Color.White),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Edit Photo",
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // --- 2. FORM CARD ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProfileTextField(
                            value = uiState.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = "Full Name",
                            icon = Icons.Outlined.Person,
                            isEditable = uiState.isEditing
                        )

                        ProfileTextField(
                            value = uiState.email,
                            onValueChange = {},
                            label = "Email Address",
                            icon = Icons.Outlined.Email,
                            isEditable = false // Email is never editable
                        )

                        ProfileTextField(
                            value = uiState.phone,
                            onValueChange = { viewModel.onPhoneChange(it) },
                            label = "Phone Number",
                            icon = Icons.Outlined.Phone,
                            isEditable = uiState.isEditing,
                            keyboardType = KeyboardType.Phone
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. ACTIONS ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Edit/Save Button
                    Button(
                        onClick = { viewModel.onEditToggle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isEditing) Color(0xFF4CAF50) else PrimaryLightBlue
                        ),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isEditing) "Save Changes" else "Edit Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Logout Button
                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE53935)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// --- REUSABLE COMPONENT: Modern Text Field ---
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isEditable: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isEditable) PrimaryLightBlue else Color.Gray
            )
        },
        readOnly = !isEditable,
        enabled = isEditable || label == "Email Address", // Keep Email looking "enabled" but read-only for styling consistency, or disable it fully if preferred
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryLightBlue,
            unfocusedBorderColor = Color.LightGray,
            disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
            disabledTextColor = Color.Gray,
            disabledLabelColor = Color.Gray,
            focusedLabelColor = PrimaryLightBlue,
            unfocusedLabelColor = Color.Gray
        )
    )
}