package com.d3if4802.smartexam.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey val questionId: Int,
    val answerText: String
)