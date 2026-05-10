package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.models.AnnouncementDto
import com.syncsphere.app.models.CreateAnnouncementRequest
import com.syncsphere.app.repository.AnnouncementRepository
import com.syncsphere.app.ui.common.DemoSeedData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(private val announcementRepository: AnnouncementRepository) : ViewModel() {

    private val _announcements = MutableStateFlow<Result<List<AnnouncementDto>>?>(null)
    val announcements: StateFlow<Result<List<AnnouncementDto>>?> = _announcements

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _createAnnouncementState = MutableStateFlow<Result<AnnouncementDto>?>(null)
    val createAnnouncementState: StateFlow<Result<AnnouncementDto>?> = _createAnnouncementState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getAnnouncements() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = announcementRepository.getAnnouncements()
            _announcements.value = result.fold(
                onSuccess = { remote ->
                    if (remote.isEmpty()) Result.success(DemoSeedData.announcements) else Result.success(remote)
                },
                onFailure = { Result.success(DemoSeedData.announcements) }
            )
            _isLoading.value = false
        }
    }

    fun createAnnouncement(request: CreateAnnouncementRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = announcementRepository.createAnnouncement(request)
            _createAnnouncementState.value = result
            result.fold(
                onSuccess = { created ->
                    val current = _announcements.value?.getOrNull().orEmpty()
                    _announcements.value = Result.success(listOf(created) + current)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to create announcement"
                }
            )
            _isLoading.value = false
        }
    }
}

