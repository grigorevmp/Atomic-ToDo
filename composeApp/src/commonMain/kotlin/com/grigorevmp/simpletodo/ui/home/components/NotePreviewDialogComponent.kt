package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import com.grigorevmp.simpletodo.ui.notes.MarkdownText
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_no_content
import simpletodo.composeapp.generated.resources.task_close
import simpletodo.composeapp.generated.resources.task_edit

@Composable
internal fun NotePreviewDialog(
    note: Note,
    onEdit: () -> Unit,
    onClose: () -> Unit,
    dimScroll: Boolean
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(note.title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Box(Modifier.fillMaxWidth().heightIn(max = 360.dp)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (note.content.isBlank()) {
                        Text(
                            stringResource(Res.string.home_no_content),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        MarkdownText(note.content)
                    }
                }
                FadingScrollEdges(
                    scrollState = scrollState,
                    modifier = Modifier.matchParentSize(),
                    color = MaterialTheme.colorScheme.surface,
                    enabled = dimScroll
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onEdit) { Text(stringResource(Res.string.task_edit)) }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text(stringResource(Res.string.task_close)) }
        }
    )
}
