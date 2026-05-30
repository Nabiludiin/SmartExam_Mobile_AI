package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.*
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

private object ViewExamColors {
    val Background = Color(0xFFF8FAFC)
    val GreenButton = Color(0xFF42B581)
    val BorderGray = Color(0xFFE2E8F0)
    val TextGray = Color(0xFF64748B)
    val BadgeBg = Color(0xFFF0F9FF)
    val BadgeText = Color(0xFF0284C7)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewExamScreen(
    examTitle: String = "Aptitude Test",
    examDescription: String = "Tes ini berguna untuk menjamin kualitas dari seorang kandidat apabila dikerjakan dengan penuh kesungguhan.",
    historyList: List<ExamAttempt> = emptyList(),

    // --- UBAH JADI onBackClick ---
    onBackClick: () -> Unit = {},
    onStartTestClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    // --- UBAH IKON JADI PANAH KEMBALI ---
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = ColorPrimaryBlue
                        )
                    }
                },
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
        containerColor = ViewExamColors.Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ViewExamColors.BorderGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = examTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = examDescription, fontSize = 14.sp, color = ViewExamColors.TextGray, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onStartTestClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ViewExamColors.GreenButton),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mulai Tes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Riwayat Percobaan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isEmpty()) {
                Text(
                    text = "Belum ada riwayat percobaan.",
                    fontSize = 14.sp,
                    color = ViewExamColors.TextGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                historyList.forEach { history ->
                    HistoryCard(history = history)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HistoryCard(history: ExamAttempt) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ViewExamColors.BorderGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "PERCOBAAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ViewExamColors.TextGray)
                    Text(text = "${history.attemptNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "NILAI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ViewExamColors.TextGray)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(text = "${history.scorePercentage.toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "(${history.scoreAbsolute}/${history.scoreMax})", fontSize = 12.sp, color = ViewExamColors.TextGray, modifier = Modifier.padding(bottom = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Tanggal Mulai:", fontSize = 13.sp, color = ViewExamColors.TextGray)
                Text(text = history.tanggalMulai, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "IP Address:", fontSize = 13.sp, color = ViewExamColors.TextGray)
                Text(text = history.ipAddress, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ViewExamColors.BadgeBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = history.status,
                        color = ViewExamColors.BadgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = ViewExamColors.Background,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { /* TODO */ }) {
                        Icon(Icons.Outlined.RemoveRedEye, contentDescription = "Detail", tint = ViewExamColors.TextGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}