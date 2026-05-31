package com.d3if4802.smartexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.d3if4802.smartexam.db.AppDatabase
import com.d3if4802.smartexam.ui.AppNavigation
import com.d3if4802.smartexam.ui.theme.SmartExamTheme
import com.d3if4802.smartexam.viewmodel.ExamViewModel
import com.d3if4802.smartexam.viewmodel.ExamViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val viewModelFactory = ExamViewModelFactory(database.answerDao())
        val viewModel = ViewModelProvider(this, viewModelFactory)[ExamViewModel::class.java]

        setContent {
            SmartExamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}