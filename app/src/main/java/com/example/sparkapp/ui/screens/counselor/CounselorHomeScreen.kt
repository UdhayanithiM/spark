package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.R

// Define Theme Colors
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

@Composable
fun CounselorHomeScreen(mainNavController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // --- 1. Welcome Header ---
        Text(
            text = "Welcome Counselor,",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray
        )
        Text(
            text = "What would you like to do?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. Learning Module Card ---
        HomeCard(
            title = "Learning Modules",
            subtitle = "Access training materials and guides",
            imageResId = R.drawable.card_learning_module, // Ensure this drawable exists
            onClick = {
                mainNavController.navigate(AppRoutes.PRE_TEST)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. Referral Card ---
        HomeCard(
            title = "Create Referral",
            subtitle = "Submit a new student assessment",
            imageResId = R.drawable.card_referral, // Ensure this drawable exists
            onClick = {
                mainNavController.navigate("create_referral")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Modern Reusable Card
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(
    imageResId: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Image Section
            Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Content Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Action Button (Visual cue)
                Surface(
                    shape = CircleShape,
                    color = PrimaryLightBlue.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = PrimaryLightBlue
                        )
                    }
                }
            }
        }
    }
}