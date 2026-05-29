package com.d3if4802.smartexam.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d3if4802.smartexam.R
import com.d3if4802.smartexam.viewmodel.ExamViewModel
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

val ResultGreen = Color(0xFF00875A)
val ResultLightBlue = Color(0xFFE6F0FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: ExamViewModel = viewModel(),
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val savedAnswers by viewModel.allAnswers.collectAsState()
    val serverQuestions = viewModel.questions.collectAsState().value
    val totalQuestions = serverQuestions.size

    val totalAnswered = savedAnswers.count { it.answerText.isNotBlank() }

    val aiScores = remember { mutableStateMapOf<Int, Int>() }
    var isLoadingScores by remember { mutableStateOf(true) }

    LaunchedEffect(savedAnswers, serverQuestions) {
        if (savedAnswers.isNotEmpty() && serverQuestions.isNotEmpty() && isLoadingScores) {

            // PENTING: Ganti string di bawah ini dengan API Key Gemini aslimu!
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "GANTI_DENGAN_API_KEY_ASLI_GEMINI_DI_SINI"
            )

            savedAnswers.forEach { answer ->
                // Cari data soal secara lengkap dari server
                val question = serverQuestions.find { it.id == answer.questionId }
                val questionText = question?.text ?: ""

                // Tarik kunci jawaban dari PostgreSQL. Jika kosong, suruh AI mikir sendiri.
                val kunciJawaban = question?.kunciJawaban ?: "Gunakan standar teori akademik yang paling benar dan umum untuk pertanyaan ini."
                val userAnswer = answer.answerText.trim()

                if (userAnswer.isEmpty()) {
                    aiScores[answer.questionId] = 0
                } else {
                    scope.launch {
                        try {
                            // Prompt yang lebih tegas dan menyertakan Kunci Jawaban
                            val systemPrompt = """
                                Bertindaklah sebagai dosen ahli penguji ujian esai.
                                Pertanyaan: '$questionText'
                                Kunci Jawaban/Referensi Dosen: '$kunciJawaban'
                                Jawaban Mahasiswa: '$userAnswer'
                                
                                Bandingkan Jawaban Mahasiswa dengan Kunci Jawaban/Referensi. 
                                Berikan nilai kelayakan berupa SATU ANGKA BULAT dari 0 sampai 10.
                                ATURAN MUTLAK: Balas HANYA dengan angka (contoh: 8). Dilarang keras memberikan teks tambahan, penjelasan, bintang, atau simbol apapun.
                            """.trimIndent()

                            val response = generativeModel.generateContent(systemPrompt)
                            val rawText = response.text ?: ""

                            Log.d("GEMINI_TEST", "Soal ID ${answer.questionId} | Gemini Balas: $rawText")

                            // Ekstraksi pintar menggunakan Regex (Cari angka pertama yang muncul)
                            val extractedNumber = Regex("\\d+").find(rawText)?.value
                            val cleanScore = extractedNumber?.toIntOrNull() ?: 0

                            // Jaga-jaga kalau AI halusinasi ngasih nilai 100 dari skala 10
                            aiScores[answer.questionId] = if (cleanScore > 10) 10 else cleanScore

                        } catch (e: Exception) {
                            Log.e("GEMINI_TEST", "Gemini Gagal Jalan! Penyebab: ${e.message}")
                            aiScores[answer.questionId] = 0
                        }
                    }
                }
            }
            isLoadingScores = false
        }
    }

    val finalTotalScore = aiScores.values.sum()
    val maxScore = totalQuestions * 10
    val finalPercentage = if (maxScore > 0) (finalTotalScore * 100) / maxScore else 0

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ExamDrawerContent(
                logoPainter = painterResource(id = R.drawable.logo_smartexam),
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = ExamDeepBlue)
                        }
                    },
                    title = { Text("Aptitude Test", fontWeight = FontWeight.Bold, color = ExamDeepBlue) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFF8FAFC)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Aptitude Test: Hasil", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Surface(color = ResultGreen, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "SELESAI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ExamCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Student one", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Surface(color = ResultLightBlue, shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = "$totalAnswered/$totalQuestions Terjawab",
                                        fontSize = 10.sp,
                                        color = ExamDeepBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = ExamCardBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Skor Evaluasi AI Gemini:", fontSize = 10.sp, color = ExamTextGray)
                                if (isLoadingScores) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("$finalPercentage% ($finalTotalScore/$maxScore)", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ExamOrange)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Status Validasi:", fontSize = 10.sp, color = ExamTextGray)

                                val statusText: String
                                val statusColor: Color

                                if (isLoadingScores) {
                                    statusText = "Menilai..."
                                    statusColor = ExamOrange
                                } else {
                                    when {
                                        finalPercentage == 100 -> {
                                            statusText = "Aman (100%)"
                                            statusColor = ResultGreen
                                        }
                                        finalPercentage >= 70 -> {
                                            statusText = "Perlu Diperiksa"
                                            statusColor = ExamOrange
                                        }
                                        else -> {
                                            statusText = "Periksa Dosen!"
                                            statusColor = Color(0xFFDC2626) // Merah
                                        }
                                    }
                                }

                                Text(
                                    text = statusText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Review Jawaban & Analisis Esai", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ExamDeepBlue)
                Spacer(modifier = Modifier.height(16.dp))

                serverQuestions.forEachIndexed { index, question ->
                    val userAnswer = savedAnswers.find { it.questionId == question.id }?.answerText ?: ""
                    val currentScore = aiScores[question.id] ?: 0

                    QuestionReviewItem(
                        no = index + 1,
                        soal = question.text,
                        jawaban = userAnswer,
                        isAnswered = userAnswer.isNotBlank(),
                        skorSoal = currentScore
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ExamDeepBlue)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coba Ujian Lagi", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExamDeepBlue)
                ) {
                    Text("Kembali ke Beranda", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun QuestionReviewItem(no: Int, soal: String, jawaban: String, isAnswered: Boolean, skorSoal: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ExamCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    text = "$no. $soal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = if (isAnswered) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Skor: $skorSoal/10",
                        color = if (isAnswered) Color(0xFF065F46) else Color(0xFF991B1B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Jawaban Anda:", fontSize = 11.sp, color = ExamTextGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAnswered) jawaban else "Belum ada jawaban yang tersimpan.",
                fontSize = 13.sp,
                color = if (isAnswered) Color(0xFF0F172A) else Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}