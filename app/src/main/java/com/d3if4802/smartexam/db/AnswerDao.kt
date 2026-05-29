package com.d3if4802.smartexam.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: AnswerEntity): Long

    @Query("SELECT * FROM answers WHERE questionId = :qId")
    suspend fun getAnswer(qId: Int): AnswerEntity?

    @Query("SELECT * FROM answers")
    fun getAllAnswers(): Flow<List<AnswerEntity>>

    @Query("DELETE FROM answers")
    suspend fun deleteAllAnswers(): Int
}