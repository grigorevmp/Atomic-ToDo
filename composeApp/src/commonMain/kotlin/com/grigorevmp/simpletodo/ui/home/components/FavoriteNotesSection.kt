package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdgesHorizontal
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_favorite_notes
import simpletodo.composeapp.generated.resources.home_no_content
import simpletodo.composeapp.generated.resources.home_untitled


@Composable
fun FavoriteNotesSection(
    notes: List<Note>,
    dimScroll: Boolean,
    onOpen: (Note) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            stringResource(Res.string.home_favorite_notes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        val scrollState = rememberScrollState()
        Box(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                notes.forEach { note ->
                    Surface(
                        onClick = { onOpen(note) },
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.width(220.dp)
                    ) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                note.title.ifBlank { stringResource(Res.string.home_untitled) },
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                NotePreview(note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            FadingScrollEdgesHorizontal(
                scrollState = scrollState,
                modifier = Modifier.matchParentSize(),
                enabled = dimScroll
            )
        }
    }
}

@Composable
private fun NotePreview(note: Note): String {
    val text = note.content.trim()
    if (text.isBlank()) return stringResource(Res.string.home_no_content)
    return text.replace("\n", " ").take(140)
}