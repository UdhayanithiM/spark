package com.example.sparkapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.R

@Composable
fun SplashScreen(navController: NavController) {

    // Define Modern Light Blue Colors
    val LightBlueTop = Color(0xFFF0F8FF)   // Alice Blue
    val LightBlueBottom = Color(0xFFE1F5FE) // Light Sky Blue
    val PrimaryBlue = Color(0xFF0288D1)     // Ocean Blue for Button
    val TextColor = Color(0xFF01579B)       // Dark Navy for Text

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Modern Vertical Gradient
                brush = Brush.verticalGradient(
                    colors = listOf(LightBlueTop, LightBlueBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // App Logo with subtle shadow/elevation
            Surface(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(100.dp)), // Circular shadow
                shape = RoundedCornerShape(100.dp),
                color = Color.White // White circle behind logo
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(160.dp) // Slightly smaller inside the white circle
                            .padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // App Title - Modern Typography
            Text(
                text = "SCHOOL\nPSYCHOLOGICAL ASSESSMENT\nREFERRAL KIT",
                textAlign = TextAlign.Center,
                color = TextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp, // Adds spacing for a premium look
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Modern Get Started Button
            Button(
                onClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50.dp), // Fully rounded (Pill shape)
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Takes 80% of width
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "GET STARTED",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}