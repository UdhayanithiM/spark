package com.example.sparkapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.ui.theme.SparkAppPurple
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel() // This creates the ViewModel
) {
    val uiState = loginViewModel.uiState
    val context = LocalContext.current

    // This block listens for one-time events from the ViewModel
    LaunchedEffect(key1 = true) {
        loginViewModel.uiEvent.collectLatest { event ->
            when (event) {
                // --- THIS IS THE FIX ---
                is LoginUiEvent.LoginSuccess -> {
                    // Navigate to the correct dashboard based on role
                    val route = when (event.role) {
                        // Use the event.userId to build the correct route
                        "Counselor" -> "${AppRoutes.COUNSELOR_HOME}/${event.userId}"
                        "Doctor" -> AppRoutes.DOCTOR_HOME
                        "Parent" -> AppRoutes.PARENT_HOME
                        else -> AppRoutes.LOGIN // Fallback
                    }
                    navController.navigate(route) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
                is LoginUiEvent.ShowError -> {
                    // This is your '_showMessage(msg)'
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // This is your Flutter Scaffold
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LOGIN") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Your 'Navigator.pop(context)'
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
        // This is your ListView
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Login Icon",
                modifier = Modifier.size(60.dp),
                tint = Color.Black
            )

            Spacer(Modifier.height(16.dp))

            // This is your DropdownButtonFormField
            RoleDropdown(
                selectedRole = uiState.selectedRole,
                options = uiState.roleOptions,
                onRoleSelected = { loginViewModel.onRoleChanged(it) }
            )

            Spacer(Modifier.height(16.dp))

            // This is your Email TextField
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { loginViewModel.onEmailChanged(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // This is your Password TextField
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { loginViewModel.onPasswordChanged(it) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // This is your Login Button
            Button(
                onClick = { loginViewModel.onLoginClicked(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                enabled = !uiState.isLoading // Disable button when loading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("LOGIN", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // This is your "Sign up" link
            SignUpLink(
                onSignUpClicked = {
                    navController.navigate(AppRoutes.SIGNUP) // Your 'Navigator.pushNamed(context, '/signup')'
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    selectedRole: String,
    options: List<String>,
    onRoleSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRole,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SignUpLink(onSignUpClicked: () -> Unit) {
    // This builds the "Don't have an account? Sign up" text
    Text(
        text = buildAnnotatedString {
            append("Don't have an account? ")
            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                append("Sign up")
            }
        },
        modifier = Modifier.clickable { onSignUpClicked() }
    )
}