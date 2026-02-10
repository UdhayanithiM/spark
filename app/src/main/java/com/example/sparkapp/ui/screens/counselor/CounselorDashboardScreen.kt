package com.example.sparkapp.ui.screens.counselor

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// --- THEME COLOR DEFINITION ---
private val PrimaryLightBlue = Color(0xFF03A9F4)
private val BackgroundGray = Color(0xFFF5F7FA)

// --- SCREEN DEFINITIONS ---
sealed class CounselorScreen(val route: String, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : CounselorScreen("counselor_home_content", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Status : CounselorScreen("referral_status", "Referrals", Icons.Filled.Assignment, Icons.Outlined.Assignment)
    object Profile : CounselorScreen("counselor_profile", "Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline)
}

val counselorBottomNavItems = listOf(
    CounselorScreen.Home,
    CounselorScreen.Status,
    CounselorScreen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounselorDashboardScreen(
    mainNavController: NavController,
    userId: String,
    onLogout: () -> Unit
) {
    val tabNavController = rememberNavController()

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar( // Modern Center Aligned Header
                title = {
                    Text(
                        "Counselor Portal",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryLightBlue,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { mainNavController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle Notifications */ }) {
                        Icon(Icons.Outlined.Notifications, "Notifications")
                    }
                }
            )
        },
        bottomBar = {
            // Modern Bottom Navigation with Shadow
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    counselorBottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = {
                                Text(
                                    screen.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryLightBlue,
                                selectedTextColor = PrimaryLightBlue,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = PrimaryLightBlue.copy(alpha = 0.15f) // Subtle pill background
                            ),
                            onClick = {
                                if (!isSelected) {
                                    tabNavController.navigate(screen.route) {
                                        popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = CounselorScreen.Home.route,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable(CounselorScreen.Home.route) {
                CounselorHomeScreen(mainNavController = mainNavController)
            }
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