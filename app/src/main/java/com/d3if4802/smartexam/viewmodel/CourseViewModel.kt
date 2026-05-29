package com.d3if4802.smartexam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if4802.smartexam.data.RetrofitClient
// PERBAIKAN: Ubah import ke package data agar sesuai dengan model dari Retrofit
import com.d3if4802.smartexam.data.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseViewModel : ViewModel() {

    private val _courseList = MutableStateFlow<List<Course>>(emptyList())
    val courseList: StateFlow<List<Course>> = _courseList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchCoursesFromApi()
    }

    fun fetchCoursesFromApi() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getCourses()
                _courseList.value = response
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}