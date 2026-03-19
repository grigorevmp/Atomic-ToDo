package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.util.formatDeadline
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_no_content
import simpletodo.composeapp.generated.resources.home_notes_linked_to
import simpletodo.composeapp.generated.resources.home_notes_title
import simpletodo.composeapp.generated.resources.home_untitled

@Composable
internal fun TaskNotesSheet(
    taskTitle: String,
    notes: List<Note>,
    onOpenNote: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val dialogContainerColor = if (isLightTheme) Color.White else MaterialTheme.colorScheme.surface
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 760.dp)
                .heightIn(max = 820.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = dialogContainerColor,
            tonalElevation = 6.dp,
            shadowElevation = 14.dp
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
            ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(Res.string.home_notes_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        stringResource(
                            Res.string.home_notes_linked_to,
                            notes.size.toString(),
                            taskTitle
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

                items(notes, key = { it.id }) { note ->
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenNote(note) }
                    ) {
                        Column(
                            Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                note.title.ifBlank { stringResource(Res.string.home_untitled) },
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                formatDeadline(note.updatedAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                notePreview(note),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun notePreview(note: Note): String {
    val text = note.content.trim()
    if (text.isBlank()) return stringResource(Res.string.home_no_content)
    return text.replace("\n", " ").take(140)
}
