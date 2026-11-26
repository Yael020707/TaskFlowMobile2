package com.example.taskflowmobile2.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflowmobile2.data.local.entities.CategoriaEntity
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.ui.theme.TaskFlowMobile2Theme
import com.example.taskflowmobile2.ui.viewmodel.TaskUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(
    taskId: Long,
    uiState: TaskUiState,
    onBackClick: () -> Unit,
    onSelectTask: (Long) -> Unit,
    onToggleTaskCompletion: (TareaEntity) -> Unit,
    onUpdateTask: (TareaEntity) -> Unit,
    onAddHito: (Long, String) -> Unit,
    onToggleHito: (HitoEntity) -> Unit,
    onDeleteHito: (HitoEntity) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showEditDialog by remember { mutableStateOf(false) }
    var newHitoDesc by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        onSelectTask(taskId)
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearMessage()
        }
    }

    val tarea = uiState.selectedTarea
    val categoria = uiState.categorias.find { it.idCategoria == tarea?.categoriaId }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Tarea") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (tarea != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Tarea")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (tarea == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontró la tarea seleccionada.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val priorityColor = when (tarea.prioridad) {
                "Alta" -> MaterialTheme.colorScheme.error
                "Media" -> Color(0xFFFF9800)
                else -> Color(0xFF4CAF50)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Task Main Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = tarea.completada,
                                onCheckedChange = { onToggleTaskCompletion(tarea) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tarea.titulo,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (tarea.descripcion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tarea.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            categoria?.let { cat ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(cat.nombre) }
                                )
                            }
                            AssistChip(
                                onClick = { },
                                label = { Text(tarea.prioridad) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = priorityColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            if (tarea.fechaLimite.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tarea.fechaLimite,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hitos Header
                Text(
                    text = "Hitos y Subtareas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Add Hito Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newHitoDesc,
                        onValueChange = { newHitoDesc = it },
                        placeholder = { Text("Agregar sub-tarea u hito...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newHitoDesc.isNotBlank()) {
                                onAddHito(tarea.idTarea, newHitoDesc)
                                newHitoDesc = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Hito")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                // List of Hitos
                if (uiState.selectedTareaHitos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se han agregado hitos a esta tarea.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = uiState.selectedTareaHitos,
                            key = { it.idHito }
                        ) { hito ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = hito.completado,
                                    onCheckedChange = { onToggleHito(hito) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = hito.descripcion,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (hito.completado) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { onDeleteHito(hito) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar Hito",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Task Dialog
    if (showEditDialog && tarea != null) {
        EditTaskDialog(
            tarea = tarea,
            categorias = uiState.categorias,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTask ->
                onUpdateTask(updatedTask)
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskDialog(
    tarea: TareaEntity,
    categorias: List<CategoriaEntity>,
    onDismiss: () -> Unit,
    onSave: (TareaEntity) -> Unit
) {
    var titulo by remember { mutableStateOf(tarea.titulo) }
    var descripcion by remember { mutableStateOf(tarea.descripcion) }
    var prioridad by remember { mutableStateOf(tarea.prioridad) }
    var fechaLimite by remember { mutableStateOf(tarea.fechaLimite) }
    var selectedCatId by remember { mutableLongStateOf(tarea.categoriaId) }

    var expandedPrio by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    val prioridades = listOf("Alta", "Media", "Baja")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarea") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedPrio,
                    onExpandedChange = { expandedPrio = !expandedPrio }
                ) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrio) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPrio,
                        onDismissRequest = { expandedPrio = false }
                    ) {
                        prioridades.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    prioridad = item
                                    expandedPrio = false
                                }
                            )
                        }
                    }
                }

                // Category Dropdown
                if (categorias.isNotEmpty()) {
                    val currentCatName = categorias.find { it.idCategoria == selectedCatId }?.nombre ?: "Seleccionar"
                    ExposedDropdownMenuBox(
                        expanded = expandedCat,
                        onExpandedChange = { expandedCat = !expandedCat }
                    ) {
                        OutlinedTextField(
                            value = currentCatName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            categorias.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.nombre) },
                                    onClick = {
                                        selectedCatId = cat.idCategoria
                                        expandedCat = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = fechaLimite,
                    onValueChange = { fechaLimite = it },
                    label = { Text("Fecha Límite") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        onSave(
                            tarea.copy(
                                titulo = titulo.trim(),
                                descripcion = descripcion.trim(),
                                prioridad = prioridad,
                                fechaLimite = fechaLimite.trim(),
                                categoriaId = selectedCatId
                            )
                        )
                    }
                }
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DetalleTareaScreenPreview() {
    TaskFlowMobile2Theme {
        DetalleTareaScreen(
            taskId = 1L,
            uiState = TaskUiState(
                selectedTarea = TareaEntity(1, "Proyecto Final Android", "Crear app en Kotlin con Compose", "Alta", false, "2026-03-30", 1),
                selectedTareaHitos = listOf(
                    HitoEntity(1, "Crear entidades Room", true, 1),
                    HitoEntity(2, "Inyectar Dagger Hilt", false, 1)
                ),
                categorias = listOf(CategoriaEntity(1, "Universidad", "#6750A4"))
            ),
            onBackClick = {},
            onSelectTask = {},
            onToggleTaskCompletion = {},
            onUpdateTask = {},
            onAddHito = { _, _ -> },
            onToggleHito = {},
            onDeleteHito = {},
            onClearMessage = {}
        )
    }
}
