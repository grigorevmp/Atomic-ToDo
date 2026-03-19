package com.grigorevmp.simpletodo.ui.projects.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.AppLanguage
import com.grigorevmp.simpletodo.model.Project
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.notes_cancel
import simpletodo.composeapp.generated.resources.notes_create
import simpletodo.composeapp.generated.resources.notes_save
import simpletodo.composeapp.generated.resources.projects_move_task_title
import simpletodo.composeapp.generated.resources.projects_name_label
import simpletodo.composeapp.generated.resources.projects_new_title
import simpletodo.composeapp.generated.resources.projects_status_add
import simpletodo.composeapp.generated.resources.projects_status_input
import simpletodo.composeapp.generated.resources.projects_statuses_default
import simpletodo.composeapp.generated.resources.projects_statuses_title
import simpletodo.composeapp.generated.resources.settings_delete
import simpletodo.composeapp.generated.resources.settings_title
import simpletodo.composeapp.generated.resources.task_close
import simpletodo.composeapp.generated.resources.task_no_project_selected
import simpletodo.composeapp.generated.resources.task_project_selected
import simpletodo.composeapp.generated.resources.task_remove_from_project

@Composable
fun CreateProjectDialog(
    appLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    val localizedStatuses = stringResource(Res.string.projects_statuses_default)
    val defaultStatuses = remember(appLanguage, localizedStatuses) {
        when (appLanguage) {
            AppLanguage.EN -> listOf("Todo", "Doing", "Done")
            AppLanguage.RU -> listOf("Сделать", "В процессе", "Готово")
            AppLanguage.SYSTEM -> localizedStatuses
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }
    }

    var name by remember { mutableStateOf("") }
    var newStatus by remember { mutableStateOf("") }
    val statuses = remember(defaultStatuses) {
        mutableStateListOf<String>().apply { addAll(defaultStatuses) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.projects_new_title)) },
        text = {
            StatusEditor(
                name = name,
                onNameChange = { name = it },
                statuses = statuses,
                newStatus = newStatus,
                onNewStatusChange = { newStatus = it }
            )
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name, statuses.toList()) }) {
                Text(stringResource(Res.string.notes_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.notes_cancel))
            }
        }
    )
}

@Composable
fun EditProjectDialog(
    project: Project,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (String, List<String>) -> Unit
) {
    var name by remember(project.id) { mutableStateOf(project.name) }
    var newStatus by remember(project.id) { mutableStateOf("") }
    val statuses = remember(project.id) {
        mutableStateListOf<String>().apply { addAll(project.statuses.map { it.name }) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_title)) },
        text = {
            StatusEditor(
                name = name,
                onNameChange = { name = it },
                statuses = statuses,
                newStatus = newStatus,
                onNewStatusChange = { newStatus = it }
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, statuses.toList()) }) {
                Text(stringResource(Res.string.notes_save))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text(stringResource(Res.string.settings_delete))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.task_close))
                }
            }
        }
    )
}

@Composable
fun MoveTaskDialog(
    taskProjectId: String?,
    projects: List<Project>,
    onPick: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.projects_move_task_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { onPick(null) }) {
                    Text(
                        if (taskProjectId == null) {
                            stringResource(Res.string.task_no_project_selected)
                        } else {
                            stringResource(Res.string.task_remove_from_project)
                        }
                    )
                }
                projects.forEach { project ->
                    TextButton(onClick = { onPick(project.id) }) {
                        Text(
                            if (taskProjectId == project.id) {
                                stringResource(Res.string.task_project_selected, project.name)
                            } else {
                                project.name
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.task_close))
            }
        }
    )
}

@Composable
fun DeleteProjectDialog(
    projectName: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_delete)) },
        text = { Text(projectName) },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(stringResource(Res.string.settings_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.notes_cancel))
            }
        }
    )
}

@Composable
private fun StatusEditor(
    name: String,
    onNameChange: (String) -> Unit,
    statuses: MutableList<String>,
    newStatus: String,
    onNewStatusChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp)
            .verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(Res.string.projects_name_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.projects_statuses_title),
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newStatus,
                onValueChange = onNewStatusChange,
                label = { Text(stringResource(Res.string.projects_status_input)) },
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = {
                    val trimmed = newStatus.trim()
                    if (trimmed.isEmpty()) return@TextButton
                    if (statuses.any { it.equals(trimmed, ignoreCase = true) }) return@TextButton
                    statuses.add(trimmed)
                    onNewStatusChange("")
                }
            ) {
                Text(stringResource(Res.string.projects_status_add))
            }
        }

        statuses.forEachIndexed { index, status ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(status, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    TextButton(onClick = { statuses.removeAt(index) }) {
                        Text(stringResource(Res.string.settings_delete))
                    }
                }
            }
        }
    }
}
