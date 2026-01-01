package com.example.sparkapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sparkapp.AppRoutes
import com.example.sparkapp.R // This imports your logo from "res/drawable"
import com.example.sparkapp.ui.theme.SparkAppPurple

// This is the Kotlin version of your Flutter SplashScreen widget
@Composable
fun SplashScreen(navController: NavController) {

    // Scaffold is like Flutter's Scaffold
    Scaffold(
        containerColor = SparkAppPurple, // Set background color
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        // Column is like Flutter's Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp), // This is the SafeArea padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // mainAxisAlignment.center
        ) {

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // 'R.drawable.logo' is your 'logo.png'
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp) // height: 200
            )

            Spacer(modifier = Modifier.height(20.dp)) // SizedBox(height: 20)

            // App Title
            Text(
                text = "SCHOOL\nPSYCHOLOGICAL ASSESSMENT\nREFERRAL KIT",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp)) // SizedBox(height: 40)

            // Get Started Button
            Button(
                onClick = {
                    // This is your Navigator.pushNamed(context, '/login')
                    navController.navigate(AppRoutes.LOGIN) {
                        // This makes sure the user can't press "back" to go to the splash screen
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White // backgroundColor: Colors.white
                ),
                // This is your padding:
                // const EdgeInsets.symmetric(horizontal: 40, vertical: 12)
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "GET STARTED",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}