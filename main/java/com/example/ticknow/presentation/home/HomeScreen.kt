package com.example.ticknow.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import com.example.ticknow.FanViewModel
import com.example.ticknow.R
import com.example.ticknow.ui.theme.NaranjaItem
import com.example.ticknow.ui.theme.NegroFuerte
import com.example.ticknow.ui.theme.NegroMedio


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(fanViewModel: FanViewModel, navigateToInitialScreen: () -> Unit) {
    val tasks by fanViewModel.tasks.collectAsState()
    val user = fanViewModel.currentUser// Usuario actual autenticado
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) } // Para confirmar tarea a eliminar
    // Cargar tareas cuando se renderiza la pantalla o cambia el usuario
    LaunchedEffect(user) {
        if (user != null) {
            fanViewModel.observeAuthState()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = user?.displayName ?: "Nombre no disponible",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White
                        )
                        Text(
                            text = user?.email ?: "Correo no disponible",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 10.dp),
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    AsyncImage(
                        model = user?.photoUrl ?: R.drawable.default_profile,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .size(40.dp)
                            .clip(CircleShape)

                    )
                },

                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NegroFuerte)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = Color(0xFFC0C0C0),
                contentColor = NegroFuerte,
                elevation = FloatingActionButtonDefaults.elevation(hoveredElevation = 10.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NegroFuerte)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (tasks.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                    Text("No se encontraron tareas")
                }
            } else {

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onDelete = { taskToDelete = task },
                            onToggle = {
                                fanViewModel.toggleTaskCompletion(task.id)
//                                    updatedTask -> fanViewModel.updateTask(updatedTask)
                            }, // Pasa el ID directamente
                            onEdit = { taskToEdit = it },
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, description -> fanViewModel.addTask(title, description) }
        )
    }


    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSaveTask = { updatedTask ->
                fanViewModel.updateTask(updatedTask)
                taskToEdit = null
            }
        )
    }

    // Diálogo para confirmar cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    fanViewModel.logout { navigateToInitialScreen() } // Lógica para cerrar sesión
                }) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para confirmar eliminación de tarea
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Eliminar Tarea") },
            text = { Text("¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    fanViewModel.deleteTask(task.id) // Lógica para eliminar tarea
                    taskToDelete = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }


}


@Composable
fun TaskItem(
    task: Task,
    onDelete: (taskId: String) -> Unit,
    onToggle: (task: Task) -> Unit,
    onEdit: (task: Task) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp, end = 20.dp)
            .background(NaranjaItem, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = {
                onToggle(task) // No copies aquí la tarea, deja que el ViewModel lo maneje
//                onToggle(task.copy(isCompleted = !task.isCompleted))
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = NegroMedio,
                checkedColor = NegroFuerte,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = if (task.isCompleted) {
                    MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                color = NegroFuerte,
                fontWeight = FontWeight.Bold
            )

            if (!task.description.isNullOrBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NegroFuerte
                )
            }
        }
        IconButton(onClick = { onEdit(task) }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar tarea",
                tint = NegroFuerte
            )
        }
        IconButton(onClick = { onDelete(task.id) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar tarea",
                tint = NegroFuerte
            )
        }
    }
}


@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (title: String, description: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Agregar Tarea") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    placeholder = { Text("Título de la tarea") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Descripción opcional") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddTask(title.trim(), description.trim().takeIf { it.isNotBlank() })
                        onDismiss()
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSaveTask: (updatedTask: Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Tarea") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    placeholder = { Text("Título de la tarea") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Descripción opcional") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveTask(
                            task.copy(
                                title = title.trim(),
                                description = description.trim().takeIf { it.isNotBlank() })
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun LogoutIcon(
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showLogoutDialog = true }) {
        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout() // Llamada a la acción de cerrar sesión
                    }) {
                        Text("Cerrar Sesión")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}


@Composable
fun DeleteTaskIcon(
    onDeleteTask: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDeleteDialog = true }) {
        Icon(Icons.Default.Delete, contentDescription = "Eliminar tarea")

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar Tarea") },
                text = { Text("¿Estás seguro de que deseas eliminar esta tarea? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        onDeleteTask() // Llamada a la acción para eliminar la tarea
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

