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
import androidx.compose.ui.text.TextStyle // <-- 1. ADD THIS IMPORT
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
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val deepPurple = Color(0xFF673AB7)

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
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
                CircularProgressIndicator()
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
                // This section replicates the buildDetailRow widgets
                DetailRow(label = "Name", value = details.name)
                DetailRow(label = "Father's Occupation", value = details.fatherOccupation)
                DetailRow(label = "Mother's Occupation", value = details.motherOccupation)
                DetailRow(label = "Father's Phone number", value = details.fatherPhone)
                DetailRow(label = "Mother's Phone number", value = details.motherPhone)
                DetailRow(label = "Email", value = details.email)

                Spacer(modifier = Modifier.height(20.dp))

                // Edit Button (Disabled, matches Flutter logic)
                Button(
                    onClick = { /* onPressed: () {} */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = deepPurple),
                    enabled = false // Matches Flutter's empty onPressed
                ) {
                    Text("Edit")
                }
            }
        }
    }
}

// This is the Kotlin version of the buildDetailRow helper function
@Composable
private fun DetailRow(label: String, value: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF673AB7)), // deepPurple
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$label: ",
                // 2. ADD 'TextStyle'
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
            Text(
                value ?: "N/A",
                // 3. ADD 'TextStyle'
                style = TextStyle(fontSize = 16.sp, color = Color.Black.copy(alpha = 0.87f))
            )
        }
    }
}