package com.d3if4802.smartexam.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncAnswersWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("OFFLINE_SYNC", "Koneksi internet kembali! Memulai sinkronisasi jawaban offline...")
            Result.success()
        } catch (e: Exception) {
            Log.e("OFFLINE_SYNC", "Gagal melakukan sinkronisasi: ${e.message}")
            Result.retry() // Coba lagi nanti jika gagal
        }
    }
}