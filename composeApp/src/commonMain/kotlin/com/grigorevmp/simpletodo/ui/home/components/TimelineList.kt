package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import com.grigorevmp.simpletodo.ui.components.itemPlacement
import com.grigorevmp.simpletodo.ui.home.EmptyState
import com.grigorevmp.simpletodo.util.dateKey
import com.grigorevmp.simpletodo.util.nowInstant
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_no_deadline
import simpletodo.composeapp.generated.resources.home_pinned_tasks
import simpletodo.composeapp.generated.resources.home_planned_earlier
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TimelineList(
    tasks: List<TodoTask>,
    favoriteNotes: List<Note>,
    emptyStateTitle: String?,
    emptyStateBody: String?,
    showEmptyMascot: Boolean,
    onToggleDone: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleSub: (String, String) -> Unit,
    onOpenDetails: (TodoTask) -> Unit,
    onEdit: (TodoTask) -> Unit,
    onDelete: (String) -> Unit,
    onClearCompleted: () -> Unit,
    showCompleted: Boolean,
    tagName: (String?) -> String?,
    projectName: (String?) -> String? = { null },
    noteCount: (TodoTask) -> Int,
    onOpenNotes: (TodoTask) -> Unit,
    onMoveProject: (TodoTask) -> Unit = {},
    dimScroll: Boolean,
    onOpenFavorite: (Note) -> Unit
) {
    val plannedEarlierLabel = stringResource(Res.string.home_planned_earlier)
    val noDeadlineLabel = stringResource(Res.string.home_no_deadline)
    val pinnedLabel = stringResource(Res.string.home_pinned_tasks)
    val pinnedTasks = remember(tasks) { tasks.filter { it.pinned } }
    val otherTasks = remember(tasks) { tasks.filterNot { it.pinned } }
    val visibleOtherTasks = remember(otherTasks, showCompleted) {
        if (showCompleted) otherTasks else otherTasks.filterNot { it.done }
    }
    val grouped = remember(visibleOtherTasks, plannedEarlierLabel, noDeadlineLabel) {
        visibleOtherTasks.groupBy { t ->
            val now = nowInstant()
            val planned = t.plannedAt
            val deadline = t.deadline
            if (deadline != null && deadline < now) {
                plannedEarlierLabel
            } else if (planned != null && planned < now && deadline == null) {
                plannedEarlierLabel
            } else if (deadline != null) {
                dateKey(deadline)
            } else if (planned != null) {
                dateKey(planned)
            } else {
                noDeadlineLabel
            }
        }
    }
    val groupEntries = grouped.entries.toList()
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxWidth()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
        ) {
            if (emptyStateTitle != null && emptyStateBody != null) {
                item {
                    EmptyState(
                        title = emptyStateTitle,
                        body = emptyStateBody,
                        showMascot = showEmptyMascot
                    )
                }
            }

            if (favoriteNotes.isNotEmpty()) {
                item {
                    FavoriteNotesSection(
                        notes = favoriteNotes,
                        dimScroll = dimScroll,
                        onOpen = onOpenFavorite
                    )
                }
            }
            if (pinnedTasks.isNotEmpty()) {
                item {
                    Text(
                        text = pinnedLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
                    )
                }
                items(pinnedTasks, key = { it.id }) { t ->
                    Box(Modifier.itemPlacement()) {
                        AnimatedVisibility(
                            visible = showCompleted || !t.done,
                            enter = fadeIn(tween(160)),
                            exit = shrinkVertically(tween(320)) + fadeOut(tween(260))
                        ) {
                            TaskCard(
                                task = t,
                                tagLabel = tagName(t.tagId),
                                projectLabel = projectName(t.projectId),
                                noteCount = noteCount(t),
                                onOpenNotes = { onOpenNotes(t) },
                                onToggleDone = { onToggleDone(t.id) },
                                onToggleSub = { subId -> onToggleSub(t.id, subId) },
                                onOpenDetails = { onOpenDetails(t) },
                                onEdit = { onEdit(t) },
                                onTogglePinned = { onTogglePinned(t.id) },
                                onDelete = { onDelete(t.id) },
                                onMoveProject = { onMoveProject(t) },
                                onClearCompleted = onClearCompleted,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }

            groupEntries.forEachIndexed { index, (title, v) ->
                item {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
                        )
                    }
                }

                items(v, key = { it.id }) { t ->
                    Box(
                        Modifier.itemPlacement()
                    ) {
                        AnimatedVisibility(
                            visible = showCompleted || !t.done,
                            enter = fadeIn(tween(160)),
                            exit = shrinkVertically(tween(320)) + fadeOut(tween(260))
                        ) {
                            TaskCard(
                                task = t,
                                tagLabel = tagName(t.tagId),
                                projectLabel = projectName(t.projectId),
                                noteCount = noteCount(t),
                                onOpenNotes = { onOpenNotes(t) },
                                onToggleDone = { onToggleDone(t.id) },
                                onToggleSub = { subId -> onToggleSub(t.id, subId) },
                                onOpenDetails = { onOpenDetails(t) },
                                onEdit = { onEdit(t) },
                                onTogglePinned = { onTogglePinned(t.id) },
                                onDelete = { onDelete(t.id) },
                                onMoveProject = { onMoveProject(t) },
                                onClearCompleted = onClearCompleted,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(140.dp)) }
        }
        FadingScrollEdges(
            listState = listState,
            modifier = Modifier.matchParentSize(),
            enabled = dimScroll
        )
    }
}