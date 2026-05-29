package com.d3if4802.smartexam.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d3if4802.smartexam.R
import com.d3if4802.smartexam.viewmodel.ExamViewModel
import kotlinx.coroutines.launch

val ExamDeepBlue = Color(0xFF005691)
val ExamOrange = Color(0xFFF9AB3E)
val ExamCardBorder = Color(0xFFE2E8F0)
val ExamTextGray = Color(0xFF64748B)
val ExamBackground = Color.White
val ExamDrawerTextBlue = Color(0xFF0F172A)
val ExamAnsweredGreen = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamViewModel = viewModel(),
    onFinishExam: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Ambil data asinkron dari server
    val questions = viewModel.questions.collectAsState().value
    val answerText by viewModel.jawabanState.collectAsState()
    val timeLeft by viewModel.timeLeftString.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val isTimeUp by viewModel.isTimeUp.collectAsState()
    val totalQuestions = questions.size

    val answeredStatus = remember { mutableStateMapOf<Int, Boolean>() }

    // FUNGSI AUTO-SUBMIT JIKA WAKTU HABIS
    LaunchedEffect(isTimeUp) {
        if (isTimeUp && questions.isNotEmpty()) {
            // Karena ini purwarupa, kita pakai mahasiswaId statis = 3 (Budi Santoso dari database)
            viewModel.kirimSemuaJawabanKeServer(mahasiswaId = 3)
            onFinishExam()
        }
    }

    LaunchedEffect(currentQuestionIndex, answerText) {
        answeredStatus[currentQuestionIndex] = answerText.isNotBlank()
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
                ExamTopAppBar(
                    timerText = timeLeft,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                ExamBottomNavigationBar(
                    onNavClick = { showBottomSheet = true }
                )
            },
            containerColor = ExamBackground
        ) { paddingValues ->

            // 1. PROTEKSI LOADING: Jangan tampilkan soal kalau array masih kosong (sedang nembak API)
            if (questions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ExamDeepBlue)
                }
            } else {
                // 2. SOAL SUDAH DIUNDUH: Tampilkan layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    ExamProgressHeader(currentIdx = currentQuestionIndex, total = totalQuestions)
                    Spacer(modifier = Modifier.height(24.dp))

                    val currentQuestion = questions[currentQuestionIndex]

                    ExamQuestionContent(
                        category = "Ujian Esai", // Category dihapus dari DB, kita static dulu
                        questionNumber = "Q${currentQuestionIndex + 1}",
                        questionText = currentQuestion.text,
                        answerText = answerText,
                        onAnswerChange = { viewModel.onJawabanBerubah(it) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ExamNavigationButtons(
                        currentIdx = currentQuestionIndex,
                        total = totalQuestions,
                        onPrevious = { viewModel.soalSebelumnya() },
                        onNext = { viewModel.soalSelanjutnya() },
                        onFinish = {
                            // Fungsi saat klik tombol Akhiri Tes
                            viewModel.kirimSemuaJawabanKeServer(mahasiswaId = 3)
                            onFinishExam()
                        }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text(text = "NAVIGASI SOAL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ExamTextGray)
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(Array(totalQuestions) { it }) { index, _ ->
                        val isCurrent = index == currentQuestionIndex
                        val isAnswered = answeredStatus[index] == true

                        val backgroundColor = when {
                            isCurrent -> ExamDeepBlue
                            isAnswered -> ExamAnsweredGreen
                            else -> ExamCardBorder
                        }

                        val textColor = when {
                            isCurrent || isAnswered -> Color.White
                            else -> ExamDrawerTextBlue
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(backgroundColor)
                                .clickable {
                                    viewModel.rubahIndeksSoal(index)
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) showBottomSheet = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${index + 1}", fontWeight = FontWeight.Bold, color = textColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTopAppBar(timerText: String, onMenuClick: () -> Unit) {
    val isWarning = timerText.startsWith("00:")
    val timerColor = if (isWarning) Color(0xFFDC2626) else ExamOrange

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        title = { Text("Aptitude Test", fontWeight = FontWeight.Bold) },
        actions = {
            Surface(
                color = timerColor,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = timerText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            navigationIconContentColor = ExamDeepBlue,
            titleContentColor = ExamDeepBlue
        )
    )
}

@Composable
fun ExamProgressHeader(currentIdx: Int, total: Int) {
    Column {
        Text(text = "Progress", fontSize = 12.sp, color = ExamTextGray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Question ${currentIdx + 1} of $total", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ExamDeepBlue)
            Text(text = "${((currentIdx + 1).toFloat() / total * 100).toInt()}% Complete", fontSize = 12.sp, color = ExamDeepBlue)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (currentIdx + 1).toFloat() / total },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small),
            color = ExamDeepBlue, trackColor = ExamCardBorder
        )
    }
}

@Composable
fun ExamQuestionContent(
    category: String, questionNumber: String, questionText: String, answerText: String, onAnswerChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), border = BorderStroke(1.dp, ExamCardBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = category, fontSize = 12.sp, color = ExamTextGray)
                Text(text = questionNumber, fontSize = 12.sp, color = ExamTextGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = questionText, fontSize = 16.sp, lineHeight = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = answerText, onValueChange = onAnswerChange,
                modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                placeholder = { Text("Ketik argumen dan jawaban esai Anda di sini...", color = Color.Gray) },
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ExamDeepBlue, unfocusedBorderColor = ExamTextGray)
            )
        }
    }
}

@Composable
fun ExamNavigationButtons(currentIdx: Int, total: Int, onPrevious: () -> Unit, onNext: () -> Unit, onFinish: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        if (currentIdx > 0) {
            Button(onClick = onPrevious, colors = ButtonDefaults.buttonColors(containerColor = ExamCardBorder, contentColor = ExamDeepBlue)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Previous", fontWeight = FontWeight.Medium)
            }
        } else Spacer(modifier = Modifier.width(1.dp))

        if (currentIdx < total - 1) {
            Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = ExamOrange)) {
                Text("Next", fontWeight = FontWeight.Medium, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
            }
        } else {
            Button(onClick = onFinish, colors = ButtonDefaults.buttonColors(containerColor = ExamOrange)) {
                Text("Akhiri Tes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExamBottomNavigationBar(onNavClick: () -> Unit) {
    BottomAppBar(containerColor = Color.White, modifier = Modifier.height(80.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically
        ) {
            ExamBottomNavItem(icon = Icons.Outlined.Home, label = "Mata Kuliah", onClick = { })
            Surface(color = ExamOrange, shape = MaterialTheme.shapes.small) {
                ExamBottomNavItem(icon = Icons.Default.List, label = "Review", tint = Color.White, onClick = { })
            }
            ExamBottomNavItem(icon = Icons.Default.Menu, label = "Navigasi", onClick = onNavClick)
        }
    }
}

@Composable
fun ExamBottomNavItem(icon: ImageVector, label: String, tint: Color = ExamDeepBlue, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint)
        Text(text = label, fontSize = 12.sp, color = tint)
    }
}