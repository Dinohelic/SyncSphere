package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.models.DashboardStatsResponse
import com.syncsphere.app.models.TaskDto
import com.syncsphere.app.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<Result<List<TaskDto>>?>(null)
    val tasks: StateFlow<Result<List<TaskDto>>?> = _tasks

    private val _dashboardStats = MutableStateFlow<Result<DashboardStatsResponse>?>(null)
    val dashboardStats: StateFlow<Result<DashboardStatsResponse>?> = _dashboardStats

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _tasks.value = taskRepository.getTasks()
            _isLoading.value = false
        }
    }

    fun getDashboardStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _dashboardStats.value = taskRepository.getDashboardStats()
            _isLoading.value = false
        }
    }
}

