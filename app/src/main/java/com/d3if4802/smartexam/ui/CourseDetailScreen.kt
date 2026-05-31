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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.d3if4802.smartexam.data.Course
import com.d3if4802.smartexam.data.CourseMaterial
import com.d3if4802.smartexam.data.Exam
import com.d3if4802.smartexam.data.RetrofitClient

private object DetailColors {
    val BackgroundLight = Color(0xFFF8FAFC)
    val PrimaryBlue = Color(0xFF0064B0)
    val BadgeBlue = Color(0xFFE6F0FF)
    val BadgeTextBlue = Color(0xFF0056D2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Int,
    onBackClick: () -> Unit,
    onMateriClick: (materiId: Int) -> Unit,
    onLatihanClick: (examId: Int) -> Unit
) {
    // Variabel penampung data dari Database
    var course by remember { mutableStateOf<Course?>(null) }
    var daftarMateri by remember { mutableStateOf<List<CourseMaterial>>(emptyList()) }
    var daftarUjian by remember { mutableStateOf<List<Exam>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Proses Menarik Data dari API saat layar dibuka
    LaunchedEffect(courseId) {
        try {
            val courses = RetrofitClient.apiService.getCourses()
            course = courses.find { it.id == courseId }
            daftarMateri = RetrofitClient.apiService.getMaterialsByCourse("eq.$courseId")
            daftarUjian = RetrofitClient.apiService.getExamsByCourse("eq.$courseId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

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
                            tint = DetailColors.PrimaryBlue
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
        containerColor = DetailColors.BackgroundLight
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DetailColors.PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
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
                                text = course?.namaMatkul ?: "Mata Kuliah",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                lineHeight = 30.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = DetailColors.BadgeBlue,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp), tint = DetailColors.BadgeTextBlue)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = course?.kategori ?: "Lainnya", color = DetailColors.BadgeTextBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                Text(text = course?.users?.nama_lengkap ?: "Dosen Pengampu", fontSize = 13.sp, color = Color.DarkGray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "${course?.jumlahMahasiswa ?: 0} Mahasiswa", fontSize = 13.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Materi Pembelajaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))

                if (daftarMateri.isEmpty()) {
                    Text(text = "Belum ada materi dari dosen.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    daftarMateri.forEach { materi ->
                        MenuItemCard(
                            title = materi.judulMateri,
                            description = "Format: ${materi.tipeMateri}",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            onClick = { onMateriClick(materi.materiId) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Daftar Ujian & Latihan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))

                if (daftarUjian.isEmpty()) {
                    Text(text = "Belum ada ujian yang dibuat.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    daftarUjian.forEach { ujian ->
                        MenuItemCard(
                            title = ujian.judulUjian,
                            description = "Tipe: ${ujian.tipeUjian}",
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            onClick = { onLatihanClick(ujian.examId) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
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
                color = DetailColors.BadgeBlue,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = DetailColors.BadgeTextBlue)
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