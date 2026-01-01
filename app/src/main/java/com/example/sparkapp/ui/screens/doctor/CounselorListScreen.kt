package com.example.sparkapp.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sparkapp.network.CounselorProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorListScreen(
    viewModel: DoctorViewModel,
    onNavigateToDetail: (CounselorProfile) -> Unit,
    onNavigateBack: () -> Unit
) {
    val counselors = viewModel.counselorList

    // Fetch data when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchCounselors()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counselor List") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (counselors.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No Counselors found or Loading...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(counselors) { counselor ->
                    CounselorCard(counselor, onClick = { onNavigateToDetail(counselor) })
                }
            }
        }
    }
}

@Composable
fun CounselorCard(counselor: CounselorProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = counselor.name ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = "School: ${counselor.school ?: "N/A"}", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Qualification: ${counselor.qualification ?: "N/A"}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}