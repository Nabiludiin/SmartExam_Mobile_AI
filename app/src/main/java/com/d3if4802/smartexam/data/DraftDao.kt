package com.d3if4802.smartexam.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun simpanDraft(draft: DraftEntity): Long

    @Query("SELECT teksJawaban FROM draft_jawaban WHERE idSoal = :soalId LIMIT 1")
    suspend fun ambilDraft(soalId: Int): String?
}