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
import com.d3if4802.smartexam.data.RetrofitClient
import com.d3if4802.smartexam.data.SubmitAnswerRequest
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

    var activeExamId: Int = 0
    private var timerJob: Job? = null
    private var isSubmitting = false

    init {
        fetchAllAnswers()
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

    fun fetchQuestionsFromServer(examFilter: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getQuestions(examFilter)
                _questions.value = response

                if (response.isNotEmpty()) {
                    loadJawaban(0)
                    startTimer()
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

    fun startTimer() {
        timerJob?.cancel()
        _isTimeUp.value = false
        timerJob = viewModelScope.launch {
            var timeInSeconds = 45 * 60
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
            _questions.value = emptyList()
            timerJob?.cancel()
            isSubmitting = false
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

                val percobaanKe = _historyList.value.size + 1
                val historyPayload = CreateAttemptRequest(
                    examId = activeExamId,
                    mahasiswaId = mahasiswaId,
                    attemptNumber = percobaanKe,
                    scoreAbsolute = 0,
                    scoreMax = 50,
                    scorePercentage = 0.0,
                    ipAddress = "192.168.0.55",
                    status = "Belum Ditinjau"
                )

                RetrofitClient.apiService.createExamAttempt(historyPayload)

                nilaiSemuaJawabanDenganAI(mahasiswaId)

            } catch (e: Exception) {
                Log.e("CEK_API", "Error: ${e.message}")
            }
        }
    }

    private fun nilaiSemuaJawabanDenganAI(mahasiswaId: Int) {
        viewModelScope.launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                val localAnswers = _allAnswers.value
                val daftarSoal = _questions.value

                for (jawaban in localAnswers) {
                    val soalTerkait = daftarSoal.find { it.id == jawaban.questionId }
                    if (soalTerkait != null) {
                        val prompt = """
                            Kamu adalah dosen penguji yang profesional. Tugasmu menilai jawaban mahasiswa.
                            
                            SOAL: ${soalTerkait.text}
                            KUNCI JAWABAN: ${soalTerkait.kunciJawaban ?: "Gunakan pengetahuan umum"}
                            RUBRIK: ${soalTerkait.rubrikPenilaian ?: "Nilai maksimal 50"}
                            JAWABAN MAHASISWA: ${jawaban.answerText}
                            
                            ATURAN SUPER KETAT: Jawab HANYA dengan format persis di bawah ini, TANPA MARKDOWN atau simbol bintang (**) sama sekali:
                            SKOR: [Tulis angka bulat saja antara 0 sampai 50]
                            FEEDBACK: [Berikan 1-2 kalimat alasan objektif kenapa nilainya segitu]
                        """.trimIndent()

                        val response = generativeModel.generateContent(prompt)
                        val aiResult = response.text?.replace("*", "") ?: ""

                        var skorAi = 0
                        var feedbackAi = "Gagal memproses feedback"

                        if (aiResult.contains("SKOR:") && aiResult.contains("FEEDBACK:")) {
                            try {
                                val skorString = aiResult.substringAfter("SKOR:").substringBefore("FEEDBACK:").trim()
                                skorAi = skorString.toIntOrNull() ?: 0
                                feedbackAi = aiResult.substringAfter("FEEDBACK:").trim()
                            } catch (e: Exception) {
                                Log.e("CEK_AI", "Gagal parsing: $aiResult")
                            }
                        }

                        updateSkorKeServer(
                            mahasiswaId = mahasiswaId,
                            questionId = soalTerkait.id,
                            skorAi = skorAi,
                            feedback = feedbackAi,
                            status = "Dinilai AI"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("CEK_AI", "Error Gemini: ${e.message}")
            }
        }
    }

    fun updateSkorKeServer(mahasiswaId: Int, questionId: Int, skorAi: Int, feedback: String, status: String) {
        viewModelScope.launch {
            try {
                val payload = mapOf(
                    "skor_ai" to skorAi,
                    "feedback" to feedback,
                    "status_verifikasi" to status
                )
                RetrofitClient.apiService.updateAssessmentScore(
                    mId = "eq.$mahasiswaId",
                    qId = "eq.$questionId",
                    payload = payload
                )
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