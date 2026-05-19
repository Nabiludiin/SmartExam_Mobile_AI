package com.d3if4802.smartexam.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_jawaban")
data class DraftEntity(
    @PrimaryKey val idSoal: Int,
    val teksJawaban: String,
    val waktuTersimpan: Long
)