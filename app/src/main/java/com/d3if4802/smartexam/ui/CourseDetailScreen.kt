package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d3if4802.smartexam.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseName: String = "Algoritma Pemrograman", // Default value
    dosenName: String = "Albert Mandala, S.Pd", // Default value
    onNavigateToExam: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) { // Buka Drawer saat di klik
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ColorPrimaryBlue)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SMART EXAM", fontWeight = FontWeight.ExtraBold, color = ColorPrimaryBlue, fontSize = 18.sp)
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier.padding(end = 16.dp).size(32.dp).clip(CircleShape).background(Color.Gray)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorBackgroundLight)
                )
            },
            containerColor = ColorBackgroundLight
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Home, contentDescription = "Home", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(" > Pengelolaan Mata Kuliah > ", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                    // Data disesuaikan dari variabel
                    Text(courseName, fontSize = 12.sp, color = ColorPrimaryBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F7FB)),
                    border = BorderStroke(1.dp, ColorCardBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Data disesuaikan dari variabel
                            Text(courseName, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(color = Color(0xFFE0E7FF), shape = RoundedCornerShape(8.dp)) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Science, contentDescription = null, tint = ColorPrimaryBlue, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Projek", fontSize = 12.sp, color = ColorPrimaryBlue)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            // Data disesuaikan dari variabel
                            Text(dosenName, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(Icons.Outlined.Group, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("34 Mahasiswa", fontSize = 14.sp, color = Color.DarkGray)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = ColorCardBorder)
                        Spacer(modifier = Modifier.height(24.dp))

                        ModuleItem(icon = Icons.Outlined.MenuBook, title = "Materi", description = "Description", onClick = { })
                        Spacer(modifier = Modifier.height(12.dp))
                        ModuleItem(icon = Icons.Outlined.Assignment, title = "Latihan", description = "Description", onClick = onNavigateToExam)
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE0E7FF)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = ColorPrimaryBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(description, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.LightGray)
        }
    }
}