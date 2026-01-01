package com.example.sparkapp.ui.screens.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sparkapp.data.ChecklistData
import com.example.sparkapp.data.ChecklistSection
// --- CORRECTED IMPORTS ---
import com.example.sparkapp.ui.theme.SparkAppPurple
import com.example.sparkapp.ui.theme.ChecklistHeaderBg
// ---
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(onProceed: () -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Checklist") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SparkAppPurple, // <-- CORRECTED
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
                .verticalScroll(rememberScrollState())
        ) {
            ChecklistData.sections.forEach { section ->
                SectionCard(section = section)
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SparkAppPurple) // <-- CORRECTED
            ) {
                Text("Proceed to Post-Test", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SectionCard(section: ChecklistSection) {
    // Section Title
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(ChecklistHeaderBg) // <-- CORRECTED
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = section.title,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Section Items
    Card(
        modifier = Modifier.fillMaxWidth(),
        // Note: This color matches 'ChecklistItemBg' from your Color.kt
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            section.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "● ",
                        style = TextStyle(fontSize = 16.sp)
                    )
                    Text(
                        text = item,
                        modifier = Modifier.weight(1f),
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
            }
        }
    }
}