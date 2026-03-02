package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.platform.FileExportLauncher
import com.grigorevmp.simpletodo.platform.FileImportLauncher
import com.grigorevmp.simpletodo.platform.isIos
import com.grigorevmp.simpletodo.platform.rememberFileExportLauncher
import com.grigorevmp.simpletodo.platform.rememberFileImportLauncher
import com.grigorevmp.simpletodo.ui.components.SimpleIcons
import com.grigorevmp.simpletodo.ui.settings.common.DataActionTone
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_close
import simpletodo.composeapp.generated.resources.settings_copy
import simpletodo.composeapp.generated.resources.settings_data_delete
import simpletodo.composeapp.generated.resources.settings_data_export
import simpletodo.composeapp.generated.resources.settings_data_import
import simpletodo.composeapp.generated.resources.settings_data_title
import simpletodo.composeapp.generated.resources.settings_delete
import simpletodo.composeapp.generated.resources.settings_delete_desc
import simpletodo.composeapp.generated.resources.settings_delete_title
import simpletodo.composeapp.generated.resources.settings_export_error
import simpletodo.composeapp.generated.resources.settings_export_title
import simpletodo.composeapp.generated.resources.settings_import_action
import simpletodo.composeapp.generated.resources.settings_import_error
import simpletodo.composeapp.generated.resources.settings_import_hint
import simpletodo.composeapp.generated.resources.settings_import_title


@Composable
fun ExportAlertDialog(
    exportText: String,
    clipboard: ClipboardManager,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_export_title)) },
        text = {
            OutlinedTextField(
                value = exportText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(exportText))
            }) { Text(stringResource(Res.string.settings_copy)) }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) { Text(stringResource(Res.string.settings_close)) }
        }
    )
}

@Composable
fun ImportAlertDialog(
    importText: String,
    importError: Boolean,
    scope: CoroutineScope,
    repo: TodoRepository,
    onImportTextChange: (String) -> Unit,
    onImportErrorChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_import_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = importText,
                    onValueChange = {
                        onImportTextChange(it)
                        onImportErrorChange(false)
                    },
                    placeholder = { Text(stringResource(Res.string.settings_import_hint)) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp)
                )
                if (importError) {
                    Text(
                        stringResource(Res.string.settings_import_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    val result = repo.importData(importText)
                    if (result.isSuccess) {
                        onDismiss()
                    } else {
                        onImportErrorChange(true)
                    }
                }
            }) { Text(stringResource(Res.string.settings_import_action)) }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) { Text(stringResource(Res.string.settings_close)) }
        }
    )
}

@Composable
fun DeleteDataAlertDialog(
    scope: CoroutineScope,
    repo: TodoRepository,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_delete_title)) },
        text = { Text(stringResource(Res.string.settings_delete_desc)) },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    repo.clearAllData()
                    onDismiss()
                }
            }) {
                Text(
                    stringResource(Res.string.settings_delete),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) { Text(stringResource(Res.string.settings_close)) }
        }
    )
}

