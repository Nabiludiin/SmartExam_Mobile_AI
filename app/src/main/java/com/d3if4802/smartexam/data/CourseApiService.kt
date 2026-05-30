package com.d3if4802.smartexam.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

// ==========================================
// 1. KUMPULAN DATA CLASS (MODEL POSTGRESQL)
// ==========================================

data class Course(
    val id: Int,
    @SerializedName("nama_matkul") val namaMatkul: String,
    @SerializedName("kode_matkul") val kodeMatkul: String,
    @SerializedName("dosen_id") val dosenId: Int?,
    @SerializedName("kategori") val kategori: String? = "Lainnya",
    val users: UserData? = null,
    @SerializedName("enrollments") val dataEnrollment: List<DataEnrollment>? = null
) {
    val jumlahMahasiswa: Int
        get() = dataEnrollment?.size ?: 0
}

data class UserData(
    @SerializedName("nama_lengkap") val nama_lengkap: String
)

data class Question(
    @SerializedName("question_id") val id: Int,
    @SerializedName("teks_soal") val text: String,
    @SerializedName("kunci_jawaban") val kunciJawaban: String?,
    @SerializedName("rubrik_penilaian") val rubrikPenilaian: String?
)

data class DataEnrollment(
    @SerializedName("mahasiswa_id") val mahasiswaId: Int
)

data class SubmitAnswerRequest(
    @SerializedName("mahasiswa_id") val mahasiswaId: Int,
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("jawaban_mahasiswa") val jawabanMahasiswa: String
)

data class ExamAttempt(
    @SerializedName("attempt_id") val attemptId: Int,
    @SerializedName("attempt_number") val attemptNumber: Int,
    @SerializedName("score_absolute") val scoreAbsolute: Int,
    @SerializedName("score_max") val scoreMax: Int,
    @SerializedName("score_percentage") val scorePercentage: Double,
    @SerializedName("tanggal_mulai") val tanggalMulai: String,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("status") val status: String
)

// ==========================================
// 2. SATU INTERFACE UNTUK SEMUA API
// ==========================================
interface ApiService {
    @GET("course?select=*,users(nama_lengkap),enrollments(mahasiswa_id)")
    suspend fun getCourses(): List<Course>

    @GET("questions")
    suspend fun getQuestions(): List<Question>

    @POST("ai_assessments")
    suspend fun submitAllAnswers(@Body answers: List<SubmitAnswerRequest>)

    @PATCH("ai_assessments")
    suspend fun updateAssessmentScore(
        @Query("question_id") qId: String,
        @Body payload: Map<String, Any>
    )
    @GET("exam_attempts?select=*&order=attempt_number.desc")
    suspend fun getExamHistory(
        @Query("mahasiswa_id") mahasiswaId: String,
        @Query("exam_id") examId: String
    ): List<ExamAttempt>
}

// ==========================================
// 3. RETROFIT CLIENT (JEMBATAN UTAMA)
// ==========================================
object RetrofitClient {
    // Ganti dengan IP aslimu yang baru
    private const val BASE_URL = "http://10.65.17.192:3000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}