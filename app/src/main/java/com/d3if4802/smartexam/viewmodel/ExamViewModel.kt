package com.d3if4802.smartexam.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d3if4802.smartexam.data.AppDatabase
import com.d3if4802.smartexam.data.DraftEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class sederhana untuk menampung pertanyaan
data class Question(
    val id: Int,
    val category: String,
    val text: String
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {
    private val draftDao = AppDatabase.getDatabase(application).draftDao()

    // --- LOGIKA 10 SOAL ---
    private val _currentQuestionIndex = MutableStateFlow(0) // Indeks soal saat ini (0-9)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    // Bank Soal: Saya buatkan 10 soal profesional/etika esai
    val questions = listOf(
        Question(1, "Aptitude Test: Ethics", "Apa yang dimaksud dengan integritas dalam lingkungan kerja profesional?"),
        Question(2, "Aptitude Test: Collaboration", "Jelaskan pentingnya kerja sama tim dalam menyelesaikan proyek besar!"),
        Question(3, "Professional Development", "Sebutkan strategi Anda untuk meningkatkan kompetensi diri di era digital!"),
        Question(4, "Decision Making", "Bagaimana Anda menangani konflik kepentingan antara klien dan perusahaan?"),
        Question(5, "Analytical Thinking", "Analisis dampak penggunaan AI dalam efisiensi administrasi kantoran!"),
        Question(6, "Leadership", "Menurut Anda, kualitas kepemimpinan apa yang paling krusial di masa krisis?"),
        Question(7, "Confidentiality", "Seberapa penting menjaga kerahasiaan data klien dalam pekerjaan esai Anda?"),
        Question(8, "Time Management", "Bagaimana Anda mengatur prioritas saat menghadapi beberapa *deadline* sekaligus?"),
        Question(9, "Critical Reasoning", "Jelaskan pandangan Anda mengenai keseimbangan antara keuntungan perusahaan dan tanggung jawab sosial!"),
        Question(10, "Risk Management", "Bagaimana langkah Anda dalam mitigasi risiko kesalahan dalam penyusunan laporan keuangan?")
    )

    // --- LOGIKA JAWABAN (AUTOSAVE) ---
    private val _jawabanState = MutableStateFlow("")
    val jawabanState: StateFlow<String> = _jawabanState

    // --- LOGIKA TIMER MUNDUR (45 MENIT) ---
    private val _timeLeftString = MutableStateFlow("45:00")
    val timeLeftString: StateFlow<String> = _timeLeftString
    private var countDownTimer: CountDownTimer? = null

    init {
        // Saat ViewModel dibuat, load soal nomor 1 (indeks 0) dan mulai timer
        muatSoalAtauDraft(0)
        startCountdown()
    }

    // Fungsi untuk navigasi cepat (klik dari sidebar)
    fun rubahIndeksSoal(indeksBaru: Int) {
        if (indeksBaru in 0..9) {
            _currentQuestionIndex.value = indeksBaru
            muatSoalAtauDraft(indeksBaru)
        }
    }

    fun soalSelanjutnya() {
        if (_currentQuestionIndex.value < 9) {
            val indeksBaru = _currentQuestionIndex.value + 1
            rubahIndeksSoal(indeksBaru)
        }
    }

    fun soalSebelumnya() {
        if (_currentQuestionIndex.value > 0) {
            val indeksBaru = _currentQuestionIndex.value - 1
            rubahIndeksSoal(indeksBaru)
        }
    }

    private fun muatSoalAtauDraft(index: Int) {
        val soalId = questions[index].id
        viewModelScope.launch {
            // Ambil draf yang spesifik untuk soalId ini
            val savedDraft = draftDao.ambilDraft(soalId)
            _jawabanState.value = savedDraft ?: ""
        }
    }

    // Dipanggil setiap kali user mengetik 1 huruf di layar
    fun onJawabanBerubah(teksBaru: String) {
        _jawabanState.value = teksBaru
        val soalId = questions[_currentQuestionIndex.value].id
        simpanKeRoom(soalId, teksBaru) // Langsung amankan ke SQLite!
    }

    private fun simpanKeRoom(soalId: Int, teks: String) {
        viewModelScope.launch {
            val draft = DraftEntity(
                idSoal = soalId, // Tautkan jawaban ke ID soal yang benar
                teksJawaban = teks,
                waktuTersimpan = System.currentTimeMillis()
            )
            draftDao.simpanDraft(draft)
        }
    }

    // Logika Timer Mundur
    private fun startCountdown() {
        // 45 menit = 45 * 60 * 1000 milidetik
        countDownTimer = object : CountDownTimer(45 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                // Format waktu MM:SS (misal 05:08)
                _timeLeftString.value = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _timeLeftString.value = "00:00"
                // TODO: Logika saat waktu habis (misal: otomatis kumpulkan)
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}