package com.d3if4802.smartexam

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.d3if4802.smartexam.ui.ReviewScreen
import com.d3if4802.smartexam.ui.ViewExamScreen
import com.d3if4802.smartexam.viewmodel.ExamViewModel
import com.d3if4802.smartexam.viewmodel.ExamViewModelFactory

const val LOGGED_IN_MAHASISWA_ID = 3

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
                onNavigateToDetail = { course ->
                    val dosen = course.users?.nama_lengkap ?: "Dosen Belum Diatur"
                    val kat = course.kategori ?: "Lainnya"
                    navController.navigate("course_detail/${course.id}/${course.namaMatkul}/$kat/$dosen/${course.jumlahMahasiswa}")
                }
            )
        }

        composable(
            route = "course_detail/{courseId}/{namaMatkul}/{kategori}/{namaDosen}/{jumlahMahasiswa}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType },
                navArgument("namaMatkul") { type = NavType.StringType },
                navArgument("kategori") { type = NavType.StringType },
                navArgument("namaDosen") { type = NavType.StringType },
                navArgument("jumlahMahasiswa") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0
            val namaMatkul = backStackEntry.arguments?.getString("namaMatkul") ?: ""
            val kategori = backStackEntry.arguments?.getString("kategori") ?: ""
            val namaDosen = backStackEntry.arguments?.getString("namaDosen") ?: ""
            val jumlahMahasiswa = backStackEntry.arguments?.getInt("jumlahMahasiswa") ?: 0

            val daftarUjianAsli by sharedViewModel.examList.collectAsState()
            val daftarMateriAsli by sharedViewModel.materialList.collectAsState()

            LaunchedEffect(courseId) {
                sharedViewModel.fetchExamsByCourse(courseId)
                sharedViewModel.fetchMaterialsByCourse(courseId)
            }

            CourseDetailScreen(
                namaMatkul = namaMatkul,
                kategori = kategori,
                namaDosen = namaDosen,
                jumlahMahasiswa = jumlahMahasiswa,
                daftarUjian = daftarUjianAsli,
                daftarMateri = daftarMateriAsli,
                onBackClick = { navController.popBackStack() },
                onMateriClick = { materiId ->
                    Log.d("CEK_API", "Klik materi ID: $materiId")
                },
                onLatihanClick = { examId ->
                    navController.navigate("view_exam/$examId")
                }
            )
        }

        composable(
            route = "view_exam/{examId}",
            arguments = listOf(navArgument("examId") { type = NavType.IntType })
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getInt("examId") ?: 0
            val historyData by sharedViewModel.historyList.collectAsState()

            LaunchedEffect(examId) {
                sharedViewModel.activeExamId = examId
                sharedViewModel.fetchExamHistory(
                    mahasiswaId = LOGGED_IN_MAHASISWA_ID,
                    examId = examId
                )
            }

            ViewExamScreen(
                historyList = historyData,
                onBackClick = { navController.popBackStack() },
                onStartTestClick = {
                    sharedViewModel.resetExam()
                    val filter = "eq.$examId"
                    sharedViewModel.fetchQuestionsFromServer(filter)
                    navController.navigate("exam")
                },
                onReviewClick = {
                    navController.navigate("review/$LOGGED_IN_MAHASISWA_ID")
                }
            )
        }

        composable("exam") {
            ExamScreen(
                viewModel = sharedViewModel,
                onFinishExam = {
                    sharedViewModel.kirimSemuaJawabanKeServer(LOGGED_IN_MAHASISWA_ID)
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
                    sharedViewModel.fetchQuestionsFromServer("eq.${sharedViewModel.activeExamId}")
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

        composable(
            route = "review/{mahasiswaId}",
            arguments = listOf(navArgument("mahasiswaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val mId = backStackEntry.arguments?.getInt("mahasiswaId") ?: LOGGED_IN_MAHASISWA_ID
            ReviewScreen(
                viewModel = sharedViewModel,
                mahasiswaId = mId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}