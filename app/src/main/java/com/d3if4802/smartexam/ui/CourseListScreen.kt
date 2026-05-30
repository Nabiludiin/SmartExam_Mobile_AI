package com.d3if4802.smartexam.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d3if4802.smartexam.data.Course
import com.d3if4802.smartexam.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel = viewModel(),
    onNavigateToDetail: () -> Unit = {}
) {
    val allCourses by viewModel.courseList.collectAsState()
    var isGridView by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text("Daftar Mata Kuliah", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))
            }

            items(allCourses, span = { if (isGridView) GridItemSpan(1) else GridItemSpan(2) }) { course ->
                if (isGridView) GridCourseItem(course, onNavigateToDetail)
                else ListCourseItem(course, onNavigateToDetail)
            }
        }
    }
}

@Composable
fun ListCourseItem(course: Course, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(course.namaMatkul, fontWeight = FontWeight.Bold)
            // Mengambil nama langsung dari relasi DB
            Text("👨‍🏫 ${course.users?.nama_lengkap ?: "Dosen Belum Diatur"}", fontSize = 12.sp)
            Text("🔖 ${course.kodeMatkul}", fontSize = 12.sp)
            Button(onClick = onClick) { Text("Masuk") }
        }
    }
}

@Composable
fun GridCourseItem(course: Course, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(course.namaMatkul, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(course.users?.nama_lengkap ?: "-", fontSize = 10.sp, maxLines = 1)
            Button(onClick = onClick) { Text("Masuk") }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.Book, null) }, label = { Text("Kelas") })
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.Home, null) }, label = { Text("Beranda") }, colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF0064B0)))
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("Profil") })
    }
}