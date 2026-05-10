package com.syncsphere.app.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syncsphere.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(taskViewModel: TaskViewModel = hiltViewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()
    val isLoading by taskViewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        taskViewModel.getTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tasks") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                tasks?.onSuccess { taskList ->
                    LazyColumn {
                        items(taskList.filter { it.title.contains(searchQuery, ignoreCase = true) }) { task ->
                            TaskCard(task = task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: com.syncsphere.app.models.TaskDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            task.description?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Status: ${task.status}", style = MaterialTheme.typography.bodySmall)
            }
            task.dueDate?.let { Text(text = "Due: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

