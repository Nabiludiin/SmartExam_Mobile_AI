package com.d3if4802.smartexam

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d3if4802.smartexam.ui.ExamScreen
import com.d3if4802.smartexam.ui.ResultScreen
import com.d3if4802.smartexam.viewmodel.ExamViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: ExamViewModel = viewModel()

    NavHost(navController = navController, startDestination = "exam") {
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
                    navController.navigate("exam") {
                        popUpTo("result") { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate("exam") {
                        popUpTo("result") { inclusive = true }
                    }
                }
            )
        }
    }
}