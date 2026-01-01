package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.R

// This is your 'HomePageContent' widget
@Composable
fun CounselorHomeScreen(mainNavController: NavController) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- THIS IS THE FIX ---

        // 1. Learning Module card
        Spacer(modifier = Modifier.height(20.dp))
        HomeCard(
            title = "Learning Modules",
            imageResId = R.drawable.card_learning_module,
            onClick = {
                // This starts your 5-step flow by navigating to the Pre-Test
                mainNavController.navigate(AppRoutes.PRE_TEST)
            }
        )

        // 2. Referral card
        Spacer(modifier = Modifier.height(20.dp))
        HomeCard(
            title = "Create Referral",
            imageResId = R.drawable.card_referral,
            onClick = {
                // This navigates to the referral form
                mainNavController.navigate("create_referral")
            }
        )

        // --- END OF FIX ---
    }
}

// This is the reusable Card UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(
    imageResId: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick, // This replaces GestureDetector
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop // This is your 'fit: BoxFit.cover'
            )
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}