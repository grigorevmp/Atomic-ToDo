package com.grigorevmp.simpletodo.ui.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.Project
import com.grigorevmp.simpletodo.model.ProjectStatus
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.platform.PlatformBackHandler
import com.grigorevmp.simpletodo.ui.home.components.TaskEditorSheet
import com.grigorevmp.simpletodo.ui.projects.components.CreateProjectDialog
import com.grigorevmp.simpletodo.ui.projects.components.DeleteProjectDialog
import com.grigorevmp.simpletodo.ui.projects.components.EditProjectDialog
import com.grigorevmp.simpletodo.ui.projects.components.MoveTaskDialog
import com.grigorevmp.simpletodo.ui.projects.components.ProjectsListContent
import com.grigorevmp.simpletodo.ui.projects.components.SelectedProjectContent
import com.grigorevmp.simpletodo.util.newId
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.note_editor_untitled

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

    if (selectedProject == null) {
        ProjectsListContent(
            projects = projects,
            tasks = tasks,
            notes = notes,
            dimScroll = prefs.dimScroll,
            onProjectClick = { project -> selectedProjectId = project.id },
            onProjectLongClick = { project -> deleteProjectCandidate = project }
        )
    } else {
        SelectedProjectContent(
            repo = repo,
            project = selectedProject,
            tasks = tasks,
            notes = notes,
            links = links,
            projects = projects,
            prefs = prefs,
            onBack = { selectedProjectId = null },
            onOpenSettings = { showEditProjectDialog = true },
            onEditTask = { task ->
                editTask = task
                showEditor = true
            },
            onMoveTask = { task -> moveTask = task },
            onEditNote = onEditNote
        )
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
                    oldByName[statusName.lowercase()]
                        ?: ProjectStatus(id = newId("pstatus"), name = statusName)
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
            taskProjectId = task.projectId,
            projects = projects,
            onPick = { projectId ->
                scope.launch { repo.assignTaskToProject(task.id, projectId) }
                moveTask = null
            },
            onDismiss = { moveTask = null }
        )
    }

    deleteProjectCandidate?.let { project ->
        DeleteProjectDialog(
            projectName = project.name,
            onDismiss = { deleteProjectCandidate = null },
            onDelete = {
                scope.launch { repo.deleteProject(project.id) }
                if (selectedProjectId == project.id) {
                    selectedProjectId = null
                }
                deleteProjectCandidate = null
            }
        )
    }
}
