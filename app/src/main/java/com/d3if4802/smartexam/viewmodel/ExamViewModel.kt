package com.d3if4802.smartexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.d3if4802.smartexam.data.Question
import com.d3if4802.smartexam.data.RetrofitClient
import com.d3if4802.smartexam.db.AnswerDao
import com.d3if4802.smartexam.db.AnswerEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamViewModel(private val answerDao: AnswerDao) : ViewModel() {

    // 1. STATE UNTUK MENYIMPAN SOAL DARI SERVER
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _jawabanState = MutableStateFlow("")
    val jawabanState: StateFlow<String> = _jawabanState.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _timeLeftString = MutableStateFlow("45:00")
    val timeLeftString: StateFlow<String> = _timeLeftString.asStateFlow()

    private val _allAnswers = MutableStateFlow<List<AnswerEntity>>(emptyList())
    val allAnswers: StateFlow<List<AnswerEntity>> = _allAnswers.asStateFlow()

    private val _isTimeUp = MutableStateFlow(false)
    val isTimeUp: StateFlow<Boolean> = _isTimeUp.asStateFlow()

    private var timerJob: Job? = null

    init {
        // 2. OTOMATIS AMBIL SOAL SAAT MASUK KE LAYAR UJIAN
        fetchQuestionsFromServer()
        fetchAllAnswers()
    }

    // 3. FUNGSI UNTUK MENYEDOT DATA DARI POSTGREST
    private fun fetchQuestionsFromServer() {
        viewModelScope.launch {
            try {
                // Tarik data dari database lewat RetrofitClient
                val response = RetrofitClient.apiService.getQuestions()
                _questions.value = response

                // Jika soal berhasil didapat, langsung buka soal pertama dan jalankan timer
                if (response.isNotEmpty()) {
                    loadJawaban(0)
                    startTimer()
                }
            } catch (e: Exception) {
                // Pantau Logcat jika IP salah atau koneksi terputus
                e.printStackTrace()
            }
        }
    }

    fun startTimer() {
        timerJob?.cancel()
        _isTimeUp.value = false
        timerJob = viewModelScope.launch {
            var timeInSeconds = 45 * 60 // 45 Menit
            while (timeInSeconds > 0) {
                delay(1000L)
                timeInSeconds--
                val minutes = timeInSeconds / 60
                val seconds = timeInSeconds % 60
                _timeLeftString.value = String.format("%02d:%02d", minutes, seconds)
            }
            _isTimeUp.value = true
        }
    }

    fun resetExam() {
        viewModelScope.launch {
            answerDao.deleteAllAnswers()
            _jawabanState.value = ""
            _currentQuestionIndex.value = 0
            startTimer()
        }
    }

    private fun fetchAllAnswers() {
        viewModelScope.launch {
            answerDao.getAllAnswers().collect { list ->
                _allAnswers.value = list
            }
        }
    }

    fun onJawabanBerubah(jawabanBaru: String) {
        _jawabanState.value = jawabanBaru
        simpanJawaban(jawabanBaru)
    }

    fun rubahIndeksSoal(indeksBaru: Int) {
        val currentQuestions = _questions.value
        // Cek validasi array agar aplikasi tidak force close
        if (currentQuestions.isNotEmpty() && indeksBaru in currentQuestions.indices) {
            _currentQuestionIndex.value = indeksBaru
            loadJawaban(indeksBaru)
        }
    }

    fun soalSebelumnya() {
        rubahIndeksSoal(_currentQuestionIndex.value - 1)
    }

    fun soalSelanjutnya() {
        rubahIndeksSoal(_currentQuestionIndex.value + 1)
    }

    private fun simpanJawaban(jawaban: String) {
        viewModelScope.launch {
            val currentQuestions = _questions.value
            if (currentQuestions.isNotEmpty()) {
                val entity = AnswerEntity(
                    questionId = currentQuestions[_currentQuestionIndex.value].id,
                    answerText = jawaban
                )
                answerDao.insertAnswer(entity)
            }
        }
    }

    private fun loadJawaban(indeks: Int) {
        viewModelScope.launch {
            val currentQuestions = _questions.value
            if (currentQuestions.isNotEmpty()) {
                val qId = currentQuestions[indeks].id
                val savedAnswer = answerDao.getAnswer(qId)
                _jawabanState.value = savedAnswer?.answerText ?: ""
            }
        }
    }

    // 4. FUNGSI UNTUK MENGIRIM SEMUA JAWABAN KE SERVER SAAT UJIAN SELESAI
    fun kirimSemuaJawabanKeServer(mahasiswaId: Int) {
        viewModelScope.launch {
            try {
                val localAnswers = _allAnswers.value
                val payload = localAnswers.map { answerEntity ->
                    com.d3if4802.smartexam.data.SubmitAnswerRequest(
                        mahasiswaId = mahasiswaId,
                        questionId = answerEntity.questionId,
                        jawabanMahasiswa = answerEntity.answerText
                    )
                }
                if (payload.isNotEmpty()) {
                    RetrofitClient.apiService.submitAllAnswers(payload)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ExamViewModelFactory(private val answerDao: AnswerDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExamViewModel(answerDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}