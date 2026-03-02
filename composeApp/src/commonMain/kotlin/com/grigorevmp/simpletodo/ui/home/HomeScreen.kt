package com.grigorevmp.simpletodo.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.Project
import com.grigorevmp.simpletodo.model.TaskNoteLink
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.ui.home.components.CelebrationVolley
import com.grigorevmp.simpletodo.ui.home.components.FlatList
import com.grigorevmp.simpletodo.ui.home.components.MeteorHeader
import com.grigorevmp.simpletodo.ui.home.components.MoveTaskToProjectDialog
import com.grigorevmp.simpletodo.ui.home.components.NotePreviewDialog
import com.grigorevmp.simpletodo.ui.home.components.SegmentedTabs
import com.grigorevmp.simpletodo.ui.home.components.SortSheet
import com.grigorevmp.simpletodo.ui.home.components.TagFilters
import com.grigorevmp.simpletodo.ui.home.components.TaskDetailsSheet
import com.grigorevmp.simpletodo.ui.home.components.TaskEditorSheet
import com.grigorevmp.simpletodo.ui.home.components.TaskNotesSheet
import com.grigorevmp.simpletodo.ui.home.components.TimelineList
import com.grigorevmp.simpletodo.ui.home.components.TopBar
import com.grigorevmp.simpletodo.ui.home.components.calendar.CalendarTab
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_empty_inbox_body
import simpletodo.composeapp.generated.resources.home_empty_inbox_title
import simpletodo.composeapp.generated.resources.home_motivation_1
import simpletodo.composeapp.generated.resources.home_motivation_2
import simpletodo.composeapp.generated.resources.home_motivation_3
import simpletodo.composeapp.generated.resources.home_motivation_4
import simpletodo.composeapp.generated.resources.home_motivation_5
import simpletodo.composeapp.generated.resources.home_motivation_6
import simpletodo.composeapp.generated.resources.tab_calendar
import simpletodo.composeapp.generated.resources.tab_inbox
import simpletodo.composeapp.generated.resources.tab_timeline

private enum class HomeTab { TIMELINE, CALENDAR, INBOX }

@Composable
fun HomeScreen(
    repo: TodoRepository,
    createSignal: Int,
    onCreateHandled: () -> Unit,
    onEditNote: (String) -> Unit
) {
    val tasks by repo.tasks.collectAsState()
    val notes by repo.notes.collectAsState()
    val links by repo.taskNoteLinks.collectAsState()
    val projects by repo.projects.collectAsState()
    val prefs by repo.prefs.collectAsState()

    var showEditor by remember { mutableStateOf(false) }
    var editTask by remember { mutableStateOf<TodoTask?>(null) }

    LaunchedEffect(createSignal) {
        if (createSignal > 0) {
            editTask = null
            showEditor = true
            onCreateHandled()
        }
    }

    HomeMainContent(
        repo = repo,
        tasks = tasks,
        notes = notes,
        links = links,
        projects = projects,
        prefs = prefs,
        onEditTask = { task ->
            editTask = task
            showEditor = true
        },
        onEditNote = onEditNote
    )

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
}

