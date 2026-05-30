package com.d3if4802.smartexam.data

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

data class Course(
    @SerializedName(value = "id", alternate = ["course_id"]) val id: Int,
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

data class CourseMaterial(
    @SerializedName("materi_id") val materiId: Int,
    @SerializedName("course_id") val courseId: Int,
    @SerializedName("judul_materi") val judulMateri: String,
    @SerializedName("tipe_materi") val tipeMateri: String,
    @SerializedName("file_url") val fileUrl: String?
)

data class Exam(
    @SerializedName("exam_id") val examId: Int,
    @SerializedName("course_id") val courseId: Int,
    @SerializedName("judul_ujian") val judulUjian: String,
    @SerializedName("tipe_ujian") val tipeUjian: String
)

data class Question(
    @SerializedName("question_id") val id: Int,
    @SerializedName("exam_id") val examId: Int,
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

data class CreateAttemptRequest(
    @SerializedName("exam_id") val examId: Int,
    @SerializedName("mahasiswa_id") val mahasiswaId: Int,
    @SerializedName("attempt_number") val attemptNumber: Int,
    @SerializedName("score_absolute") val scoreAbsolute: Int,
    @SerializedName("score_max") val scoreMax: Int,
    @SerializedName("score_percentage") val scorePercentage: Double,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("status") val status: String
)

data class AssessmentResult(
    @SerializedName("assessments_id") val assessmentsId: Int,
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("jawaban_mahasiswa") val jawabanMahasiswa: String,
    @SerializedName("skor_ai") val skorAi: Int?,
    @SerializedName("feedback") val feedback: String?,
    @SerializedName("status_verifikasi") val statusVerifikasi: String?,
    @SerializedName("questions") val question: Question? = null
)

interface ApiService {
    @GET("course?select=*,users(nama_lengkap),enrollments(mahasiswa_id)")
    suspend fun getCourses(): List<Course>

    @GET("materi")
    suspend fun getMaterialsByCourse(@Query("course_id", encoded = true) courseId: String): List<CourseMaterial>

    @GET("exams")
    suspend fun getExamsByCourse(@Query("course_id", encoded = true) courseId: String): List<Exam>

    @GET("questions")
    suspend fun getQuestions(@Query("exam_id", encoded = true) examId: String): List<Question>

    @POST("ai_assessments?on_conflict=mahasiswa_id,question_id")
    suspend fun submitAllAnswers(
        @Body answers: List<SubmitAnswerRequest>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates"
    )

    @PATCH("ai_assessments")
    suspend fun updateAssessmentScore(
        @Query("mahasiswa_id") mId: String,
        @Query("question_id") qId: String,
        @Body payload: Map<String, Any>
    )

    @GET("exam_attempts?select=*&order=attempt_number.desc")
    suspend fun getExamHistory(
        @Query("mahasiswa_id") mahasiswaId: String,
        @Query("exam_id") examId: String
    ): List<ExamAttempt>

    @POST("exam_attempts")
    suspend fun createExamAttempt(@Body attemptData: CreateAttemptRequest)

    @GET("ai_assessments?select=*,questions(*)")
    suspend fun getAssessmentResults(
        @Query("mahasiswa_id") mahasiswaId: String
    ): List<AssessmentResult>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.65.17.192:3000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}