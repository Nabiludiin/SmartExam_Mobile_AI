package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d3if4802.smartexam.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDrawerContent(
    logoPainter: Painter,
    onClose: () -> Unit
) {
    var isMataKuliahExpanded by remember { mutableStateOf(true) }
    var isAktivitasExpanded by remember { mutableStateOf(true) }

    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerTonalElevation = 0.dp,
        modifier = Modifier.fillMaxHeight().width(320.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = logoPainter,
                    contentDescription = "SmartExam Logo",
                    modifier = Modifier.height(48.dp).width(120.dp),
                    contentScale = ContentScale.Fit
                )
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ExamDeepBlue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .border(BorderStroke(2.dp, ExamDeepBlue), CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Albert Mandala, S. P...", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ExamDrawerTextBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(color = ExamDeepBlue, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = "DOSEN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                BadgedBox(
                    badge = {
                        Badge(containerColor = Color.Red, contentColor = Color.White) {
                            Text("9+")
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = ExamDeepBlue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = ExamCardBorder)
            Spacer(modifier = Modifier.height(16.dp))

            ExamDrawerCategory(
                title = "MATA KULIAH",
                isExpanded = isMataKuliahExpanded,
                onClick = { isMataKuliahExpanded = !isMataKuliahExpanded }
            )
            if (isMataKuliahExpanded) {
                Column {
                    ExamDrawerItem(icon = Icons.Outlined.Home, label = "Mata Kuliah Saya", isSelected = true)
                    ExamDrawerItem(icon = Icons.Default.Search, label = "Katalog", isSelected = false)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExamDrawerCategory(
                title = "AKTIVITAS",
                isExpanded = isAktivitasExpanded,
                onClick = { isAktivitasExpanded = !isAktivitasExpanded }
            )
            if (isAktivitasExpanded) {
                Column {
                    ExamDrawerItem(icon = Icons.Default.Refresh, label = "Terakhir Dikunjungi", isSelected = false)
                    ExamDrawerItem(icon = Icons.Default.List, label = "Riwayat", isSelected = false)
                    ExamDrawerItem(icon = Icons.Outlined.DateRange, label = "Laporan Waktu", isSelected = false)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = logoPainter,
                    contentDescription = "SmartExam Footer Logo",
                    modifier = Modifier.height(30.dp).width(80.dp),
                    contentScale = ContentScale.Fit
                )
                Text(text = "Academic Excellence\nReporting System © 2025", fontSize = 10.sp, color = ExamTextGray, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ExamDrawerCategory(title: String, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ExamDrawerTextBlue
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = "Toggle",
            tint = ExamDrawerTextBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ExamDrawerItem(icon: Any, label: String, isSelected: Boolean) {
    val bgColor = if (isSelected) ExamDeepBlue else Color.Transparent
    val contentColor = if (isSelected) Color.White else ExamDrawerTextBlue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (icon) {
            is ImageVector -> Icon(imageVector = icon, contentDescription = label, tint = contentColor)
            is Painter -> Icon(painter = icon, contentDescription = label, tint = contentColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = contentColor)
    }
}

@Preview(showBackground = true)
@Composable
fun ExamSidebarPreview() {
    MaterialTheme {
        ExamDrawerContent(
            logoPainter = painterResource(id = R.drawable.logo_smartexam),
            onClose = {}
        )
    }
}