package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.models.DashboardStatsResponse
import com.syncsphere.app.models.TaskDto
import com.syncsphere.app.repository.TaskRepository
import com.syncsphere.app.ui.common.DemoSeedData
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
            val result = taskRepository.getTasks()
            _tasks.value = result.fold(
                onSuccess = { remote ->
                    if (remote.isEmpty()) Result.success(DemoSeedData.tasks) else Result.success(remote)
                },
                onFailure = { Result.success(DemoSeedData.tasks) }
            )
            _isLoading.value = false
        }
    }

    fun getDashboardStats() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = taskRepository.getDashboardStats()
            _dashboardStats.value = result.fold(
                onSuccess = { Result.success(it) },
                onFailure = {
                    val taskSource = _tasks.value?.getOrNull().orEmpty().ifEmpty { DemoSeedData.tasks }
                    Result.success(DemoSeedData.dashboardStats(taskSource))
                }
            )
            _isLoading.value = false
        }
    }
}

