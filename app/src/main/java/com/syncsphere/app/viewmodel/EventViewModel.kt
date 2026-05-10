package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.repository.EventRepository
import com.syncsphere.app.models.EventDto
import com.syncsphere.app.ui.common.DemoSeedData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(private val eventRepository: EventRepository) : ViewModel() {

    private val _events = MutableStateFlow<Result<List<EventDto>>?>(null)
    val events: StateFlow<Result<List<EventDto>>?> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = eventRepository.getEvents()
            _events.value = result.fold(
                onSuccess = { remote ->
                    if (remote.isEmpty()) Result.success(DemoSeedData.events) else Result.success(remote)
                },
                onFailure = { Result.success(DemoSeedData.events) }
            )
            _isLoading.value = false
        }
    }
}

