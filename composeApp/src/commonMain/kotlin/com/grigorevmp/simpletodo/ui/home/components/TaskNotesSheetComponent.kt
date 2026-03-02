package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.util.formatDeadline
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_no_content
import simpletodo.composeapp.generated.resources.home_notes_linked_to
import simpletodo.composeapp.generated.resources.home_notes_title
import simpletodo.composeapp.generated.resources.home_untitled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskNotesSheet(
    taskTitle: String,
    notes: List<Note>,
    onOpenNote: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = notes.isNotEmpty())
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
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

@Composable
private fun notePreview(note: Note): String {
    val text = note.content.trim()
    if (text.isBlank()) return stringResource(Res.string.home_no_content)
    return text.replace("\n", " ").take(140)
}
