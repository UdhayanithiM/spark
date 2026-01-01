package com.example.sparkapp.ui.screens.signup

import android.widget.Toast
import androidx.compose.foundation.background
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

    // Listen for events from ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SignUpUiEvent.SignUpSuccess -> {
                    // Show success message
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    // Navigate back to Login screen
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
            TopAppBar(
                title = { Text("SIGN UP") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SparkAppPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        // This is your Form(child: ListView(...))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Role selector
            RoleDropdown(
                selectedRole = uiState.selectedRole,
                options = uiState.roleOptions,
                onRoleSelected = { viewModel.onRoleChanged(it) }
            )

            // Name field (common for all)
            CommonTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = "Name"
            )

            // Fields based on selected role
            when (uiState.selectedRole) {
                "Doctor" -> DoctorFields(uiState, viewModel)
                "Counselor" -> CounselorFields(uiState, viewModel)
                "Parent" -> ParentFields(uiState, viewModel)
            }

            Spacer(Modifier.height(20.dp))

            // Sign up Button
            Button(
                onClick = { viewModel.onSignUpClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("SIGN UP", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// This is your 'buildDoctorFields()'
@Composable
fun DoctorFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    CommonTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email"
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
        label = "Phone number"
    )
}

// This is your 'buildCounselorFields()'
@Composable
fun CounselorFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
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
        label = "School"
    )
    CommonTextField(
        value = uiState.yearInSchool,
        onValueChange = { viewModel.onYearInSchoolChanged(it) },
        label = "Year in current school"
    )
    CommonTextField(
        value = uiState.phone,
        onValueChange = { viewModel.onPhoneChanged(it) },
        label = "Phone number"
    )
    CommonTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        isPassword = true
    )
    CommonTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email"
    )
}

// This is your 'buildParentFields()'
@Composable
fun ParentFields(uiState: SignUpUiState, viewModel: SignUpViewModel) {
    CommonTextField(
        value = uiState.age,
        onValueChange = { viewModel.onAgeChanged(it) },
        label = "Age" // Parent's Age
    )
    CommonTextField(
        value = uiState.qualification,
        onValueChange = { viewModel.onQualificationChanged(it) },
        label = "Qualification"
    )
    CommonTextField(
        value = uiState.fatherOcc,
        onValueChange = { viewModel.onFatherOccChanged(it) },
        label = "Father's Occupation"
    )
    CommonTextField(
        value = uiState.motherOcc,
        onValueChange = { viewModel.onMotherOccChanged(it) },
        label = "Mother's Occupation"
    )
    CommonTextField(
        value = uiState.fatherPhone,
        onValueChange = { viewModel.onFatherPhoneChanged(it) },
        label = "Father's Phone number"
    )
    CommonTextField(
        value = uiState.motherPhone,
        onValueChange = { viewModel.onMotherPhoneChanged(it) },
        label = "Mother's Phone number"
    )
    CommonTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        label = "Email"
    )
    CommonTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        label = "Password",
        isPassword = true
    )

    Spacer(Modifier.height(10.dp))

    // This is your 'Container' with the light blue background
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE8ECF8), // Your Color(0xFFE8ECF8)
                shape = MaterialTheme.shapes.medium
            )
            .padding(12.dp)
    ) {
        CommonTextField(
            value = uiState.studentName,
            onValueChange = { viewModel.onStudentNameChanged(it) },
            label = "Student Name"
        )
        CommonTextField(
            value = uiState.standard,
            onValueChange = { viewModel.onStandardChanged(it) },
            label = "Standard"
        )
        CommonTextField(
            value = uiState.studentAge, // Using the new 'studentAge' state
            onValueChange = { viewModel.onStudentAgeChanged(it) },
            label = "Age" // Student's Age
        )
        CommonTextField(
            value = uiState.registerNumber,
            onValueChange = { viewModel.onRegisterNumberChanged(it) },
            label = "Register number"
        )
    }
}