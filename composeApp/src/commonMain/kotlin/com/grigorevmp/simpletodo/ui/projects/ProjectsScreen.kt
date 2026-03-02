package com.grigorevmp.simpletodo.ui.projects

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.Project
import com.grigorevmp.simpletodo.model.ProjectStatus
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.model.Importance
import com.grigorevmp.simpletodo.platform.PlatformBackHandler
import com.grigorevmp.simpletodo.ui.components.AppIconId
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import com.grigorevmp.simpletodo.ui.components.PlatformIcon
import com.grigorevmp.simpletodo.ui.home.TaskCard
import com.grigorevmp.simpletodo.ui.home.TaskEditorSheet
import com.grigorevmp.simpletodo.ui.home.components.SegmentedTabs
import com.grigorevmp.simpletodo.ui.components.SimpleIcons
import com.grigorevmp.simpletodo.util.newId
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.notes_cancel
import simpletodo.composeapp.generated.resources.notes_create
import simpletodo.composeapp.generated.resources.notes_save
import simpletodo.composeapp.generated.resources.notes_title
import simpletodo.composeapp.generated.resources.home_subtasks
import simpletodo.composeapp.generated.resources.note_editor_untitled
import simpletodo.composeapp.generated.resources.projects_action_done
import simpletodo.composeapp.generated.resources.projects_action_edit
import simpletodo.composeapp.generated.resources.projects_action_move
import simpletodo.composeapp.generated.resources.projects_action_undo
import simpletodo.composeapp.generated.resources.projects_completed_column
import simpletodo.composeapp.generated.resources.projects_create_cd
import simpletodo.composeapp.generated.resources.projects_empty
import simpletodo.composeapp.generated.resources.projects_move_task_title
import simpletodo.composeapp.generated.resources.projects_name_label
import simpletodo.composeapp.generated.resources.projects_new_title
import simpletodo.composeapp.generated.resources.projects_show_completed
import simpletodo.composeapp.generated.resources.projects_sections_title
import simpletodo.composeapp.generated.resources.projects_section_no_status
import simpletodo.composeapp.generated.resources.projects_section_completed
import simpletodo.composeapp.generated.resources.projects_status_add
import simpletodo.composeapp.generated.resources.projects_status_input
import simpletodo.composeapp.generated.resources.projects_statuses_default
import simpletodo.composeapp.generated.resources.projects_statuses_title
import simpletodo.composeapp.generated.resources.projects_tab_all
import simpletodo.composeapp.generated.resources.projects_tab_kanban
import simpletodo.composeapp.generated.resources.projects_title
import simpletodo.composeapp.generated.resources.settings_delete
import simpletodo.composeapp.generated.resources.settings_title
import simpletodo.composeapp.generated.resources.task_close
import simpletodo.composeapp.generated.resources.task_no_project_selected
import simpletodo.composeapp.generated.resources.task_no_status
import simpletodo.composeapp.generated.resources.task_project_status_title
import simpletodo.composeapp.generated.resources.task_project_selected
import simpletodo.composeapp.generated.resources.task_remove_from_project

private enum class ProjectViewMode { ALL, KANBAN }

