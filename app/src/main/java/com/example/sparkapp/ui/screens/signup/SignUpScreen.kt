package com.example.sparkapp.ui.screens.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // <-- THIS IMPORT WAS MISSING
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.ui.components.CommonTextField
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SparkAppPurple,
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Role Dropdown
            RoleDropdown(
                selectedRole = uiState.selectedRole,
                options = uiState.roleOptions,
                onRoleSelected = { viewModel.onRoleChanged(it) }
            )

            Spacer(Modifier.height(16.dp))

            // Common Name Field
            CommonTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = "Full Name"
            )

            // Dynamic Fields based on Role
            when (uiState.selectedRole) {
                "Doctor" -> DoctorFields(uiState, viewModel)
                "Counselor" -> CounselorFields(uiState, viewModel)
                "Parent" -> ParentFields(uiState, viewModel)
            }

            Spacer(Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = { viewModel.onSignUpClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("REGISTER", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

// --- Field Composables ---

@Composable
fun DoctorFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    CommonTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email Address"
    )
    CommonTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        isPassword = true
    )
    CommonTextField(
        value = uiState.phone,
        onValueChange = { viewModel.onPhoneChanged(it) },
        label = "Phone Number"
    )
}

@Composable
fun CounselorFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    CommonTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email Address"
    )
    CommonTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        isPassword = true
    )
    CommonTextField(
        value = uiState.phone,
        onValueChange = { viewModel.onPhoneChanged(it) },
        label = "Phone Number"
    )
    CommonTextField(
        value = uiState.age,
        onValueChange = { viewModel.onAgeChanged(it) },
        label = "Age"
    )
    CommonTextField(
        value = uiState.qualification,
        onValueChange = { viewModel.onQualificationChanged(it) },
        label = "Qualification"
    )
    CommonTextField(
        value = uiState.school,
        onValueChange = { viewModel.onSchoolChanged(it) },
        label = "School Name"
    )
    CommonTextField(
        value = uiState.yearInSchool,
        onValueChange = { viewModel.onYearInSchoolChanged(it) },
        label = "Year in Current School"
    )
}

@Composable
fun ParentFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    // --- Account Info ---
    CommonTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email Address"
    )
    CommonTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        isPassword = true
    )

    // --- Personal Info ---
    CommonTextField(
        value = uiState.age,
        onValueChange = { viewModel.onAgeChanged(it) },
        label = "Parent Age"
    )
    CommonTextField(
        value = uiState.qualification,
        onValueChange = { viewModel.onQualificationChanged(it) },
        label = "Education/Qualification"
    )

    // --- Family Details ---
    CommonTextField(
        value = uiState.fatherOcc,
        onValueChange = { viewModel.onFatherOccChanged(it) },
        label = "Father's Occupation"
    )
    CommonTextField(
        value = uiState.fatherPhone,
        onValueChange = { viewModel.onFatherPhoneChanged(it) },
        label = "Father's Contact Number"
    )
    CommonTextField(
        value = uiState.motherOcc,
        onValueChange = { viewModel.onMotherOccChanged(it) },
        label = "Mother's Occupation"
    )
    CommonTextField(
        value = uiState.motherPhone,
        onValueChange = { viewModel.onMotherPhoneChanged(it) },
        label = "Mother's Contact Number"
    )
}




