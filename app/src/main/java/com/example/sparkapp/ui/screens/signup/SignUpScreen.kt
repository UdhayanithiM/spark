package com.example.sparkapp.ui.screens.signup

import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.ui.components.RoleDropdown
import com.example.sparkapp.ui.theme.SparkAppPurple
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SignUpUiEvent.SignUpSuccess -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is SignUpUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA) // Light Gray Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Decorative Background Header ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(SparkAppPurple)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Top Bar ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // --- Main Form Card ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Role Selection
                        Text(
                            "I am a...",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        RoleDropdown(
                            selectedRole = uiState.selectedRole,
                            options = uiState.roleOptions,
                            onRoleSelected = { viewModel.onRoleChanged(it) }
                        )

                        Spacer(Modifier.height(24.dp))

                        // Common Field: Name
                        ModernTextField(
                            value = uiState.name,
                            onValueChange = { viewModel.onNameChanged(it) },
                            label = "Full Name",
                            icon = Icons.Default.Person
                        )

                        Spacer(Modifier.height(16.dp))

                        // Dynamic Fields
                        when (uiState.selectedRole) {
                            "Doctor" -> DoctorFields(uiState, viewModel)
                            "Counselor" -> CounselorFields(uiState, viewModel)
                            "Parent" -> ParentFields(uiState, viewModel)
                        }

                        Spacer(Modifier.height(32.dp))

                        // Submit Button
                        Button(
                            onClick = { viewModel.onSignUpClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                            elevation = ButtonDefaults.buttonElevation(6.dp),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "REGISTER",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPOSABLES FOR FIELDS
// -------------------------------------------------------------------------

@Composable
fun DoctorFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    ModernTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email Address",
        icon = Icons.Default.Email,
        keyboardType = KeyboardType.Email
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        icon = Icons.Default.Lock,
        isPassword = true
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.phone,
        onValueChange = { viewModel.onPhoneChanged(it) },
        label = "Phone Number",
        icon = Icons.Default.Phone,
        keyboardType = KeyboardType.Phone
    )
}

@Composable
fun CounselorFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    SectionHeader("Contact Info")
    ModernTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email",
        icon = Icons.Default.Email,
        keyboardType = KeyboardType.Email
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.phone,
        onValueChange = { viewModel.onPhoneChanged(it) },
        label = "Phone",
        icon = Icons.Default.Phone,
        keyboardType = KeyboardType.Phone
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        icon = Icons.Default.Lock,
        isPassword = true
    )

    Spacer(Modifier.height(24.dp))
    SectionHeader("Professional Details")

    Row(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(0.4f)) {
            ModernTextField(
                value = uiState.age,
                onValueChange = { viewModel.onAgeChanged(it) },
                label = "Age",
                icon = Icons.Default.Cake,
                keyboardType = KeyboardType.Number
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier.weight(0.6f)) {
            ModernTextField(
                value = uiState.qualification,
                onValueChange = { viewModel.onQualificationChanged(it) },
                label = "Qualification",
                icon = Icons.Default.School
            )
        }
    }
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.school,
        onValueChange = { viewModel.onSchoolChanged(it) },
        label = "School Name",
        icon = Icons.Default.Business
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.yearInSchool,
        onValueChange = { viewModel.onYearInSchoolChanged(it) },
        label = "Years Experience",
        icon = Icons.Default.DateRange,
        keyboardType = KeyboardType.Number
    )
}

@Composable
fun ParentFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    SectionHeader("Account Details")
    ModernTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email Address",
        icon = Icons.Default.Email,
        keyboardType = KeyboardType.Email
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        icon = Icons.Default.Lock,
        isPassword = true
    )

    Spacer(Modifier.height(24.dp))
    SectionHeader("Personal Info")

    ModernTextField(
        value = uiState.age,
        onValueChange = { viewModel.onAgeChanged(it) },
        label = "Parent Age",
        icon = Icons.Default.Cake,
        keyboardType = KeyboardType.Number
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.qualification,
        onValueChange = { viewModel.onQualificationChanged(it) },
        label = "Qualification",
        icon = Icons.Default.School
    )

    Spacer(Modifier.height(24.dp))
    SectionHeader("Family Details")

    ModernTextField(
        value = uiState.fatherOcc,
        onValueChange = { viewModel.onFatherOccChanged(it) },
        label = "Father's Occupation",
        icon = Icons.Default.Work
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.fatherPhone,
        onValueChange = { viewModel.onFatherPhoneChanged(it) },
        label = "Father's Mobile",
        icon = Icons.Default.Phone,
        keyboardType = KeyboardType.Phone
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.motherOcc,
        onValueChange = { viewModel.onMotherOccChanged(it) },
        label = "Mother's Occupation",
        icon = Icons.Default.WorkOutline
    )
    Spacer(Modifier.height(16.dp))
    ModernTextField(
        value = uiState.motherPhone,
        onValueChange = { viewModel.onMotherPhoneChanged(it) },
        label = "Mother's Mobile",
        icon = Icons.Default.Phone,
        keyboardType = KeyboardType.Phone
    )
}

// -------------------------------------------------------------------------
// HELPER COMPOSABLES
// -------------------------------------------------------------------------

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = SparkAppPurple,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = SparkAppPurple) },
        trailingIcon = if (isPassword) {
            {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                }
            }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SparkAppPurple,
            focusedLabelColor = SparkAppPurple,
            cursorColor = SparkAppPurple,
            unfocusedBorderColor = Color.LightGray
        )
    )
}