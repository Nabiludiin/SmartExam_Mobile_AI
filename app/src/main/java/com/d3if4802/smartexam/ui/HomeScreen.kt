package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
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
import com.d3if4802.smartexam.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onStartExam: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_smartexam),
                            contentDescription = "Logo",
                            modifier = Modifier.height(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SmartExam", fontWeight = FontWeight.ExtraBold, color = ExamDeepBlue, fontSize = 22.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifikasi", tint = ExamDeepBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Profile",
                        modifier = Modifier.size(60.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Halo, Student one!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ExamDrawerTextBlue)
                        Text(text = "D3 Rekayasa Perangkat Lunak", fontSize = 14.sp, color = ExamTextGray)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Katalog Ujian Tersedia", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ExamDeepBlue)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CourseCard(
                    title = "Aptitude Test & Soft Skills",
                    code = "RPL-401",
                    duration = "45 Menit",
                    questions = "10 Soal",
                    onClick = onStartExam
                )
                Spacer(modifier = Modifier.height(16.dp))
                CourseCard(
                    title = "Pemrograman Berorientasi Objek",
                    code = "RPL-203",
                    duration = "90 Menit",
                    questions = "25 Soal",
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                CourseCard(
                    title = "Sistem Basis Data",
                    code = "RPL-205",
                    duration = "60 Menit",
                    questions = "20 Soal",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun CourseCard(title: String, code: String, duration: String, questions: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ExamCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(color = ResultLightBlue, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = code,
                        color = ExamDeepBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(text = "$questions | $duration", fontSize = 12.sp, color = ExamTextGray, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ExamDrawerTextBlue)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(45.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExamOrange)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mulai Tes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}