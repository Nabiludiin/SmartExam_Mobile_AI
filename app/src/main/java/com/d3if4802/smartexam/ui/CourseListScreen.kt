package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.d3if4802.smartexam.R
import com.d3if4802.smartexam.data.Course
import com.d3if4802.smartexam.viewmodel.CourseViewModel
import kotlinx.coroutines.launch

val ColorPrimaryBlue = Color(0xFF0064B0)
val ColorBackgroundLight = Color(0xFFF8FAFC)
val ColorCardBorder = Color(0xFFE2E8F0)
val ColorTextSlate = Color(0xFF64748B)

fun getImageUrlByCategory(kategori: String?): String {
    return when (kategori?.lowercase()) {
        "pemrograman" -> "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=400&q=80"
        "jaringan" -> "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?auto=format&fit=crop&w=400&q=80"
        "desain ui/ux" -> "https://images.unsplash.com/photo-1561070791-2526d30994b5?auto=format&fit=crop&w=400&q=80"
        else -> "https://images.unsplash.com/photo-1497032628192-86f99bcd76bc?auto=format&fit=crop&w=400&q=80"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel = viewModel(),
    onNavigateToDetail: (Course) -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var isGridView by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf("Semua", "Pemrograman", "Jaringan", "Desain UI/UX", "Lainnya")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val allCourses by viewModel.courseList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val filteredCourses = allCourses.filter { course ->
        val matchesSearch = course.namaMatkul.contains(searchQuery, ignoreCase = true)

        val matchesCategory = if (selectedCategory == "Semua") {
            true
        } else {
            val kategoriDb = course.kategori ?: "Lainnya"
            kategoriDb.equals(selectedCategory, ignoreCase = true)
        }

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
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logo_smartexam),
                            contentDescription = "Logo Smart Exam",
                            modifier = Modifier
                                .height(32.dp)
                                .padding(start = 8.dp),
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
            bottomBar = {
                BottomNavigationBar(
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            containerColor = ColorBackgroundLight
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(R.string.title_course_list), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(stringResource(R.string.subtitle_course_list), fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Nama Mata Kuliah", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(R.string.hint_search_course), color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ColorPrimaryBlue,
                                unfocusedBorderColor = ColorCardBorder,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ColorPrimaryBlue,
                                    unfocusedBorderColor = ColorCardBorder,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                categories.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item, color = Color.Black) },
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
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .padding(2.dp)
                            ) {
                                IconButton(
                                    onClick = { isGridView = false },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!isGridView) Color.White else Color.Transparent)
                                ) {
                                    Icon(Icons.Default.ViewList, contentDescription = "List View", tint = if (!isGridView) ColorPrimaryBlue else Color.Gray)
                                }
                                IconButton(
                                    onClick = { isGridView = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
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
                                        modifier = Modifier
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                            .clickable { selectedCategory = "Semua" },
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

                if (isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ColorPrimaryBlue)
                        }
                    }
                } else if (errorMessage != null) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage ?: "Error Network", color = Color.Red, fontSize = 14.sp)
                        }
                    }
                } else if (filteredCourses.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Mata kuliah tidak ditemukan.", color = Color.Gray)
                        }
                    }
                } else {
                    if (isGridView) {
                        items(filteredCourses) { course ->
                            GridCourseItem(course, onClick = { onNavigateToDetail(course) })
                        }
                    } else {
                        items(filteredCourses, span = { GridItemSpan(2) }) { course ->
                            ListCourseItem(course, onClick = { onNavigateToDetail(course) })
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

@Composable
fun ListCourseItem(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCardBorder)
    ) {
        Column {
            AsyncImage(
                model = getImageUrlByCategory(course.kategori),
                contentDescription = "Cover Mata Kuliah",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(course.namaMatkul, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text("👨‍🏫 ${course.users?.nama_lengkap ?: stringResource(R.string.dosen_not_set)}", fontSize = 12.sp, color = Color.Gray)
                Text("🔖 Kode: ${course.kodeMatkul}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onClick, modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryBlue)) {
                    Text(stringResource(R.string.btn_enter_class), fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GridCourseItem(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCardBorder)
    ) {
        Column {
            AsyncImage(
                model = getImageUrlByCategory(course.kategori),
                contentDescription = "Cover Mata Kuliah",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(course.namaMatkul, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text(course.users?.nama_lengkap ?: stringResource(R.string.dosen_not_set), fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClick, modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryBlue)) {
                    Text(stringResource(R.string.btn_enter), fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(onMenuClick: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { onMenuClick() },
            icon = { Icon(Icons.Outlined.Menu, contentDescription = "Menu") },
            label = { Text("Menu", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Beranda") },
            label = { Text("Beranda", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = ColorPrimaryBlue,
                indicatorColor = ColorPrimaryBlue
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profil") },
            label = { Text("Profil", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}