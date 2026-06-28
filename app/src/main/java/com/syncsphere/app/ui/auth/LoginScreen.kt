package com.syncsphere.app.ui.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.syncsphere.app.models.LoginRequest
import com.syncsphere.app.navigation.Routes
import com.syncsphere.app.ui.components.PrimaryButton
import com.syncsphere.app.utils.TokenManager
import com.syncsphere.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by authViewModel.isLoading.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val passwordError = password.isBlank()
    val canSubmit = !emailError && !passwordError && !isLoading

    fun submitLogin() {
        focusManager.clearFocus()
        keyboardController?.hide()
        if (canSubmit) {
            authViewModel.login(LoginRequest(email.trim(), password))
        } else {
            val message = when {
                email.isBlank() -> "Please enter your email"
                !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Please enter a valid email address"
                password.isBlank() -> "Please enter your password"
                else -> "Please fix the highlighted fields"
            }
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    LaunchedEffect(loginState) {
        loginState?.let { result ->
            result.onSuccess { authResponse ->
                authResponse.data?.token?.let { token ->
                    TokenManager.saveToken(context, token)
                    TokenManager.saveUserProfile(
                        context = context,
                        fullName = authResponse.data.user?.fullName,
                        email = authResponse.data.user?.email,
                        role = authResponse.data.user?.role
                    )
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            }.onFailure {
                val message = it.message ?: "Login failed"
                scope.launch { snackbarHostState.showSnackbar(message) }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AuthScreenSurface(
                title = "Welcome back",
                subtitle = "Sign in to continue to SyncSphere"
            ) {
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
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
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
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = passwordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitLogin() }
                    )
                )
                if (passwordError) {
                    Text(
                        text = "Password is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
                PrimaryButton(
                    text = "Sign in",
                    onClick = { submitLogin() },
                    loading = isLoading,
                    enabled = canSubmit
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Don't have an account? Sign up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { navController.navigate(Routes.REGISTER) }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

