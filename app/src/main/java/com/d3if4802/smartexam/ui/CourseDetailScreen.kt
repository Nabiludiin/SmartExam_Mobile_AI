package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ExamLightGray = Color(0xFFF8FAFC)
val BadgeBlue = Color(0xFFE6F0FF)
val BadgeTextBlue = Color(0xFF0056D2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    onBackClick: () -> Unit,
    onMateriClick: () -> Unit,
    onLatihanClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SMART EXAM", fontWeight = FontWeight.ExtraBold, color = ExamDeepBlue, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = ExamDeepBlue)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ExamLightGray)
            )
        },
        containerColor = ExamLightGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Home > Pengelolaan Mata Kuliah > Algoritma Pemrograman",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Algoritma Pemrograman",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(
                            color = BadgeBlue,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp), tint = BadgeTextBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Projek", color = BadgeTextBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Albert Mandala, S.Pd", fontSize = 13.sp, color = Color.DarkGray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("34 Mahasiswa", fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            MenuItemCard(
                title = "Materi",
                description = "Pelajari modul dan bahan bacaan",
                icon = Icons.Default.MenuBook,
                onClick = onMateriClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuItemCard(
                title = "Latihan",
                description = "Kerjakan soal essay AI",
                icon = Icons.Default.Assignment,
                onClick = onLatihanClick
            )
        }
    }
}

@Composable
fun MenuItemCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BadgeBlue,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = BadgeTextBlue)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 12.sp, color = Color.Gray)
            }
            Text(text = ">", color = Color.LightGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}