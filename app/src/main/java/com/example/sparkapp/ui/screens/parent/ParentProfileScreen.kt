package com.example.sparkapp.ui.screens.parent

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sparkapp.network.ParentDetails

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

    // 2. Use the correct state variable 'profileState'
    val uiState = viewModel.profileState
    val context = LocalContext.current
    val deepPurple = Color(0xFF673AB7)

    // 3. Handle Errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            viewModel.clearProfileError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Parent Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = deepPurple,
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
                CircularProgressIndicator(color = deepPurple)
            }
        } else if (uiState.details != null) {
            val details = uiState.details
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                DetailRow(label = "Name", value = details.name)
                DetailRow(label = "Father's Occupation", value = details.fatherOccupation)
                DetailRow(label = "Mother's Occupation", value = details.motherOccupation)
                DetailRow(label = "Father's Phone number", value = details.fatherPhone)
                DetailRow(label = "Mother's Phone number", value = details.motherPhone)
                DetailRow(label = "Email", value = details.email)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { /* No action defined yet */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = deepPurple),
                    enabled = false // Kept disabled as per original code
                ) {
                    Text("Edit")
                }
            }
        } else {
            // Fallback if details are null and not loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No profile data available.")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF673AB7)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
            Text(
                text = value ?: "N/A",
                style = TextStyle(fontSize = 16.sp, color = Color.Black.copy(alpha = 0.87f))
            )
        }
    }
}