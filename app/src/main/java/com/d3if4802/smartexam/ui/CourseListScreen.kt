package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
// PERBAIKAN 1: Pastikan mengimpor Course dari package data
import com.d3if4802.smartexam.data.Course
import com.d3if4802.smartexam.viewmodel.CourseViewModel
import kotlinx.coroutines.launch

// Penamaan unik warna global menghindari conflict declarations
val ColorPrimaryBlue = Color(0xFF0064B0)
val ColorBackgroundLight = Color(0xFFF8FAFC)
val ColorCardBorder = Color(0xFFE2E8F0)
val ColorTextSlate = Color(0xFF64748B)

// PERBAIKAN 2: HAPUS `data class Course` LAMA DI SINI KARENA BENTROK. KITA PAKAI YANG DARI FOLDER DATA.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel = viewModel(),
    onNavigateToDetail: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var isGridView by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Semua", "Pemrograman", "Jaringan", "Desain UI/UX")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val allCourses by viewModel.courseList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val filteredCourses = allCourses.filter { course ->
        // PERBAIKAN 3: Sesuaikan filter dengan field namaMatkul milik PostgreSQL
        val matchesSearch = course.namaMatkul.contains(searchQuery, ignoreCase = true)

        // Karena di DB tidak ada kategori, filter ini di by-pass sementara
        val matchesCategory = selectedCategory == "Semua"

        matchesSearch && matchesCategory
    }

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
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ColorPrimaryBlue)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = ColorPrimaryBlue, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SMART EXAM", fontWeight = FontWeight.Black, color = ColorPrimaryBlue, fontSize = 18.sp)
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier.padding(end = 16.dp).size(36.dp).clip(CircleShape).background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = { BottomNavigationBar() },
            containerColor = ColorBackgroundLight
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Home, contentDescription = "Home", tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(" > Pengelolaan Mata Kuliah", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Daftar Mata Kuliah Saya", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Temukan semua kelas yang kamu ikuti di sini dan mari lanjutkan progres belajarmu hari ini.", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Nama Mata Kuliah", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Masukkan nama mata kuliah", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimaryBlue, unfocusedBorderColor = ColorCardBorder)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Kategori", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ColorPrimaryBlue, unfocusedBorderColor = ColorCardBorder)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                categories.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            selectedCategory = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Menampilkan ${filteredCourses.size} data", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFE2E8F0)).padding(2.dp)
                            ) {
                                IconButton(
                                    onClick = { isGridView = false },
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp))
                                        .background(if (!isGridView) Color.White else Color.Transparent)
                                ) {
                                    Icon(Icons.Default.List, contentDescription = "List View", tint = if (!isGridView) ColorPrimaryBlue else Color.Gray)
                                }
                                IconButton(
                                    onClick = { isGridView = true },
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp))
                                        .background(if (isGridView) Color.White else Color.Transparent)
                                ) {
                                    Icon(Icons.Default.GridView, contentDescription = "Grid View", tint = if (isGridView) ColorPrimaryBlue else Color.Gray)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedCategory != "Semua") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Difilter berdasarkan : ", fontSize = 12.sp, color = Color.Gray)
                                Surface(color = Color(0xFFE0E7FF), shape = RoundedCornerShape(16.dp)) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp).clickable { selectedCategory = "Semua" },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Kategori : $selectedCategory", fontSize = 12.sp, color = ColorPrimaryBlue)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = ColorPrimaryBlue, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // API ASYNC STATE CONTROLLER
                if (isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ColorPrimaryBlue)
                        }
                    }
                } else if (errorMessage != null) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage ?: "Error Network", color = Color.Red, fontSize = 14.sp)
                        }
                    }
                } else if (filteredCourses.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Mata kuliah tidak ditemukan.", color = Color.Gray)
                        }
                    }
                } else {
                    if (isGridView) {
                        items(filteredCourses) { course ->
                            GridCourseItem(course, onNavigateToDetail)
                        }
                    } else {
                        items(filteredCourses, span = { GridItemSpan(2) }) { course ->
                            ListCourseItem(course, onNavigateToDetail)
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// PERBAIKAN 4: Ubah pemanggilan field menyesuaikan Course DB (namaMatkul, dosenId)
@Composable
fun ListCourseItem(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCardBorder)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFCBD5E1)), contentAlignment = Alignment.Center) {
                Text("img", color = Color.White)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(course.namaMatkul, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("👨‍🏫 Dosen ID: ${course.dosenId}", fontSize = 12.sp, color = Color.Gray)
                Text("🔖 Kode: ${course.kodeMatkul}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryBlue)) {
                    Text("Masuk Kelas", fontSize = 12.sp)
                }
            }
        }
    }
}

// PERBAIKAN 5: Sama seperti List, sesuaikan untuk Grid
@Composable
fun GridCourseItem(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCardBorder)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(70.dp).background(Color(0xFFCBD5E1)), contentAlignment = Alignment.Center) {
                Text("img", color = Color.White, fontSize = 10.sp)
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(course.namaMatkul, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text("ID: ${course.dosenId}", fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(30.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryBlue)) {
                    Text("Masuk", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    BottomAppBar(containerColor = Color.White, modifier = Modifier.height(80.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Dashboard, contentDescription = "Dashboard", tint = Color.Gray)
                Text("Dashboard", fontSize = 10.sp, color = Color.Gray)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clip(CircleShape).background(ColorPrimaryBlue).padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Book, contentDescription = "Kelas", tint = Color.White)
                Text("Kelas", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.History, contentDescription = "Riwayat", tint = Color.Gray)
                Text("Riwayat", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}