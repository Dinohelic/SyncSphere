package com.syncsphere.app.ui.tasks

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.syncsphere.app.models.CreateTaskRequest
import com.syncsphere.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(navController: NavController, taskViewModel: TaskViewModel = hiltViewModel()) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var status by remember { mutableStateOf("TODO") }
    var dueDate by remember { mutableStateOf("") }
    var assignedToId by remember { mutableStateOf("") }
    val isLoading by taskViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Task") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Priority") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DDTHH:MM:SSZ)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = assignedToId, onValueChange = { assignedToId = it }, label = { Text("Assign to (User ID)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (title.isNotBlank() && assignedToId.isNotBlank()) {
                        // taskViewModel.createTask(CreateTaskRequest(title, description, priority, status, dueDate, assignedToId))
                        Toast.makeText(context, "Create task logic not implemented", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Task")
                }
            }
        }
    }
}

