package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Project
import com.grigorevmp.simpletodo.model.TodoTask
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.task_close
import simpletodo.composeapp.generated.resources.task_no_project_selected
import simpletodo.composeapp.generated.resources.task_project_selected
import simpletodo.composeapp.generated.resources.task_project_title
import simpletodo.composeapp.generated.resources.task_remove_from_project

@Composable
internal fun MoveTaskToProjectDialog(
    task: TodoTask,
    projects: List<Project>,
    onPick: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.task_project_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = { onPick(null) }) {
                    Text(
                        if (task.projectId == null) {
                            stringResource(Res.string.task_no_project_selected)
                        } else {
                            stringResource(Res.string.task_remove_from_project)
                        }
                    )
                }
                projects.forEach { project ->
                    TextButton(onClick = { onPick(project.id) }) {
                        Text(
                            if (task.projectId == project.id) {
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
