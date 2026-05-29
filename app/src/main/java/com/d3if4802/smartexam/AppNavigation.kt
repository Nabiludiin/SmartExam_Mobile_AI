package com.d3if4802.smartexam

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d3if4802.smartexam.db.AppDatabase
import com.d3if4802.smartexam.ui.CourseDetailScreen
import com.d3if4802.smartexam.ui.CourseListScreen
import com.d3if4802.smartexam.ui.ExamScreen
import com.d3if4802.smartexam.ui.ResultScreen
import com.d3if4802.smartexam.viewmodel.ExamViewModel
import com.d3if4802.smartexam.viewmodel.ExamViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = AppDatabase.getDatabase(context)
    val factory = ExamViewModelFactory(database.answerDao())
    val sharedViewModel: ExamViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = "course_list") {

        composable("course_list") {
            CourseListScreen(
                onNavigateToDetail = {
                    navController.navigate("course_detail")
                }
            )
        }

        composable("course_detail") {
            CourseDetailScreen(
                onNavigateToExam = {
                    sharedViewModel.resetExam()
                    navController.navigate("exam")
                }
            )
        }

        composable("exam") {
            ExamScreen(
                viewModel = sharedViewModel,
                onFinishExam = {
                    navController.navigate("result") {
                        popUpTo("exam") { inclusive = true }
                    }
                }
            )
        }

        composable("result") {
            ResultScreen(
                viewModel = sharedViewModel,
                onRetry = {
                    sharedViewModel.resetExam()
                    navController.navigate("exam") {
                        popUpTo("result") { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate("course_list") {
                        popUpTo("course_list") { inclusive = true }
                    }
                }
            )
        }
    }
}