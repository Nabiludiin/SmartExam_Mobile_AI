package com.d3if4802.smartexam.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d3if4802.smartexam.R
import com.d3if4802.smartexam.viewmodel.ExamViewModel

val ResultGreen = Color(0xFF00875A)
val ResultLightBlue = Color(0xFFE6F0FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(viewModel: ExamViewModel = viewModel(), onRetry: () -> Unit = {}, onHome: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = ExamDeepBlue)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aptitude Test: Hasil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Surface(
                    color = ResultGreen,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "LULUS",
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
                            contentDescription = "Profile",
                            modifier = Modifier.size(50.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Student one", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(color = ResultLightBlue, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "10/10 Soal Evaluasi",
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

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Username:", fontSize = 10.sp, color = ExamTextGray)
                            Text("s1", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Durasi:", fontSize = 10.sp, color = ExamTextGray)
                            Text("00:01:06", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("IP:", fontSize = 10.sp, color = ExamTextGray)
                            Text("127.0.0.1", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Tanggal Mulai:", fontSize = 10.sp, color = ExamTextGray)
                            Text("11:36 PM, 20 Sept\n2025", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ResultLightBlue),
                border = BorderStroke(1.dp, Color(0xFFB9D5FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Skor Total Anda", fontSize = 14.sp, color = ExamDrawerTextBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "10/10", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = ExamDeepBlue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Review Jawaban",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ExamDeepBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            viewModel.questions.forEachIndexed { index, question ->
                QuestionReviewItem(
                    no = index + 1,
                    soal = question.text,
                    score = "10/10"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ExamDeepBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExamDeepBlue)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Coba Lagi", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExamDeepBlue)
            ) {
                Text("Kembali ke Mata Kuliah", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuestionReviewItem(no: Int, soal: String, score: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ExamCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$no. $soal", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "TERSIMPAN",
                        color = Color(0xFF065F46),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Skor Evaluasi: $score", fontSize = 11.sp, color = ExamTextGray)
        }
    }
}