@Composable
fun ProjectsScreen(
    repo: TodoRepository,
    createProjectSignal: Int,
    onCreateProjectHandled: () -> Unit,
    onEditNote: (String) -> Unit,
    onBackFromRoot: () -> Unit = {}
) {
    val tasks by repo.tasks.collectAsState()
    val notes by repo.notes.collectAsState()
    val links by repo.taskNoteLinks.collectAsState()
    val projects by repo.projects.collectAsState()
    val prefs by repo.prefs.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var viewMode by remember { mutableStateOf(ProjectViewMode.ALL) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var deleteProjectCandidate by remember { mutableStateOf<Project?>(null) }
    var editTask by remember { mutableStateOf<TodoTask?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var moveTask by remember { mutableStateOf<TodoTask?>(null) }

    val selectedProject = remember(selectedProjectId, projects) {
        projects.firstOrNull { it.id == selectedProjectId }
    }
    val untitledNote = stringResource(Res.string.note_editor_untitled)

    LaunchedEffect(createProjectSignal, selectedProjectId, projects) {
        if (createProjectSignal > 0) {
            val currentProject = selectedProjectId?.let { id -> projects.firstOrNull { it.id == id } }
            if (currentProject == null) {
                showCreateDialog = true
            } else {
                val beforeIds = notes.map { it.id }.toSet()
                repo.addNote(
                    title = untitledNote,
                    content = "",
                    taskId = null,
                    folderId = currentProject.notesFolderId,
                    favorite = false
                )
                val createdId = repo.notes.value.firstOrNull { it.id !in beforeIds }?.id
                if (createdId != null) {
                    onEditNote(createdId)
                }
            }
            onCreateProjectHandled()
        }
    }

    PlatformBackHandler(enabled = !showEditor) {
        if (selectedProjectId != null) {
            selectedProjectId = null
        } else {
            onBackFromRoot()
        }
    }

    val notesById = remember(notes) { notes.associateBy { it.id } }
    val projectsById = remember(projects) { projects.associateBy { it.id } }
    val tasksByProjectCount = remember(tasks) { tasks.groupingBy { it.projectId }.eachCount() }
    val notesByFolderCount = remember(notes) { notes.groupingBy { it.folderId }.eachCount() }
    val selectedProjectTasks = remember(tasks, selectedProjectId, prefs) {
        val list = tasks.filter { it.projectId == selectedProjectId }
        repo.sortedTasks(list, prefs)
    }
    val selectedProjectNotes = remember(selectedProject, notes) {
        val folderId = selectedProject?.notesFolderId
        notes.filter { it.folderId == folderId }.sortedByDescending { it.updatedAt }
    }

    fun notesForTask(task: TodoTask): List<Note> {
        return links
            .asSequence()
            .filter { it.taskId == task.id }
            .mapNotNull { notesById[it.noteId] }
            .distinctBy { it.id }
            .toList()
    }

    Column(Modifier.fillMaxSize()) {
        if (selectedProject == null) {
            Text(
                stringResource(Res.string.projects_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
            )

            if (projects.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.projects_empty))
                }
            } else {
                val listState = rememberLazyListState()
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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
                                            onClick = { selectedProjectId = project.id },
                                            onLongClick = { deleteProjectCandidate = project }
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
                        enabled = prefs.dimScroll
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedProjectId = null }) {
                        Icon(
                            imageVector = SimpleIcons.ArrowLeft,
                            contentDescription = stringResource(Res.string.notes_title)
                        )
                    }
                    Text(
                        selectedProject.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(190.dp)
                    )
                }
                IconButton(onClick = { showEditProjectDialog = true }) {
                    PlatformIcon(
                        id = AppIconId.Settings,
                        contentDescription = stringResource(Res.string.settings_title),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 6.dp)
            ) {
                when (viewMode) {
                    ProjectViewMode.ALL -> ProjectAllView(
                        repo = repo,
                        tasks = selectedProjectTasks,
                        notes = selectedProjectNotes,
                        tagName = { tagId -> prefs.tags.firstOrNull { it.id == tagId }?.name },
                        projectName = { id -> projectsById[id]?.name },
                        noteCount = { task -> notesForTask(task).size },
                        onEditTask = { task -> editTask = task; showEditor = true },
                        onMoveTask = { task -> moveTask = task },
                        onEditNote = onEditNote
                    )

                    ProjectViewMode.KANBAN -> ProjectKanbanView(
                        repo = repo,
                        project = selectedProject,
                        tasks = selectedProjectTasks,
                        showCompletedColumn = prefs.showCompletedProjectColumn,
                        onShowCompletedChange = { show -> scope.launch { repo.setShowCompletedProjectColumn(show) } },
                        onEditTask = { task -> editTask = task; showEditor = true },
                        onMoveTask = { task -> moveTask = task }
                    )
                }

                SegmentedTabs(
                    items = listOf(
                        stringResource(Res.string.projects_tab_all),
                        stringResource(Res.string.projects_tab_kanban)
                    ),
                    selectedIndex = if (viewMode == ProjectViewMode.ALL) 0 else 1,
                    onSelect = { viewMode = if (it == 0) ProjectViewMode.ALL else ProjectViewMode.KANBAN },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 96.dp)
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, statuses ->
                scope.launch { repo.addProject(name, statuses) }
                showCreateDialog = false
            }
        )
    }

    if (showEditProjectDialog && selectedProject != null) {
        EditProjectDialog(
            project = selectedProject,
            onDismiss = { showEditProjectDialog = false },
            onDelete = {
                scope.launch { repo.deleteProject(selectedProject.id) }
                selectedProjectId = null
                showEditProjectDialog = false
            },
            onSave = { name, statuses ->
                val oldByName = selectedProject.statuses.associateBy { it.name.lowercase() }
                val mappedStatuses = statuses.map { statusName ->
                    oldByName[statusName.lowercase()] ?: ProjectStatus(id = newId("pstatus"), name = statusName)
                }
                scope.launch {
                    repo.updateProject(
                        selectedProject.copy(
                            name = name,
                            statuses = mappedStatuses
                        )
                    )
                }
                showEditProjectDialog = false
            }
        )
    }

    if (showEditor) {
        TaskEditorSheet(
            repo = repo,
            prefsTagList = prefs.tags,
            projects = projects,
            notes = notes,
            initial = editTask,
            onDismiss = { showEditor = false }
        )
    }

    moveTask?.let { task ->
        MoveTaskDialog(
            task = task,
            projects = projects,
            onPick = { projectId ->
                scope.launch { repo.assignTaskToProject(task.id, projectId) }
                moveTask = null
            },
            onDismiss = { moveTask = null }
        )
    }

    deleteProjectCandidate?.let { project ->
        AlertDialog(
            onDismissRequest = { deleteProjectCandidate = null },
            title = { Text(stringResource(Res.string.settings_delete)) },
            text = { Text(project.name) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch { repo.deleteProject(project.id) }
                        if (selectedProjectId == project.id) {
                            selectedProjectId = null
                        }
                        deleteProjectCandidate = null
                    }
                ) { Text(stringResource(Res.string.settings_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteProjectCandidate = null }) {
                    Text(stringResource(Res.string.notes_cancel))
                }
            }
        )
    }
}

@Composable
private fun ProjectAllView(
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
private fun ProjectKanbanView(
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
                .padding(horizontal = 18.dp),
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
                                        if (checked) hiddenStatusIds.remove(status.id) else if (status.id !in hiddenStatusIds) hiddenStatusIds.add(status.id)
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
                        color = if (sub.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    val defaultStatuses = stringResource(Res.string.projects_statuses_default)
        .split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    var name by remember { mutableStateOf("") }
    var newStatus by remember { mutableStateOf("") }
    val statuses = remember(defaultStatuses) { mutableStateListOf<String>().apply { addAll(defaultStatuses) } }

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
private fun EditProjectDialog(
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

@Composable
private fun MoveTaskDialog(
    task: TodoTask,
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
