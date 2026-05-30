package com.d3if4802.smartexam.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d3if4802.smartexam.data.ExamAttempt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewExamScreen(
    historyList: List<ExamAttempt>,
    onBackClick: () -> Unit,
    onStartTestClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                PaddingValues(horizontal = 24.dp, vertical = 16.dp).let {
                    Button(
                        onClick = onStartTestClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0064B0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mulai Tes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Riwayat Percobaan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (historyList.isEmpty()) {
                item {
                    Text("Belum ada riwayat ujian.", color = Color.Gray)
                }
            } else {
                items(historyList) { attempt ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("PERCOBAAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text("${attempt.attemptNumber}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("NILAI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text("${attempt.scorePercentage.toInt()}% ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Text("(${attempt.scoreAbsolute}/${attempt.scoreMax})", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tanggal Mulai:", fontSize = 14.sp, color = Color.Gray)
                                Text(attempt.tanggalMulai, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("IP Address:", fontSize = 14.sp, color = Color.Gray)
                                Text(attempt.ipAddress, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEAF5FF), shape = RoundedCornerShape(20.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(attempt.status, color = Color(0xFF0064B0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = onReviewClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(android.R.drawable.ic_menu_view),
                                        contentDescription = "Lihat Review",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}