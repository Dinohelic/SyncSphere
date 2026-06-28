package com.syncsphere.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.syncsphere.app.models.RegisterRequest
import com.syncsphere.app.navigation.Routes
import com.syncsphere.app.ui.components.PrimaryButton
import com.syncsphere.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading by authViewModel.isLoading.collectAsState()
    val registerState by authViewModel.registerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val fullNameError = fullName.isBlank() || fullName.trim().length < 2
    val emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val passwordError = password.isBlank() || password.length < 6
    val confirmPasswordError = confirmPassword.isBlank() || confirmPassword != password
    val canSubmit = !fullNameError && !emailError && !passwordError && !confirmPasswordError && !isLoading

    fun submitRegister() {
        focusManager.clearFocus()
        keyboardController?.hide()
        if (canSubmit) {
            authViewModel.register(RegisterRequest(fullName.trim(), email.trim(), password, role = "MEMBER"))
        } else {
            val message = when {
                fullNameError -> "Please enter your full name (minimum 2 characters)"
                emailError -> "Please enter a valid email address"
                passwordError -> "Password must contain at least 6 characters"
                confirmPasswordError -> "Passwords do not match"
                else -> "Please fix the highlighted fields"
            }
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    LaunchedEffect(registerState) {
        registerState?.let { result ->
            result.onSuccess {
                scope.launch { snackbarHostState.showSnackbar("Account created successfully") }
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.REGISTER) { inclusive = true }
                }
            }.onFailure {
                val msg = it.message ?: "Unable to connect. Please try again."
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AuthScreenSurface(
                title = "Create account",
                subtitle = "Set up your SyncSphere workspace in a few steps"
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = fullNameError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                if (fullNameError) {
                    Text(
                        text = "Full name is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                if (emailError) {
                    Text(
                        text = "Email is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    isError = passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )
                if (passwordError) {
                    Text(
                        text = "Password is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    isError = confirmPasswordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitRegister() }),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )
                if (confirmPasswordError) {
                    val message = if (confirmPassword.isBlank()) {
                        "Confirm password is required"
                    } else {
                        "Passwords do not match"
                    }
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
                PrimaryButton(
                    text = "Create account",
                    onClick = { submitRegister() },
                    loading = isLoading,
                    enabled = canSubmit
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Already have an account? Sign in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { navController.navigate(Routes.LOGIN) }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

