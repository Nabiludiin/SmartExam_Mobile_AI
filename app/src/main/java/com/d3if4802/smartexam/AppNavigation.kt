package com.d3if4802.smartexam.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.d3if4802.smartexam.viewmodel.ExamViewModel

@Composable
fun AppNavigation(viewModel: ExamViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("course_list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("course_list") {
            CourseListScreen(
                onNavigateToDetail = { course ->
                    navController.navigate("course_detail/${course.id}")
                }
            )
        }

        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cId = backStackEntry.arguments?.getInt("courseId") ?: 0

            CourseDetailScreen(
                courseId = cId,
                onBackClick = { navController.popBackStack() },
                onMateriClick = { materiId -> },
                onLatihanClick = { examId ->
                    navController.navigate("view_exam/$examId")
                }
            )
        }

        composable(
            route = "view_exam/{examId}",
            arguments = listOf(navArgument("examId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eId = backStackEntry.arguments?.getInt("examId") ?: 0
            val mId by viewModel.currentUserId.collectAsState(initial = 0)

            LaunchedEffect(eId, mId) {
                if (mId != 0) {
                    viewModel.fetchExamHistory(mahasiswaId = mId, examId = eId)
                }
                viewModel.fetchSingleExam(examId = eId)
            }

            val historyList by viewModel.historyList.collectAsState()
            val examList by viewModel.examList.collectAsState()
            val ujianSaatIni = examList.find { it.examId == eId }
            val batasDariDosen = ujianSaatIni?.batasPercobaan ?: 1

            ViewExamScreen(
                historyList = historyList,
                maxAttempts = batasDariDosen,
                onBackClick = { navController.popBackStack() },
                onStartTestClick = {
                    navController.navigate("exam/$eId")
                },
                onReviewClick = {
                    navController.navigate("review/$mId/$eId")
                }
            )
        }

        composable(
            route = "exam/{examId}",
            arguments = listOf(navArgument("examId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eId = backStackEntry.arguments?.getInt("examId") ?: 0

            ExamScreen(
                examId = eId,
                viewModel = viewModel,
                onFinishExam = {
                    navController.navigate("result") {
                        popUpTo("course_list") { inclusive = false }
                    }
                },
                onCancelExam = {
                    navController.popBackStack()
                }
            )
        }

        composable("result") {
            ResultScreen(
                viewModel = viewModel,
                onRetry = { navController.popBackStack() },
                onHome = {
                    navController.navigate("course_list") { popUpTo(0) }
                }
            )
        }

        composable(
            route = "history/{mahasiswaId}/{examId}",
            arguments = listOf(
                navArgument("mahasiswaId") { type = NavType.IntType },
                navArgument("examId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val mId = backStackEntry.arguments?.getInt("mahasiswaId") ?: 0
            val eId = backStackEntry.arguments?.getInt("examId") ?: 0

            HistoryScreen(
                viewModel = viewModel,
                mahasiswaId = mId,
                examId = eId,
                onBackClick = { navController.popBackStack() },
                onAttemptClick = { navController.navigate("review/$mId/$eId") }
            )
        }

        composable(
            route = "review/{mahasiswaId}/{examId}",
            arguments = listOf(
                navArgument("mahasiswaId") { type = NavType.IntType },
                navArgument("examId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val mId = backStackEntry.arguments?.getInt("mahasiswaId") ?: 0
            val eId = backStackEntry.arguments?.getInt("examId") ?: 0

            ReviewScreen(
                viewModel = viewModel,
                mahasiswaId = mId,
                examId = eId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}