package com.example.sparkapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.ui.theme.SparkAppPurple
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState = loginViewModel.uiState
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    // Event Handling
    LaunchedEffect(key1 = true) {
        loginViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is LoginUiEvent.LoginSuccess -> {
                    val route = when (event.role) {
                        "Counselor" -> "${AppRoutes.COUNSELOR_HOME}/${event.userId}"
                        "Doctor" -> AppRoutes.DOCTOR_HOME
                        "Parent" -> AppRoutes.PARENT_HOME
                        else -> AppRoutes.LOGIN
                    }
                    navController.navigate(route) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
                is LoginUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA) // Light background for contrast
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Header Decoration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(SparkAppPurple)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Sign In",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(Modifier.height(40.dp))

                // Login Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon
                        Surface(
                            shape = CircleShape,
                            color = SparkAppPurple.copy(alpha = 0.1f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Login",
                                    tint = SparkAppPurple,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Role Selection
                        Text(
                            text = "I am a...",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(Modifier.height(8.dp))
                        RoleDropdown(
                            selectedRole = uiState.selectedRole,
                            options = uiState.roleOptions,
                            onRoleSelected = { loginViewModel.onRoleChanged(it) }
                        )

                        Spacer(Modifier.height(20.dp))

                        // Email Input
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { loginViewModel.onEmailChanged(it) },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = SparkAppPurple) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SparkAppPurple,
                                focusedLabelColor = SparkAppPurple,
                                cursorColor = SparkAppPurple
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        // Password Input
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { loginViewModel.onPasswordChanged(it) },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = SparkAppPurple) },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SparkAppPurple,
                                focusedLabelColor = SparkAppPurple,
                                cursorColor = SparkAppPurple
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        // Forgot Password
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = { navController.navigate(AppRoutes.FORGOT_PASSWORD) }) {
                                Text(
                                    "Forgot Password?",
                                    color = SparkAppPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Login Button
                        Button(
                            onClick = { loginViewModel.onLoginClicked(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    "LOGIN",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Sign Up Link
                SignUpLink(onSignUpClicked = { navController.navigate(AppRoutes.SIGNUP) })

                Spacer(Modifier.height(24.dp))
            }
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
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SparkAppPurple,
                focusedLabelColor = SparkAppPurple,
                cursorColor = SparkAppPurple
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SignUpLink(onSignUpClicked: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Don't have an account? ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SparkAppPurple,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.clickable { onSignUpClicked() }
        )
    }
}