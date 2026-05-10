package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.models.CreateEventRequest
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

    private val _createEventState = MutableStateFlow<Result<EventDto>?>(null)
    val createEventState: StateFlow<Result<EventDto>?> = _createEventState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _mutationState = MutableStateFlow<Result<EventDto>?>(null)
    val mutationState: StateFlow<Result<EventDto>?> = _mutationState

    private val _deleteState = MutableStateFlow<Result<Unit>?>(null)
    val deleteState: StateFlow<Result<Unit>?> = _deleteState

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

    fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = eventRepository.createEvent(request)
            _createEventState.value = result
            _mutationState.value = result
            result.fold(
                onSuccess = { created ->
                    val current = _events.value?.getOrNull().orEmpty()
                    _events.value = Result.success((listOf(created) + current).sortedBy { it.eventDate })
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to create event"
                }
            )
            _isLoading.value = false
        }
    }

    fun updateEvent(id: String, request: CreateEventRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = eventRepository.updateEvent(id, request)
            _mutationState.value = result
            result.fold(
                onSuccess = { updated ->
                    val current = _events.value?.getOrNull().orEmpty().toMutableList()
                    val index = current.indexOfFirst { it.id == updated.id }
                    if (index >= 0) current[index] = updated else current.add(0, updated)
                    _events.value = Result.success(current.sortedBy { it.eventDate })
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to update event"
                }
            )
            _isLoading.value = false
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = eventRepository.deleteEvent(id)
            _deleteState.value = result
            result.onFailure { error ->
                _errorMessage.value = error.message ?: "Failed to delete event"
            }
            if (result.isSuccess) {
                _events.value = Result.success(_events.value?.getOrNull().orEmpty().filterNot { it.id == id })
            }
            _isLoading.value = false
        }
    }

    fun clearOperationStates() {
        _createEventState.value = null
        _mutationState.value = null
        _deleteState.value = null
        _errorMessage.value = null
    }
}

