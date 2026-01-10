package com.example.sparkapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sparkapp.network.CounselorProfile
import com.example.sparkapp.network.ReferralResponse
import com.example.sparkapp.network.ScoreboardResponse
import com.example.sparkapp.ui.screens.SplashScreen
import com.example.sparkapp.ui.screens.checklist.ChecklistScreen
import com.example.sparkapp.ui.screens.counselor.CounselorDashboardScreen
import com.example.sparkapp.ui.screens.counselor.CounselorStudentDetailScreen
import com.example.sparkapp.ui.screens.doctor.*
import com.example.sparkapp.ui.screens.history.ScoreDetailScreen
import com.example.sparkapp.ui.screens.login.LoginScreen
import com.example.sparkapp.ui.screens.login.ForgotPasswordScreen
import com.example.sparkapp.ui.screens.module.FullScreenPlayerScreen
import com.example.sparkapp.ui.screens.module.ModuleScreen
import com.example.sparkapp.ui.screens.parent.ParentDashboardScreen
import com.example.sparkapp.ui.screens.parent.ParentProfileScreen
import com.example.sparkapp.ui.screens.parent.ParentViewModel
import com.example.sparkapp.ui.screens.posttest.PostTestScreen
import com.example.sparkapp.ui.screens.pretest.PreTestScreen
import com.example.sparkapp.ui.screens.referral.ReferralScreen
import com.example.sparkapp.ui.screens.scenario.ScenarioScreen
import com.example.sparkapp.ui.screens.signup.SignUpScreen
import com.google.gson.Gson
import java.net.URLEncoder

// ------------------------------------
// ROUTES (DO NOT DELETE)
// ------------------------------------
object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"

    const val DOCTOR_HOME = "doctorDashboard"
    const val COUNSELOR_HOME = "counselorDashboard"
    const val PARENT_HOME = "parentDashboard"

    const val PRE_TEST = "pre_test"
    const val MODULE_PAGE = "module_page"
    const val CHECKLIST = "checklist"
    const val POST_TEST = "post_test"
    const val SCENARIO = "scenario"

    const val COUNSELOR_LIST = "counselor_list"
    const val COUNSELOR_DETAIL = "counselor_detail"
    const val COUNSELOR_STUDENT_DETAIL = "counselor_student_detail"
}

// ------------------------------------
// MAIN NAVIGATION
// ------------------------------------
@Composable
fun MainNavigation() {

    val navController = rememberNavController()
    val doctorViewModel: DoctorViewModel = viewModel()
    val parentViewModel: ParentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH
    ) {

        // ---------- SPLASH ----------
        composable(AppRoutes.SPLASH) {
            SplashScreen(navController)
        }

        // ---------- AUTH ----------
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController)
        }

        composable(AppRoutes.SIGNUP) {
            SignUpScreen(navController)
        }
        composable(AppRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }

        // ---------- COUNSELOR ----------
        composable(
            route = "${AppRoutes.COUNSELOR_HOME}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            CounselorDashboardScreen(
                mainNavController = navController,
                userId = it.arguments?.getString("userId") ?: "0",
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ---------- PARENT ----------
        composable(AppRoutes.PARENT_HOME) {
            ParentDashboardScreen(
                onNavigateToProfile = { navController.navigate("parentProfile") },
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable("parentProfile") {
            ParentProfileScreen(
                viewModel = parentViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---------- STUDENT FLOW ----------
        composable(AppRoutes.PRE_TEST) { PreTestScreen(navController) }
        composable(AppRoutes.MODULE_PAGE) { ModuleScreen(navController) }
        composable(AppRoutes.SCENARIO) { ScenarioScreen(navController) }

        composable(AppRoutes.CHECKLIST) {
            ChecklistScreen(
                onProceed = { navController.navigate(AppRoutes.POST_TEST) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.POST_TEST) {
            PostTestScreen(navController)
        }

        composable("create_referral") {
            ReferralScreen(navController)
        }

        composable(
            route = "fullscreen_player/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.IntType })
        ) {
            FullScreenPlayerScreen(
                navController,
                it.arguments?.getInt("videoId") ?: return@composable
            )
        }

        // ---------- SCORE DETAIL ----------
        composable(
            route = "score_detail/{name}/{score}/{total}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("score") { type = NavType.StringType },
                navArgument("total") { type = NavType.StringType }
            )
        ) {
            ScoreDetailScreen(
                navController,
                it.arguments?.getString("name") ?: "",
                it.arguments?.getString("score") ?: "0",
                it.arguments?.getString("total") ?: "0"
            )
        }

        // ---------- DOCTOR ----------
        composable(AppRoutes.DOCTOR_HOME) {
            DoctorDashboardScreen(
                viewModel = doctorViewModel,
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                },

                onNavigateToStudent = { student ->
                    val json = URLEncoder.encode(Gson().toJson(student), "UTF-8")
                    navController.navigate("doctorStudentDetail/$json")
                },
                onNavigateToCounselors = {
                    navController.navigate(AppRoutes.COUNSELOR_LIST)
                }
            )
        }

        // ---------- DOCTOR STUDENT DETAIL ----------
        composable(
            route = "doctorStudentDetail/{studentJson}",
            arguments = listOf(navArgument("studentJson") { type = NavType.StringType })
        ) {
            DoctorStudentDetailScreen(
                student = Gson().fromJson(
                    it.arguments?.getString("studentJson"),
                    ReferralResponse::class.java
                ),
                viewModel = doctorViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---------- COUNSELOR LIST ----------
        composable(AppRoutes.COUNSELOR_LIST) {
            CounselorListScreen(
                viewModel = doctorViewModel,
                onNavigateToDetail = { counselor ->
                    val json = URLEncoder.encode(
                        Gson().toJson(counselor),
                        "UTF-8"
                    )
                    navController.navigate("${AppRoutes.COUNSELOR_DETAIL}/$json")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---------- COUNSELOR DETAIL ----------
        composable(
            route = "${AppRoutes.COUNSELOR_DETAIL}/{counselorJson}",
            arguments = listOf(navArgument("counselorJson") { type = NavType.StringType })
        ) {
            CounselorDetailScreen(
                counselor = Gson().fromJson(
                    it.arguments?.getString("counselorJson"),
                    CounselorProfile::class.java
                ),
                viewModel = doctorViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ---------- COUNSELOR → STUDENT ----------
        composable(
            route = "${AppRoutes.COUNSELOR_STUDENT_DETAIL}/{studentJson}",
            arguments = listOf(navArgument("studentJson") { type = NavType.StringType })
        ) {
            val context = LocalContext.current
            val counselorId = context
                .getSharedPreferences("SparkAppPrefs", Context.MODE_PRIVATE)
                .getString("user_id", "0") ?: "0"

            CounselorStudentDetailScreen(
                student = Gson().fromJson(
                    it.arguments?.getString("studentJson"),
                    ReferralResponse::class.java
                ),
                counselorId = counselorId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