@Composable
fun DataManagementSection(
    repo: TodoRepository
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showDeleteDataDialog by remember { mutableStateOf(false) }
    var exportText by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf(false) }
    var exportFailed by remember { mutableStateOf(false) }

    val useFileImportExport = !isIos
    val exportLauncher = if (useFileImportExport) {
        rememberFileExportLauncher(defaultName = "simpletodo-backup.json") { ok ->
            if (!ok) exportFailed = true
        }
    } else null
    val importLauncher = if (useFileImportExport) {
        rememberFileImportLauncher { content ->
            if (content.isNullOrBlank()) {
                importError = true
            } else {
                scope.launch {
                    val result = repo.importData(content)
                    importError = result.isFailure
                }
            }
        }
    } else null

    DataActionsCard(
        scope = scope,
        repo = repo,
        useFileImportExport = useFileImportExport,
        exportLauncher = exportLauncher,
        importLauncher = importLauncher,
        exportFailed = exportFailed,
        importError = importError,
        onExportFailedChange = { exportFailed = it },
        onImportErrorChange = { importError = it },
        onExportTextChange = { exportText = it },
        onImportTextChange = { importText = it },
        onShowExportDialogChange = { showExportDialog = it },
        onShowImportDialogChange = { showImportDialog = it },
        onShowDeleteDataDialogChange = { showDeleteDataDialog = it }
    )

    if (!useFileImportExport && showExportDialog) {
        ExportAlertDialog(
            exportText = exportText,
            clipboard = clipboard,
            onDismiss = { showExportDialog = false }
        )
    }

    if (!useFileImportExport && showImportDialog) {
        ImportAlertDialog(
            importText = importText,
            importError = importError,
            scope = scope,
            repo = repo,
            onImportTextChange = { importText = it },
            onImportErrorChange = { importError = it },
            onDismiss = { showImportDialog = false }
        )
    }

    if (showDeleteDataDialog) {
        DeleteDataAlertDialog(
            scope = scope,
            repo = repo,
            onDismiss = { showDeleteDataDialog = false }
        )
    }
}

@Composable
fun DataActionsCard(
    scope: CoroutineScope,
    repo: TodoRepository,
    useFileImportExport: Boolean,
    exportLauncher: FileExportLauncher?,
    importLauncher: FileImportLauncher?,
    exportFailed: Boolean,
    importError: Boolean,
    onExportFailedChange: (Boolean) -> Unit,
    onImportErrorChange: (Boolean) -> Unit,
    onExportTextChange: (String) -> Unit,
    onImportTextChange: (String) -> Unit,
    onShowExportDialogChange: (Boolean) -> Unit,
    onShowImportDialogChange: (Boolean) -> Unit,
    onShowDeleteDataDialogChange: (Boolean) -> Unit
) {
    SettingsCard(title = stringResource(Res.string.settings_data_title)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DataActionButton(
                text = stringResource(Res.string.settings_data_export),
                icon = SimpleIcons.ArrowUp,
                modifier = Modifier.weight(1f),
                onClick = {
                    onExportFailedChange(false)
                    if (useFileImportExport) {
                        scope.launch {
                            val exported = repo.exportData()
                            onExportTextChange(exported)
                            exportLauncher?.launch(exported)
                        }
                    } else {
                        scope.launch {
                            val exported = repo.exportData()
                            onExportTextChange(exported)
                            onShowExportDialogChange(true)
                        }
                    }
                }
            )
            DataActionButton(
                text = stringResource(Res.string.settings_data_import),
                icon = SimpleIcons.ArrowDown,
                modifier = Modifier.weight(1f),
                onClick = {
                    onImportErrorChange(false)
                    if (useFileImportExport) {
                        importLauncher?.launch()
                    } else {
                        onImportTextChange("")
                        onShowImportDialogChange(true)
                    }
                }
            )
        }
        DataActionButton(
            text = stringResource(Res.string.settings_data_delete),
            icon = SimpleIcons.Delete,
            tone = DataActionTone.Danger,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onShowDeleteDataDialogChange(true) }
        )
        if (useFileImportExport && exportFailed) {
            Text(
                stringResource(Res.string.settings_export_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (useFileImportExport && importError) {
            Text(
                stringResource(Res.string.settings_import_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DataActionButton(
    text: String,
    icon: ImageVector,
    tone: DataActionTone = DataActionTone.Default,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.large
    val primary = if (tone == DataActionTone.Danger) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }
    val container = lerp(
        MaterialTheme.colorScheme.surfaceVariant,
        primary,
        0.12f
    )
    val border = lerp(
        MaterialTheme.colorScheme.outlineVariant,
        primary,
        0.35f
    )
    val content = if (tone == DataActionTone.Danger) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Surface(
        shape = shape,
        tonalElevation = 0.dp,
        color = container,
        border = BorderStroke(1.dp, border),
        modifier = modifier
            .height(62.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = primary.copy(alpha = 0.18f),
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = content
            )
        }
    }
}
