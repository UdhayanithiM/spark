package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment // New Icon
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sparkapp.ui.theme.SparkAppPurple

// --- UPDATED TABS ---
sealed class CounselorScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : CounselorScreen("counselor_home_content", "Home", Icons.Default.Home)
    object Status : CounselorScreen("referral_status", "Referrals", Icons.Default.Assignment) // <-- NEW TAB
    object Profile : CounselorScreen("counselor_profile", "Profile", Icons.Default.PersonOutline)
}

val counselorBottomNavItems = listOf(
    CounselorScreen.Home,
    CounselorScreen.Status, // <-- REPLACED HISTORY & CHAT
    CounselorScreen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorDashboardScreen(
    mainNavController: NavController,
    userId: String, // <-- REQUIRED: PASSED FROM LOGIN/MAINNAV
    onLogout: () -> Unit
) {
    val tabNavController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Counselor Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SparkAppPurple,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { mainNavController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.NotificationsNone, "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                counselorBottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SparkAppPurple,
                            selectedTextColor = SparkAppPurple,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black
                        ),
                        onClick = {
                            tabNavController.navigate(screen.route) {
                                popUpTo(tabNavController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = CounselorScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CounselorScreen.Home.route) {
                CounselorHomeScreen(mainNavController = mainNavController)
            }
            // --- NEW SCREEN ---
            composable(CounselorScreen.Status.route) {
                ReferralStatusScreen(
                    counselorId = userId,
                    mainNavController = mainNavController
                )
            }
            composable(CounselorScreen.Profile.route) {
                CounselorProfileScreen(onLogout = onLogout)
            }
        }
    }
}