package com.example.taskflowmobile2.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskflowmobile2.data.local.entities.CategoriaEntity
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.ui.theme.TaskFlowMobile2Theme
import com.example.taskflowmobile2.ui.viewmodel.TaskUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: TaskUiState,
    onFilterSelect: (String) -> Unit,
    onToggleTask: (TareaEntity) -> Unit,
    onDeleteTask: (TareaEntity) -> Unit,
    onCreateTask: (String, String, String, String, Long) -> Unit,
    onUpdateTask: (TareaEntity) -> Unit,
    onSelectTask: (Long) -> Unit,
    onAddHito: (Long, String) -> Unit,
    onToggleHito: (HitoEntity) -> Unit,
    onDeleteHito: (HitoEntity) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TareaEntity?>(null) }
    var taskToDetail by remember { mutableStateOf<TareaEntity?>(null) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TaskFlow Mobile",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = uiState.currentUser?.let { "Hola, ${it.nombre}" } ?: "Tablero de Tareas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    // Dark Mode Toggle Button
                    IconButton(onClick = { onToggleDarkMode(!uiState.isDarkMode) }) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Modo Oscuro"
                        )
                    }

                    // Logout Button
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva Tarea")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Row
            MetricsSection(
                totalCount = uiState.totalTareasCount,
                pendientesCount = uiState.pendientesCount,
                completadasCount = uiState.completadasCount
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Row
            FilterChipsRow(
                selectedFilter = uiState.selectedFilter,
                onFilterSelect = onFilterSelect
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tasks List
            if (uiState.filteredTareas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay tareas en esta sección.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.filteredTareas,
                        key = { it.idTarea }
                    ) { tarea ->
                        val categoria = uiState.categorias.find { it.idCategoria == tarea.categoriaId }
                        TaskItemCard(
                            tarea = tarea,
                            categoria = categoria,
                            onClick = {
                                taskToDetail = tarea
                                onSelectTask(tarea.idTarea)
                            },
                            onToggleCompletion = { onToggleTask(tarea) },
                            onDeleteClick = { taskToDelete = tarea }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Confirm Delete Dialog (DELETE)
    taskToDelete?.let { tarea ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Eliminar Tarea") },
            text = { Text("¿Estás seguro de que deseas eliminar la tarea '${tarea.titulo}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(tarea)
                        taskToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Deseas salir de la aplicación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Create Task Dialog (INSERT)
    if (showCreateDialog) {
        CreateTaskDialog(
            categorias = uiState.categorias,
            onDismiss = { showCreateDialog = false },
            onCreate = { titulo, desc, prio, fecha, catId ->
                onCreateTask(titulo, desc, prio, fecha, catId)
                showCreateDialog = false
            }
        )
    }

    // Detail & Edit Task Dialog (UPDATE & Hitos)
    taskToDetail?.let { tarea ->
        TaskDetailModalDialog(
            tarea = tarea,
            uiState = uiState,
            onDismiss = { taskToDetail = null },
            onUpdateTask = { updated ->
                onUpdateTask(updated)
                taskToDetail = updated
            },
            onToggleTask = {
                onToggleTask(tarea)
                taskToDetail = tarea.copy(completada = !tarea.completada)
            },
            onAddHito = { desc -> onAddHito(tarea.idTarea, desc) },
            onToggleHito = onToggleHito,
            onDeleteHito = onDeleteHito
        )
    }
}

@Composable
private fun MetricsSection(
    totalCount: Int,
    pendientesCount: Int,
    completadasCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricCard(
            title = "Pendientes",
            count = pendientesCount,
            icon = Icons.Default.PendingActions,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Completadas",
            count = completadasCount,
            icon = Icons.Default.CheckCircle,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Total",
            count = totalCount,
            icon = Icons.Default.FilterList,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    count: Int,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
) {
    val filters = listOf("Todas", "Pendientes", "Completadas")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelect(filter) },
                label = { Text(filter) }
            )
        }
    }
}

@Composable
private fun TaskItemCard(
    tarea: TareaEntity,
    categoria: CategoriaEntity?,
    onClick: () -> Unit,
    onToggleCompletion: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val priorityColor = when (tarea.prioridad) {
        "Alta" -> MaterialTheme.colorScheme.error
        "Media" -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tarea.completada) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { onToggleCompletion() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (tarea.descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category Chip
                    categoria?.let { cat ->
                        AssistChip(
                            onClick = { },
                            label = { Text(cat.nombre, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(8.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }

                    // Priority Badge
                    AssistChip(
                        onClick = { },
                        label = { Text(tarea.prioridad, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = priorityColor,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Deadline
                    if (tarea.fechaLimite.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = tarea.fechaLimite,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskDialog(
    categorias: List<CategoriaEntity>,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, Long) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    var fechaLimite by remember { mutableStateOf("2026-04-15") }
    var selectedCatId by remember { mutableLongStateOf(categorias.firstOrNull()?.idCategoria ?: 1L) }

    var expandedPrio by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }

    val prioridades = listOf("Alta", "Media", "Baja")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título *") },
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
                    label = { Text("Fecha Límite (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        onCreate(titulo, descripcion, prioridad, fechaLimite, selectedCatId)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailModalDialog(
    tarea: TareaEntity,
    uiState: TaskUiState,
    onDismiss: () -> Unit,
    onUpdateTask: (TareaEntity) -> Unit,
    onToggleTask: () -> Unit,
    onAddHito: (String) -> Unit,
    onToggleHito: (HitoEntity) -> Unit,
    onDeleteHito: (HitoEntity) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editTitulo by remember { mutableStateOf(tarea.titulo) }
    var editDesc by remember { mutableStateOf(tarea.descripcion) }
    var editPrio by remember { mutableStateOf(tarea.prioridad) }
    var editFecha by remember { mutableStateOf(tarea.fechaLimite) }
    var newHitoText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isEditing) "Editar Tarea" else "Detalle de Tarea",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = editTitulo,
                        onValueChange = { editTitulo = it },
                        label = { Text("Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Descripción") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editFecha,
                        onValueChange = { editFecha = it },
                        label = { Text("Fecha Límite") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = tarea.completada,
                            onCheckedChange = { onToggleTask() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tarea.titulo,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )
                    }

                    if (tarea.descripcion.isNotBlank()) {
                        Text(
                            text = tarea.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Prioridad: ${tarea.prioridad}") }
                        )
                        AssistChip(
                            onClick = { },
                            label = { Text("Fecha: ${tarea.fechaLimite}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Hitos y Subtareas",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newHitoText,
                            onValueChange = { newHitoText = it },
                            placeholder = { Text("Agregar hito...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                if (newHitoText.isNotBlank()) {
                                    onAddHito(newHitoText)
                                    newHitoText = ""
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Hito")
                        }
                    }

                    if (uiState.selectedTareaHitos.isEmpty()) {
                        Text(
                            text = "Sin hitos registrados.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.selectedTareaHitos.forEach { hito ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = hito.completado,
                                    onCheckedChange = { onToggleHito(hito) }
                                )
                                Text(
                                    text = hito.descripcion,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDecoration = if (hito.completado) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { onDeleteHito(hito) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar Hito",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEditing && editTitulo.isNotBlank()) {
                        onUpdateTask(
                            tarea.copy(
                                titulo = editTitulo.trim(),
                                descripcion = editDesc.trim(),
                                prioridad = editPrio,
                                fechaLimite = editFecha.trim()
                            )
                        )
                        isEditing = false
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(if (isEditing) "Guardar Cambios" else "Cerrar")
            }
        },
        dismissButton = {
            if (isEditing) {
                TextButton(onClick = { isEditing = false }) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    TaskFlowMobile2Theme {
        DashboardScreen(
            uiState = TaskUiState(
                tareas = listOf(
                    TareaEntity(1, "Estudiar Kotlin Compose", "Repasar ViewModel y StateFlow", "Alta", false, "2026-03-30", 1),
                    TareaEntity(2, "Comprar supermercado", "Frutas y verduras", "Baja", true, "2026-03-25", 2)
                ),
                categorias = listOf(
                    CategoriaEntity(1, "Universidad", "#6750A4"),
                    CategoriaEntity(2, "Personal", "#3B6939")
                )
            ),
            onFilterSelect = {},
            onToggleTask = {},
            onDeleteTask = {},
            onCreateTask = { _, _, _, _, _ -> },
            onUpdateTask = {},
            onSelectTask = {},
            onAddHito = { _, _ -> },
            onToggleHito = {},
            onDeleteHito = {},
            onToggleDarkMode = {},
            onLogoutClick = {},
            onClearMessage = {}
        )
    }
}
