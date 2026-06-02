package com.d3if4802.smartexam.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d3if4802.smartexam.R
import com.d3if4802.smartexam.data.ExamAttempt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewExamScreen(
    historyList: List<ExamAttempt>,
    maxAttempts: Int,
    onBackClick: () -> Unit,
    onStartTestClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    val canStartTest = historyList.size < maxAttempts

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_smartexam),
                        contentDescription = "Logo Smart Exam",
                        modifier = Modifier
                            .height(32.dp)
                            .padding(start = 4.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF0064B0)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                PaddingValues(horizontal = 24.dp, vertical = 16.dp).let { padding ->
                    if (canStartTest) {
                        Button(
                            onClick = onStartTestClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(padding)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0064B0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Mulai Tes (Sisa: ${maxAttempts - historyList.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = { },
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(padding)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Batas Percobaan Habis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Riwayat Percobaan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("${historyList.size}/$maxAttempts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
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
                                        imageVector = Icons.Outlined.Visibility,
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