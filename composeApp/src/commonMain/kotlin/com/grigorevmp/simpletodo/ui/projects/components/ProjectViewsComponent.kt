package com.grigorevmp.simpletodo.ui.projects.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.model.Importance
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.Project
import com.grigorevmp.simpletodo.model.ProjectStatus
import com.grigorevmp.simpletodo.model.TaskNoteLink
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.ui.components.AppIconId
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import com.grigorevmp.simpletodo.ui.components.PlatformIcon
import com.grigorevmp.simpletodo.ui.home.components.TaskCard
import com.grigorevmp.simpletodo.ui.home.components.SegmentedTabs
import com.grigorevmp.simpletodo.ui.home.components.SegmentedTabItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_subtasks
import simpletodo.composeapp.generated.resources.notes_title
import simpletodo.composeapp.generated.resources.projects_action_done
import simpletodo.composeapp.generated.resources.projects_action_edit
import simpletodo.composeapp.generated.resources.projects_action_move
import simpletodo.composeapp.generated.resources.projects_action_undo
import simpletodo.composeapp.generated.resources.projects_completed_column
import simpletodo.composeapp.generated.resources.projects_section_completed
import simpletodo.composeapp.generated.resources.projects_section_no_status
import simpletodo.composeapp.generated.resources.projects_sections_title
import simpletodo.composeapp.generated.resources.projects_show_completed
import simpletodo.composeapp.generated.resources.projects_tab_all
import simpletodo.composeapp.generated.resources.projects_tab_kanban
import simpletodo.composeapp.generated.resources.task_close
import simpletodo.composeapp.generated.resources.task_no_status
import simpletodo.composeapp.generated.resources.task_project_status_title

private enum class ProjectContentMode { ALL, KANBAN }

@Composable
fun SelectedProjectContent(
    repo: TodoRepository,
    project: Project,
    tasks: List<TodoTask>,
    notes: List<Note>,
    links: List<TaskNoteLink>,
    projects: List<Project>,
    prefs: AppPrefs,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditTask: (TodoTask) -> Unit,
    onMoveTask: (TodoTask) -> Unit,
    onEditNote: (String) -> Unit
) {
    var viewMode by remember(project.id) { mutableStateOf(ProjectContentMode.ALL) }
    val scope = rememberCoroutineScope()

    val projectsById = remember(projects) { projects.associateBy { it.id } }
    val notesById = remember(notes) { notes.associateBy { it.id } }
    val noteCountByTaskId = remember(links, notesById) {
        links
            .groupBy { it.taskId }
            .mapValues { (_, taskLinks) ->
                taskLinks
                    .mapNotNull { notesById[it.noteId] }
                    .distinctBy { it.id }
                    .size
            }
    }
    val selectedProjectTasks = remember(tasks, project.id, prefs) {
        val list = tasks.filter { it.projectId == project.id }
        repo.sortedTasks(list, prefs)
    }
    val selectedProjectNotes = remember(project.notesFolderId, notes) {
        notes.filter { it.folderId == project.notesFolderId }
            .sortedByDescending { it.updatedAt }
    }

    Column(Modifier.fillMaxSize()) {
        SelectedProjectHeader(
            projectName = project.name,
            onBack = onBack,
            onOpenSettings = onOpenSettings
        )

        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 6.dp)
        ) {
            when (viewMode) {
                ProjectContentMode.ALL -> ProjectAllView(
                    repo = repo,
                    tasks = selectedProjectTasks,
                    notes = selectedProjectNotes,
                    tagName = { tagId -> prefs.tags.firstOrNull { it.id == tagId }?.name },
                    projectName = { id -> projectsById[id]?.name },
                    noteCount = { task -> noteCountByTaskId[task.id] ?: 0 },
                    onEditTask = onEditTask,
                    onMoveTask = onMoveTask,
                    onEditNote = onEditNote
                )

                ProjectContentMode.KANBAN -> ProjectKanbanView(
                    repo = repo,
                    project = project,
                    tasks = selectedProjectTasks,
                    showCompletedColumn = prefs.showCompletedProjectColumn,
                    onShowCompletedChange = { show ->
                        scope.launch { repo.setShowCompletedProjectColumn(show) }
                    },
                    onEditTask = onEditTask,
                    onMoveTask = onMoveTask
                )
            }

            SegmentedTabs(
                items = listOf(
                    SegmentedTabItem(label = stringResource(Res.string.projects_tab_all)),
                    SegmentedTabItem(label = stringResource(Res.string.projects_tab_kanban))
                ),
                selectedIndex = if (viewMode == ProjectContentMode.ALL) 0 else 1,
                onSelect = {
                    viewMode = if (it == 0) {
                        ProjectContentMode.ALL
                    } else {
                        ProjectContentMode.KANBAN
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp)
            )
        }
    }
}

