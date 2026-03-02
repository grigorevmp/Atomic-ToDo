package com.grigorevmp.simpletodo.ui.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.NoteFolder
import com.grigorevmp.simpletodo.platform.PlatformBackHandler
import com.grigorevmp.simpletodo.ui.notes.components.NotesBrowserContent

@Composable
fun NotesScreen(
    repo: TodoRepository,
    createNoteSignal: Int,
    onCreateNoteHandled: () -> Unit,
    openNoteId: String?,
    onOpenNoteHandled: () -> Unit,
    onEditorVisibleChange: (Boolean) -> Unit = {},
    onBackFromRoot: () -> Unit = {}
) {
    val tasks by repo.tasks.collectAsState()
    val notes by repo.notes.collectAsState()
    val links by repo.taskNoteLinks.collectAsState()
    val folders by repo.noteFolders.collectAsState()
    val projects by repo.projects.collectAsState()
    val prefs by repo.prefs.collectAsState()

    var currentFolderId by remember { mutableStateOf<String?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var editNote by remember { mutableStateOf<Note?>(null) }

    PlatformBackHandler(enabled = !showEditor) {
        if (currentFolderId != null) {
            currentFolderId = parentFolderId(currentFolderId, folders)
        } else {
            onBackFromRoot()
        }
    }

    LaunchedEffect(createNoteSignal) {
        if (createNoteSignal > 0) {
            editNote = null
            showEditor = true
            onCreateNoteHandled()
        }
    }

    LaunchedEffect(openNoteId, notes) {
        val targetId = openNoteId ?: return@LaunchedEffect
        val note = notes.firstOrNull { it.id == targetId }
        if (note != null) {
            currentFolderId = note.folderId
            editNote = note
            showEditor = true
        }
        onOpenNoteHandled()
    }

    LaunchedEffect(showEditor) {
        onEditorVisibleChange(showEditor)
    }

    NotesBrowserContent(
        repo = repo,
        tasks = tasks,
        notes = notes,
        links = links,
        folders = folders,
        projects = projects,
        prefs = prefs,
        currentFolderId = currentFolderId,
        onCurrentFolderChange = { currentFolderId = it },
        onOpenNote = {
            editNote = it
            showEditor = true
        }
    )

    if (showEditor) {
        NoteEditorScreen(
            repo = repo,
            initial = editNote,
            tasks = tasks,
            folderId = currentFolderId,
            onDismiss = { showEditor = false }
        )
    }

}

private fun parentFolderId(currentFolderId: String?, folders: List<NoteFolder>): String? {
    if (currentFolderId == null) return null
    val byId = folders.associateBy { it.id }
    return byId[currentFolderId]?.parentId
}
