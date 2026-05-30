package com.d3if4802.smartexam

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

        // 1. LAYAR DAFTAR MATA KULIAH
        composable("course_list") {
            CourseListScreen(
                onNavigateToDetail = { course ->
                    val dosen = course.users?.nama_lengkap ?: "Dosen Belum Diatur"
                    val kat = course.kategori ?: "Lainnya"

                    // Mengirimkan data sebagai argumen di dalam rute URL
                    navController.navigate("course_detail/${course.namaMatkul}/$kat/$dosen/${course.jumlahMahasiswa}")
                }
            )
        }

        // 2. LAYAR DETAIL MATA KULIAH (Menerima Argumen Dinamis)
        composable(
            route = "course_detail/{namaMatkul}/{kategori}/{namaDosen}/{jumlahMahasiswa}",
            arguments = listOf(
                navArgument("namaMatkul") { type = NavType.StringType },
                navArgument("kategori") { type = NavType.StringType },
                navArgument("namaDosen") { type = NavType.StringType },
                navArgument("jumlahMahasiswa") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val namaMatkul = backStackEntry.arguments?.getString("namaMatkul") ?: ""
            val kategori = backStackEntry.arguments?.getString("kategori") ?: ""
            val namaDosen = backStackEntry.arguments?.getString("namaDosen") ?: ""
            val jumlahMahasiswa = backStackEntry.arguments?.getInt("jumlahMahasiswa") ?: 0

            CourseDetailScreen(
                namaMatkul = namaMatkul,
                kategori = kategori,
                namaDosen = namaDosen,
                jumlahMahasiswa = jumlahMahasiswa,
                onBackClick = {
                    navController.popBackStack()
                },
                onMateriClick = {
                    // TODO: Arahkan ke layar materi jika diperlukan
                },
                onLatihanClick = {
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

        // 4. LAYAR HASIL
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