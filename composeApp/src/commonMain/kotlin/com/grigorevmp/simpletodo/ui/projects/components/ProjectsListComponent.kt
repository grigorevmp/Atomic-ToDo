package com.grigorevmp.simpletodo.ui.projects.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.Project
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.projects_empty
import simpletodo.composeapp.generated.resources.projects_title

@Composable
fun ProjectsListContent(
    projects: List<Project>,
    tasks: List<TodoTask>,
    notes: List<Note>,
    dimScroll: Boolean,
    onProjectClick: (Project) -> Unit,
    onProjectLongClick: (Project) -> Unit
) {
    if (projects.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(Res.string.projects_empty))
        }
        return
    }

    val tasksByProjectCount = remember(tasks) { tasks.groupingBy { it.projectId }.eachCount() }
    val notesByFolderCount = remember(notes) { notes.groupingBy { it.folderId }.eachCount() }
    val listState = rememberLazyListState()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { ProjectsTopBar() }

            items(projects, key = { it.id }) { project ->
                val shape = RoundedCornerShape(16.dp)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
                ) {
                    Column(
                        Modifier
                            .clip(shape)
                            .combinedClickable(
                                onClick = { onProjectClick(project) },
                                onLongClick = { onProjectLongClick(project) }
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(project.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${tasksByProjectCount[project.id] ?: 0} • ${notesByFolderCount[project.notesFolderId] ?: 0}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(120.dp)) }
        }

        FadingScrollEdges(
            listState = listState,
            modifier = Modifier.matchParentSize(),
            enabled = dimScroll
        )
    }
}

@Composable
private fun ProjectsTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .height(48.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 220.dp)
        ) {
            Text(
                stringResource(Res.string.projects_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
