package com.d3if4802.smartexam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.d3if4802.smartexam.BuildConfig
import com.d3if4802.smartexam.data.AssessmentResult
import com.d3if4802.smartexam.data.CourseMaterial
import com.d3if4802.smartexam.data.CreateAttemptRequest
import com.d3if4802.smartexam.data.Exam
import com.d3if4802.smartexam.data.ExamAttempt
import com.d3if4802.smartexam.data.Question
import com.d3if4802.smartexam.data.RegisterRequest
import com.d3if4802.smartexam.data.RetrofitClient
import com.d3if4802.smartexam.data.SubmitAnswerRequest
import com.d3if4802.smartexam.data.UpdateAttemptRequest
import com.d3if4802.smartexam.data.UpdateScoreRequest
import com.d3if4802.smartexam.db.AnswerDao
import com.d3if4802.smartexam.db.AnswerEntity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamViewModel(private val answerDao: AnswerDao) : ViewModel() {

    val currentUserId = MutableStateFlow(0)
    val authErrorMessage = MutableStateFlow<String?>(null)

    private val _materialList = MutableStateFlow<List<CourseMaterial>>(emptyList())
    val materialList: StateFlow<List<CourseMaterial>> = _materialList.asStateFlow()

    private val _examList = MutableStateFlow<List<Exam>>(emptyList())
    val examList: StateFlow<List<Exam>> = _examList.asStateFlow()

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

    private val _historyList = MutableStateFlow<List<ExamAttempt>>(emptyList())
    val historyList: StateFlow<List<ExamAttempt>> = _historyList.asStateFlow()

    private val _assessmentResults = MutableStateFlow<List<AssessmentResult>>(emptyList())
    val assessmentResults: StateFlow<List<AssessmentResult>> = _assessmentResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var activeExamId: Int = 0
    private var timerJob: Job? = null
    private var isSubmitting = false

    init {
        fetchAllAnswers()
    }

    fun register(emailInput: String, usernameInput: String, passwordInput: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            authErrorMessage.value = null
            try {
                val payload = RegisterRequest(
                    username = usernameInput,
                    email = emailInput,
                    password = passwordInput
                )
                RetrofitClient.apiService.registerUser(payload)
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                authErrorMessage.value = "Gagal mendaftar: Pastikan Email/Username belum terpakai."
                Log.e("CEK_API", "Error Register: ${e.message}")
            }
        }
    }

    fun login(emailOrUsername: String, passwordInput: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            authErrorMessage.value = null
            try {
                val queryOr = "(email.eq.$emailOrUsername,username.eq.$emailOrUsername)"
                val queryPassword = "eq.$passwordInput"

                val response = RetrofitClient.apiService.loginUser(
                    emailOrUsername = queryOr,
                    password = queryPassword
                )

                _isLoading.value = false

                if (response.isNotEmpty()) {
                    val loggedInUser = response.first()
                    currentUserId.value = loggedInUser.user_id
                    onSuccess()
                } else {
                    authErrorMessage.value = "Email/Username atau Password salah."
                }
            } catch (e: Exception) {
                _isLoading.value = false
                authErrorMessage.value = "Terjadi kesalahan jaringan."
                Log.e("CEK_API", "Error Login: ${e.message}")
            }
        }
    }

    fun fetchMaterialsByCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                val filter = "eq.$courseId"
                val response = RetrofitClient.apiService.getMaterialsByCourse(filter)
                _materialList.value = response
            } catch (e: Exception) {
                Log.e("CEK_API", "Error: ${e.message}")
            }
        }
    }

    fun fetchExamsByCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                val filter = "eq.$courseId"
                val response = RetrofitClient.apiService.getExamsByCourse(filter)
                _examList.value = response
            } catch (e: Exception) {
                Log.e("CEK_API", "Error: ${e.message}")
            }
        }
    }

    fun fetchExamHistory(mahasiswaId: Int, examId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getExamHistory(
                    mahasiswaId = "eq.$mahasiswaId",
                    examId = "eq.$examId"
                )
                _historyList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchSingleExam(examId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getExamById("eq.$examId")
                if (response.isNotEmpty()) {
                    val examDariDb = response.first()
                    val listSekarang = _examList.value.toMutableList()
                    val index = listSekarang.indexOfFirst { it.examId == examId }
                    if (index != -1) {
                        listSekarang[index] = examDariDb
                    } else {
                        listSekarang.add(examDariDb)
                    }
                    _examList.value = listSekarang
                }
            } catch (e: Exception) {
                Log.e("CEK_API", "Error ambil 1 ujian: ${e.message}")
            }
        }
    }

    fun fetchQuestionsFromServer(examId: Int) {
        viewModelScope.launch {
            try {
                answerDao.deleteAllAnswers()
                _jawabanState.value = ""
                _currentQuestionIndex.value = 0
                isSubmitting = false
                _isLoading.value = false
                timerJob?.cancel()

                val examResponse = RetrofitClient.apiService.getExamById("eq.$examId")
                val durasiDosen = examResponse.firstOrNull()?.durasiMenit

                val response = RetrofitClient.apiService.getQuestions("eq.$examId")
                _questions.value = response

                if (response.isNotEmpty()) {
                    loadJawaban(0)
                    startTimer(durasiDosen)
                }
            } catch (e: Exception) {
                Log.e("CEK_API", "Error: $e")
            }
        }
    }

    fun fetchAssessmentResults(mahasiswaId: Int) {
        viewModelScope.launch {
            try {
                val filter = "eq.$mahasiswaId"
                val response = RetrofitClient.apiService.getAssessmentResults(filter)
                _assessmentResults.value = response
            } catch (e: Exception) {
                Log.e("CEK_API", "Error: ${e.message}")
            }
        }
    }

    fun startTimer(durasiMenit: Int?) {
        timerJob?.cancel()
        _isTimeUp.value = false

        if (durasiMenit == null || durasiMenit <= 0) {
            _timeLeftString.value = "Tanpa Waktu"
            return
        }

        timerJob = viewModelScope.launch {
            var timeInSeconds = durasiMenit * 60
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

    fun resetExamState() {
        _currentQuestionIndex.value = 0
        _jawabanState.value = ""
        _isTimeUp.value = false
    }

    fun resetExam() {
        viewModelScope.launch {
            answerDao.deleteAllAnswers()
            _jawabanState.value = ""
            _currentQuestionIndex.value = 0
            _questions.value = emptyList()
            timerJob?.cancel()
            isSubmitting = false
            _isLoading.value = false
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

    fun kirimSemuaJawabanKeServer(mahasiswaId: Int) {
        if (isSubmitting) return
        isSubmitting = true
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val localAnswers = _allAnswers.value
                val payload = localAnswers.map { answerEntity ->
                    SubmitAnswerRequest(
                        mahasiswaId = mahasiswaId,
                        questionId = answerEntity.questionId,
                        jawabanMahasiswa = answerEntity.answerText
                    )
                }

                if (payload.isNotEmpty()) {
                    RetrofitClient.apiService.submitAllAnswers(payload, "resolution=merge-duplicates")
                }

                val totalMaxScore = _questions.value.sumOf { it.skorMaksimal ?: 100 }
                val percobaanKe = _historyList.value.size + 1

                val historyPayload = CreateAttemptRequest(
                    examId = activeExamId,
                    mahasiswaId = mahasiswaId,
                    attemptNumber = percobaanKe,
                    scoreAbsolute = 0,
                    scoreMax = totalMaxScore,
                    scorePercentage = 0.0,
                    ipAddress = "192.168.0.55",
                    status = "Belum Ditinjau"
                )

                RetrofitClient.apiService.createExamAttempt(historyPayload)

                nilaiSemuaJawabanDenganAI(mahasiswaId, percobaanKe, totalMaxScore)

            } catch (e: Exception) {
                Log.e("CEK_API", "Error: ${e.message}")
                isSubmitting = false
                _isLoading.value = false
            }
        }
    }

    private fun nilaiSemuaJawabanDenganAI(mahasiswaId: Int, percobaanKe: Int, totalMaxScore: Int) {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank()) {
                    Log.e("CEK_AI", "API Key KOSONG")
                    isSubmitting = false
                    _isLoading.value = false
                    return@launch
                }

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = apiKey
                )

                val localAnswers = _allAnswers.value
                val daftarSoal = _questions.value
                var totalSkorDiperoleh = 0

                for (jawaban in localAnswers) {
                    val soalTerkait = daftarSoal.find { it.id == jawaban.questionId }
                    if (soalTerkait != null) {
                        val maxSkor = soalTerkait.skorMaksimal ?: 100

                        val prompt = """
                            Anda adalah sistem penilai ujian otomatis yang kaku. 
                            Tugas Anda HANYA memberikan skor dan feedback berdasarkan data berikut:
                            
                            [SOAL]: ${soalTerkait.text}
                            [KUNCI JAWABAN]: ${soalTerkait.kunciJawaban ?: "Nilai berdasarkan keakuratan dan logika."}
                            [RUBRIK PENILAIAN]: ${soalTerkait.rubrikPenilaian ?: "Skor maksimal adalah $maxSkor."}
                            [JAWABAN MAHASISWA]: ${jawaban.answerText}
                            
                            ATURAN WAJIB (JIKA DILANGGAR SISTEM AKAN ERROR):
                            1. DILARANG KERAS menggunakan simbol bintang (*), *bold, atau markdown apapun.
                            2. DILARANG menambahkan kalimat sapaan, penjelasan tambahan, atau teks penutup.
                            3. HANYA keluarkan 2 baris teks persis seperti format di bawah ini.
                            
                            SKOR: [Isi dengan 1 angka bulat antara 0 sampai $maxSkor]
                            FEEDBACK: [Isi dengan 1-2 kalimat objektif alasan penilaian]
                        """.trimIndent()

                        val response = generativeModel.generateContent(prompt)
                        val aiResult = response.text?.replace("*", "")?.trim() ?: ""

                        var skorAi = 0
                        var feedbackAi = "Gagal memproses feedback"

                        val regexSkor = Regex("(?i)SKOR:\\s*(\\d+)")
                        val regexFeedback = Regex("(?i)FEEDBACK:\\s*(.+)", RegexOption.DOT_MATCHES_ALL)

                        val matchSkor = regexSkor.find(aiResult)
                        val matchFeedback = regexFeedback.find(aiResult)

                        if (matchSkor != null && matchFeedback != null) {
                            skorAi = matchSkor.groupValues[1].toIntOrNull() ?: 0
                            feedbackAi = matchFeedback.groupValues[1].trim()
                            totalSkorDiperoleh += skorAi
                        } else {
                            feedbackAi = "Catatan Sistem: Format balasan AI salah -> $aiResult"
                        }

                        updateSkorKeServer(mahasiswaId, soalTerkait.id, skorAi, feedbackAi, "Dinilai AI")
                    }
                }

                val persentase = if (totalMaxScore > 0) (totalSkorDiperoleh.toDouble() / totalMaxScore.toDouble()) * 100.0 else 0.0

                val statusUjian = if (persentase >= 70.0) {
                    "Selesai"
                } else {
                    "Belum Ditinjau"
                }

                val updatePayload = UpdateAttemptRequest(
                    scoreAbsolute = totalSkorDiperoleh,
                    scorePercentage = persentase,
                    status = statusUjian
                )

                RetrofitClient.apiService.updateExamAttempt(
                    mId = "eq.$mahasiswaId",
                    examId = "eq.$activeExamId",
                    attemptNum = "eq.$percobaanKe",
                    payload = updatePayload
                )

                isSubmitting = false
                _isLoading.value = false

            } catch (e: Exception) {
                Log.e("CEK_AI", "ERROR FATAL: ${e.message}")
                isSubmitting = false
                _isLoading.value = false
            }
        }
    }

    fun updateSkorKeServer(mahasiswaId: Int, questionId: Int, skorAi: Int, feedback: String, status: String) {
        viewModelScope.launch {
            try {
                val payload = UpdateScoreRequest(
                    skorAi = skorAi,
                    feedback = feedback,
                    statusVerifikasi = status
                )

                RetrofitClient.apiService.updateAssessmentScore(
                    mId = "eq.$mahasiswaId",
                    qId = "eq.$questionId",
                    payload = payload
                )
            } catch (e: Exception) {
                Log.e("CEK_AI", "7. Gagal Update DB: ${e.message}")
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