@Composable
fun ProjectAllView(
    repo: TodoRepository,
    tasks: List<TodoTask>,
    notes: List<Note>,
    tagName: (String?) -> String?,
    projectName: (String?) -> String?,
    noteCount: (TodoTask) -> Int,
    onEditTask: (TodoTask) -> Unit,
    onMoveTask: (TodoTask) -> Unit,
    onEditNote: (String) -> Unit
) {
    val prefs by repo.prefs.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    tagLabel = tagName(task.tagId),
                    projectLabel = projectName(task.projectId),
                    noteCount = noteCount(task),
                    onOpenNotes = {},
                    onToggleDone = { scope.launch { repo.toggleDone(task.id) } },
                    onToggleSub = { subId -> scope.launch { repo.toggleSubtask(task.id, subId) } },
                    onOpenDetails = {},
                    onEdit = { onEditTask(task) },
                    onTogglePinned = { scope.launch { repo.togglePinned(task.id) } },
                    onDelete = { scope.launch { repo.deleteTask(task.id) } },
                    onMoveProject = { onMoveTask(task) },
                    onClearCompleted = { scope.launch { repo.clearCompletedTasks() } }
                )
            }

            if (notes.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(Res.string.notes_title), style = MaterialTheme.typography.titleMedium)
                }
                items(notes, key = { it.id }) { note ->
                    Surface(
                        onClick = { onEditNote(note.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                note.title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                note.content,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            enabled = prefs.dimScroll
        )
    }
}

@Composable
fun ProjectKanbanView(
    repo: TodoRepository,
    project: Project,
    tasks: List<TodoTask>,
    showCompletedColumn: Boolean,
    onShowCompletedChange: (Boolean) -> Unit,
    onEditTask: (TodoTask) -> Unit,
    onMoveTask: (TodoTask) -> Unit
) {
    val noStatusLabel = stringResource(Res.string.task_no_status)
    val scope = rememberCoroutineScope()
    val activeTasks = tasks.filter { !it.done }
    var quickMoveTask by remember(project.id) { mutableStateOf<TodoTask?>(null) }
    var detailsTaskId by remember(project.id) { mutableStateOf<String?>(null) }
    var showSectionsDialog by remember(project.id) { mutableStateOf(false) }
    var showNoStatusColumn by remember(project.id) { mutableStateOf(true) }
    var showCompletedSection by remember(project.id) { mutableStateOf(true) }
    val hiddenStatusIds = remember(project.id) { mutableStateListOf<String>() }
    val columns = remember(project.statuses, noStatusLabel) {
        listOf(ProjectStatus(id = "__none__", name = noStatusLabel)) + project.statuses
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showSectionsDialog = true }) {
                PlatformIcon(
                    id = AppIconId.Filter,
                    contentDescription = stringResource(Res.string.projects_sections_title),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        val visibleColumns = columns.filter { status ->
            when (status.id) {
                "__none__" -> showNoStatusColumn
                else -> status.id !in hiddenStatusIds
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(visibleColumns, key = { it.id }) { status ->
                val columnTasks = if (status.id == "__none__") {
                    activeTasks.filter { it.projectStatusId == null }
                } else {
                    activeTasks.filter { it.projectStatusId == status.id }
                }
                KanbanColumn(
                    repo = repo,
                    title = "${status.name} (${columnTasks.size})",
                    tasks = columnTasks,
                    statuses = project.statuses,
                    onOpenDetails = { task -> detailsTaskId = task.id },
                    onQuickMove = { task -> quickMoveTask = task }
                )
            }

            if (showCompletedColumn && showCompletedSection) {
                item {
                    val completed = tasks.filter { it.done }
                    KanbanColumn(
                        repo = repo,
                        title = stringResource(Res.string.projects_completed_column, completed.size.toString()),
                        tasks = completed,
                        statuses = project.statuses,
                        onOpenDetails = { task -> detailsTaskId = task.id },
                        onQuickMove = { task -> quickMoveTask = task }
                    )
                }
            }
        }

        if (showSectionsDialog) {
            AlertDialog(
                onDismissRequest = { showSectionsDialog = false },
                title = { Text(stringResource(Res.string.projects_sections_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.projects_section_no_status))
                            Checkbox(
                                checked = showNoStatusColumn,
                                onCheckedChange = { showNoStatusColumn = it }
                            )
                        }
                        project.statuses.forEach { status ->
                            val visible = status.id !in hiddenStatusIds
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(status.name)
                                Checkbox(
                                    checked = visible,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            hiddenStatusIds.remove(status.id)
                                        } else if (status.id !in hiddenStatusIds) {
                                            hiddenStatusIds.add(status.id)
                                        }
                                    }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.projects_section_completed))
                            Checkbox(
                                checked = showCompletedSection,
                                onCheckedChange = { showCompletedSection = it }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(Res.string.projects_show_completed))
                            Checkbox(
                                checked = showCompletedColumn,
                                onCheckedChange = onShowCompletedChange
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSectionsDialog = false }) {
                        Text(stringResource(Res.string.task_close))
                    }
                }
            )
        }

        quickMoveTask?.let { task ->
            AlertDialog(
                onDismissRequest = { quickMoveTask = null },
                title = { Text(stringResource(Res.string.task_project_status_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = {
                                scope.launch { repo.setTaskProjectStatus(task.id, null) }
                                quickMoveTask = null
                            }
                        ) { Text(noStatusLabel) }
                        project.statuses.forEach { status ->
                            TextButton(
                                onClick = {
                                    scope.launch { repo.setTaskProjectStatus(task.id, status.id) }
                                    quickMoveTask = null
                                }
                            ) { Text(status.name) }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { quickMoveTask = null }) {
                        Text(stringResource(Res.string.task_close))
                    }
                }
            )
        }

        detailsTaskId?.let { taskId ->
            val task = tasks.firstOrNull { it.id == taskId }
            if (task == null) {
                detailsTaskId = null
            } else {
                ProjectTaskDetailsSheet(
                    task = task,
                    onClose = { detailsTaskId = null },
                    onToggleDone = {
                        scope.launch { repo.toggleDone(task.id) }
                    },
                    onEdit = {
                        detailsTaskId = null
                        onEditTask(task)
                    },
                    onMoveProject = {
                        detailsTaskId = null
                        onMoveTask(task)
                    }
                )
            }
        }
    }
}

