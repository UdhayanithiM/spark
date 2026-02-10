package com.example.sparkapp.ui.screens.parent

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// --- THEME COLORS ---
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ParentViewModel = viewModel()
) {
    // 1. Fetch Profile Data when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchParentProfile()
    }

    val uiState = viewModel.profileState
    val context = LocalContext.current

    // 2. Handle Errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            viewModel.clearProfileError()
        }
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Profile",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryLightBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
        } else if (uiState.details != null) {
            val details = uiState.details
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- 1. AVATAR HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(PrimaryLightBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = details.name?.take(1)?.uppercase() ?: "?",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryLightBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = details.name ?: "Parent",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = details.email ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                // --- 2. DETAILS CARD ---
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
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        SectionHeader("Family Information")

                        ProfileInfoItem(Icons.Default.Work, "Father's Occupation", details.fatherOccupation)
                        ProfileInfoItem(Icons.Default.Work, "Mother's Occupation", details.motherOccupation)

                        HorizontalDivider(color = Color(0xFFF0F0F0))

                        SectionHeader("Contact Details")

                        ProfileInfoItem(Icons.Default.Phone, "Father's Phone", details.fatherPhone)
                        ProfileInfoItem(Icons.Default.Phone, "Mother's Phone", details.motherPhone)
                        ProfileInfoItem(Icons.Default.Email, "Registered Email", details.email)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. EDIT BUTTON ---
                Button(
                    onClick = { /* Future Edit Implementation */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLightBlue,
                        disabledContainerColor = PrimaryLightBlue.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false // Kept disabled as per original requirement
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        } else {
            // Fallback
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No profile data available.", color = Color.Gray)
            }
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = PrimaryLightBlue,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ProfileInfoItem(icon: ImageVector, label: String, value: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F7FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Data
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value ?: "N/A",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}