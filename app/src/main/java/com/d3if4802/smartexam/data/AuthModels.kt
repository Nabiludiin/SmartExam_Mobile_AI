package com.d3if4802.smartexam.data

data class User(
    val user_id: Int,
    val username: String,
    val email: String?,
    val nama_lengkap: String?,
    val role: String,
    val terdaftar_pada: String?
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val nama_lengkap: String,
    val role: String = "mahasiswa"
)