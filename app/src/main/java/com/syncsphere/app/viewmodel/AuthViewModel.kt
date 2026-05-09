package com.syncsphere.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syncsphere.app.models.AuthResponse
import com.syncsphere.app.models.LoginRequest
import com.syncsphere.app.models.RegisterRequest
import com.syncsphere.app.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<AuthResponse>?>(null)
    val loginState: StateFlow<Result<AuthResponse>?> = _loginState

    private val _registerState = MutableStateFlow<Result<AuthResponse>?>(null)
    val registerState: StateFlow<Result<AuthResponse>?> = _registerState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginState.value = authRepository.login(loginRequest)
            _isLoading.value = false
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _registerState.value = authRepository.register(registerRequest)
            _isLoading.value = false
        }
    }
}