@Composable
private fun HomeMainContent(
    repo: TodoRepository,
    tasks: List<TodoTask>,
    notes: List<Note>,
    links: List<TaskNoteLink>,
    projects: List<Project>,
    prefs: AppPrefs,
    onEditTask: (TodoTask) -> Unit,
    onEditNote: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sorted = remember(tasks, prefs) { repo.sortedTasks(tasks, prefs) }

    val motivations = listOf(
        stringResource(Res.string.home_motivation_1),
        stringResource(Res.string.home_motivation_2),
        stringResource(Res.string.home_motivation_3),
        stringResource(Res.string.home_motivation_4),
        stringResource(Res.string.home_motivation_5),
        stringResource(Res.string.home_motivation_6)
    )
    val motivation = remember(motivations) { motivations.random() }
    val notesById = remember(notes) { notes.associateBy { it.id } }
    val projectsById = remember(projects) { projects.associateBy { it.id } }
    val favoriteNotes =
        remember(notes) { notes.filter { it.favorite }.sortedByDescending { it.updatedAt } }

    var previewNote by remember { mutableStateOf<Note?>(null) }
    var taskNotesSheet by remember { mutableStateOf<Pair<TodoTask, List<Note>>?>(null) }
    var showSort by remember { mutableStateOf(false) }
    var tagFilter by remember { mutableStateOf<String?>(null) }
    var tab by remember { mutableStateOf(HomeTab.TIMELINE) }
    var detailsTaskId by remember { mutableStateOf<String?>(null) }
    var projectMoveTask by remember { mutableStateOf<TodoTask?>(null) }
    val celebrationTrigger = remember { mutableIntStateOf(0) }

    val filtered = remember(sorted, tagFilter) {
        when (tagFilter) {
            null -> sorted
            "__no_tag__" -> sorted.filter { it.tagId == null }
            else -> sorted.filter { it.tagId == tagFilter }
        }
    }

    val timelineTasks = filtered.filter { it.deadline != null || it.plannedAt != null }
    val inboxTasks = filtered.filter { it.deadline == null && it.plannedAt == null }
    val backgroundColor = MaterialTheme.colorScheme.background
    val listBackdrop = rememberLayerBackdrop {
        drawRect(backgroundColor)
        drawContent()
    }

    fun notesForTask(task: TodoTask): List<Note> {
        return links
            .asSequence()
            .filter { it.taskId == task.id }
            .mapNotNull { notesById[it.noteId] }
            .distinct()
            .sortedByDescending { it.updatedAt }
            .toList()
    }

    Box(Modifier.fillMaxSize()) {
        if (tab != HomeTab.CALENDAR) {
            CelebrationVolley(
                trigger = celebrationTrigger.intValue,
                modifier = Modifier.matchParentSize()
            )
        }
        Column(Modifier.fillMaxSize()) {
            TopBar(
                tagsShown = prefs.showTagFilters,
                motivation = motivation,
                onSort = { showSort = true },
                onToggleTags = { scope.launch { repo.setShowTagFilters(!prefs.showTagFilters) } }
            )

            AnimatedVisibility(prefs.showTagFilters) {
                TagFilters(
                    selectedTagId = tagFilter,
                    tags = prefs.tags,
                    onPick = { tagFilter = it }
                )
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .layerBackdrop(listBackdrop)
            ) {
                AnimatedContent(
                    targetState = tab,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "timeline-inbox"
                ) { target ->
                    when (target) {
                        HomeTab.TIMELINE -> {
                            val timelineHasVisible = if (prefs.showCompletedTasks) {
                                timelineTasks.isNotEmpty()
                            } else {
                                timelineTasks.any { !it.done }
                            }
                            TimelineList(
                                tasks = timelineTasks,
                                favoriteNotes = favoriteNotes,
                                emptyStateTitle = if (!timelineHasVisible) "No timeline tasks." else null,
                                emptyStateBody = if (!timelineHasVisible) {
                                    "Add a task with a deadline to see it on the timeline."
                                } else null,
                                showEmptyMascot = !timelineHasVisible,
                                onToggleDone = { id ->
                                    val task = tasks.firstOrNull { it.id == id }
                                    if (task != null && !task.done) {
                                        celebrationTrigger.intValue += 1
                                    }
                                    scope.launch { repo.toggleDone(id) }
                                },
                                onTogglePinned = { id -> scope.launch { repo.togglePinned(id) } },
                                onToggleSub = { taskId, subId ->
                                    scope.launch { repo.toggleSubtask(taskId, subId) }
                                },
                                onEdit = onEditTask,
                                onDelete = { id -> scope.launch { repo.deleteTask(id) } },
                                onClearCompleted = { scope.launch { repo.clearCompletedTasks() } },
                                showCompleted = prefs.showCompletedTasks,
                                onOpenDetails = { task -> detailsTaskId = task.id },
                                tagName = { tagId -> prefs.tags.firstOrNull { it.id == tagId }?.name },
                                projectName = { projectId -> projectsById[projectId]?.name },
                                noteCount = { task -> notesForTask(task).size },
                                onOpenNotes = { task ->
                                    val linked = notesForTask(task)
                                    if (linked.isNotEmpty()) {
                                        taskNotesSheet = task to linked
                                    }
                                },
                                onMoveProject = { task -> projectMoveTask = task },
                                dimScroll = prefs.dimScroll,
                                onOpenFavorite = { note -> previewNote = note }
                            )
                        }

                        HomeTab.INBOX -> {
                            val inboxHasVisible = if (prefs.showCompletedTasks) {
                                inboxTasks.isNotEmpty()
                            } else {
                                inboxTasks.any { !it.done }
                            }
                            FlatList(
                                tasks = inboxTasks,
                                favoriteNotes = favoriteNotes,
                                emptyStateTitle = if (!inboxHasVisible) {
                                    stringResource(Res.string.home_empty_inbox_title)
                                } else {
                                    null
                                },
                                emptyStateBody = if (!inboxHasVisible) {
                                    stringResource(Res.string.home_empty_inbox_body)
                                } else {
                                    null
                                },
                                showEmptyMascot = !inboxHasVisible,
                                onToggleDone = { id ->
                                    val task = tasks.firstOrNull { it.id == id }
                                    if (task != null && !task.done) {
                                        celebrationTrigger.intValue += 1
                                    }
                                    scope.launch { repo.toggleDone(id) }
                                },
                                onTogglePinned = { id -> scope.launch { repo.togglePinned(id) } },
                                onToggleSub = { taskId, subId ->
                                    scope.launch { repo.toggleSubtask(taskId, subId) }
                                },
                                onEdit = onEditTask,
                                onDelete = { id -> scope.launch { repo.deleteTask(id) } },
                                onClearCompleted = { scope.launch { repo.clearCompletedTasks() } },
                                showCompleted = prefs.showCompletedTasks,
                                onOpenDetails = { task -> detailsTaskId = task.id },
                                tagName = { tagId -> prefs.tags.firstOrNull { it.id == tagId }?.name },
                                projectName = { projectId -> projectsById[projectId]?.name },
                                noteCount = { task -> notesForTask(task).size },
                                onOpenNotes = { task ->
                                    val linked = notesForTask(task)
                                    if (linked.isNotEmpty()) {
                                        taskNotesSheet = task to linked
                                    }
                                },
                                onMoveProject = { task -> projectMoveTask = task },
                                dimScroll = prefs.dimScroll,
                                onOpenFavorite = { note -> previewNote = note }
                            )
                        }

                        HomeTab.CALENDAR -> {
                            CalendarTab(
                                tasks = filtered,
                                onToggleDone = { id ->
                                    val task = tasks.firstOrNull { it.id == id }
                                    if (task != null && !task.done) {
                                        celebrationTrigger.intValue += 1
                                    }
                                    scope.launch { repo.toggleDone(id) }
                                },
                                onTogglePinned = { id -> scope.launch { repo.togglePinned(id) } },
                                onToggleSub = { taskId, subId ->
                                    scope.launch { repo.toggleSubtask(taskId, subId) }
                                },
                                onEdit = onEditTask,
                                onDelete = { id -> scope.launch { repo.deleteTask(id) } },
                                onClearCompleted = { scope.launch { repo.clearCompletedTasks() } },
                                showCompleted = prefs.showCompletedTasks,
                                onOpenDetails = { task -> detailsTaskId = task.id },
                                tagName = { tagId -> prefs.tags.firstOrNull { it.id == tagId }?.name },
                                projectName = { projectId -> projectsById[projectId]?.name },
                                noteCount = { task -> notesForTask(task).size },
                                onOpenNotes = { task ->
                                    val linked = notesForTask(task)
                                    if (linked.isNotEmpty()) {
                                        taskNotesSheet = task to linked
                                    }
                                },
                                onMoveProject = { task -> projectMoveTask = task },
                                dimScroll = prefs.dimScroll,
                                backdrop = listBackdrop
                            )
                        }
                    }
                }

                SegmentedTabs(
                    items = listOf(
                        stringResource(Res.string.tab_timeline),
                        stringResource(Res.string.tab_calendar),
                        stringResource(Res.string.tab_inbox)
                    ),
                    selectedIndex = when (tab) {
                        HomeTab.TIMELINE -> 0
                        HomeTab.CALENDAR -> 1
                        HomeTab.INBOX -> 2
                    },
                    onSelect = { index ->
                        tab = when (index) {
                            0 -> HomeTab.TIMELINE
                            1 -> HomeTab.CALENDAR
                            else -> HomeTab.INBOX
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 96.dp)
                )
            }
        }

        detailsTaskId?.let { id ->
            val current = tasks.firstOrNull { it.id == id }
            if (current == null) {
                detailsTaskId = null
            } else {
                TaskDetailsSheet(
                    task = current,
                    tagName = { tagId -> prefs.tags.firstOrNull { it.id == tagId }?.name },
                    notes = notesForTask(current),
                    onOpenNotes = {
                        val linked = notesForTask(current)
                        if (linked.isNotEmpty()) {
                            taskNotesSheet = current to linked
                        }
                    },
                    onToggleSub = { subId ->
                        scope.launch { repo.toggleSubtask(current.id, subId) }
                    },
                    onToggleDone = {
                        if (!current.done) {
                            celebrationTrigger.intValue += 1
                        }
                        scope.launch { repo.toggleDone(current.id) }
                    },
                    onEdit = { onEditTask(current) },
                    onClose = { detailsTaskId = null }
                )
            }
        }

        previewNote?.let { note ->
            NotePreviewDialog(
                note = note,
                onEdit = { onEditNote(note.id); previewNote = null },
                onClose = { previewNote = null },
                dimScroll = prefs.dimScroll
            )
        }

        taskNotesSheet?.let { (task, list) ->
            TaskNotesSheet(
                taskTitle = task.title,
                notes = list,
                onOpenNote = { note ->
                    taskNotesSheet = null
                    previewNote = note
                },
                onDismiss = { taskNotesSheet = null }
            )
        }

        if (showSort) {
            SortSheet(
                current = prefs.sort,
                showCompleted = prefs.showCompletedTasks,
                onShowCompleted = { show -> scope.launch { repo.setShowCompletedTasks(show) } },
                onApply = { cfg -> scope.launch { repo.setSort(cfg) } },
                onDismiss = { showSort = false }
            )
        }

        projectMoveTask?.let { task ->
            MoveTaskToProjectDialog(
                task = task,
                projects = projects,
                onPick = { projectId ->
                    scope.launch { repo.assignTaskToProject(task.id, projectId) }
                    projectMoveTask = null
                },
                onDismiss = { projectMoveTask = null }
            )
        }
    }
}

@Composable
fun EmptyState(title: String, body: String, showMascot: Boolean) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showMascot) {
            MeteorHeader()
        }
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
