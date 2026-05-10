package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.repository.EventRepository
import com.syncsphere.app.models.EventDto
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
            _events.value = eventRepository.getEvents()
            _isLoading.value = false
        }
    }
}