@Composable
private fun KanbanColumn(
    repo: TodoRepository,
    title: String,
    tasks: List<TodoTask>,
    statuses: List<ProjectStatus>,
    onOpenDetails: (TodoTask) -> Unit,
    onQuickMove: (TodoTask) -> Unit
) {
    val scope = rememberCoroutineScope()
    val noStatusLabel = stringResource(Res.string.task_no_status)

    Surface(
        modifier = Modifier.width(320.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    val cardColor = when (task.importance) {
                        Importance.LOW -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                        Importance.NORMAL -> MaterialTheme.colorScheme.surface
                        Importance.HIGH -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
                        Importance.CRITICAL -> MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .combinedClickable(
                                onClick = { onOpenDetails(task) },
                                onLongClick = { onQuickMove(task) }
                            ),
                        shape = RoundedCornerShape(12.dp),
                        color = cardColor
                    ) {
                        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(task.title, style = MaterialTheme.typography.titleSmall)
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = task.projectStatusId == null,
                                    onClick = { scope.launch { repo.setTaskProjectStatus(task.id, null) } },
                                    label = { Text(noStatusLabel) }
                                )
                                statuses.forEach { status ->
                                    FilterChip(
                                        selected = task.projectStatusId == status.id,
                                        onClick = {
                                            scope.launch { repo.setTaskProjectStatus(task.id, status.id) }
                                        },
                                        label = { Text(status.name) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectTaskDetailsSheet(
    task: TodoTask,
    onClose: () -> Unit,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit,
    onMoveProject: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(task.title, style = MaterialTheme.typography.titleLarge)
            if (task.plan.isNotBlank()) {
                Text(
                    task.plan,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) { Text(stringResource(Res.string.projects_action_edit)) }
                TextButton(onClick = onMoveProject) { Text(stringResource(Res.string.projects_action_move)) }
                TextButton(onClick = onToggleDone) {
                    Text(
                        if (task.done) {
                            stringResource(Res.string.projects_action_undo)
                        } else {
                            stringResource(Res.string.projects_action_done)
                        }
                    )
                }
            }
            if (task.subtasks.isNotEmpty()) {
                Text(
                    stringResource(Res.string.home_subtasks),
                    style = MaterialTheme.typography.titleMedium
                )
                task.subtasks.forEach { sub ->
                    Text(
                        "\u2022 ${sub.text}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (sub.done) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
