package com.syncsphere.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.models.UserResponse
import com.syncsphere.app.navigation.Routes
import com.syncsphere.app.ui.components.StatusChip
import com.syncsphere.app.utils.TokenManager
import com.syncsphere.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, taskViewModel: TaskViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val name = TokenManager.getUserName(context) ?: "SyncSphere User"
    val email = TokenManager.getUserEmail(context) ?: "team@syncsphere.app"
    val role = TokenManager.getUserRole(context) ?: "MEMBER"
    val users by taskViewModel.users.collectAsState()
    val isUsersLoading by taskViewModel.isUsersLoading.collectAsState()
    val isPromotingUser by taskViewModel.isPromotingUser.collectAsState()
    val promoteUserState by taskViewModel.promoteUserState.collectAsState()
    val isAdmin = role.equals("ADMIN", ignoreCase = true)

    LaunchedEffect(isAdmin) {
        if (isAdmin) {
            taskViewModel.getUsers()
        }
    }

    LaunchedEffect(promoteUserState) {
        promoteUserState?.fold(
            onSuccess = {
                snackbarHostState.showSnackbar(it)
            },
            onFailure = { error ->
                snackbarHostState.showSnackbar(error.message ?: "Promotion failed")
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Profile") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(54.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(text = name, style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        StatusChip(
                            text = role.uppercase(),
                            color = if (isAdmin) Color(0xFFDC2626) else Color(0xFF2563EB)
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(text = "Organization", style = MaterialTheme.typography.labelLarge)
                            Text(text = "SyncSphere Product Team", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        TokenManager.clearToken(context)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Logout")
                }
            }

            if (isAdmin) {
                item {
                    Text(
                        text = "Team Members",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isUsersLoading) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                } else {
                    val memberList = users?.getOrNull().orEmpty()
                    if (memberList.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "No members yet", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Users will appear here once they sign up.", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    } else {
                        items(memberList, key = { it.id }) { user ->
                            AdminUserCard(
                                user = user,
                                isPromoting = isPromotingUser,
                                onPromote = { taskViewModel.promoteUser(user.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUserCard(
    user: UserResponse,
    isPromoting: Boolean,
    onPromote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = user.fullName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusChip(
                    text = user.role.uppercase(),
                    color = if (user.role.equals("ADMIN", true)) Color(0xFFDC2626) else Color(0xFF2563EB)
                )
            }

            if (!user.role.equals("ADMIN", true)) {
                Button(
                    onClick = onPromote,
                    enabled = !isPromoting
                ) {
                    Text("Promote to Admin")
                }
            }
        }
    }
}

