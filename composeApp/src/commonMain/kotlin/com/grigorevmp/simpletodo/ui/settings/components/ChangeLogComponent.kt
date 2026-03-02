package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.ui.components.SimpleIcons
import com.grigorevmp.simpletodo.ui.settings.BuildType
import com.grigorevmp.simpletodo.ui.settings.CHANGELOG_ENTRIES
import com.grigorevmp.simpletodo.ui.settings.ChangelogEntry
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_changelog_title
import simpletodo.composeapp.generated.resources.settings_close


@Composable
fun ChangeLogAlertDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_changelog_title)) },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CHANGELOG_ENTRIES.forEach { entry ->
                    ChangelogCard(entry = entry)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
            }) { Text(stringResource(Res.string.settings_close)) }
        }
    )
}

@Composable
private fun ChangelogCard(entry: ChangelogEntry) {
    val accent = when (entry.buildType) {
        BuildType.RELEASE -> MaterialTheme.colorScheme.primary
        BuildType.BETA -> MaterialTheme.colorScheme.tertiary
        BuildType.ALPHA -> MaterialTheme.colorScheme.secondary
    }
    val buildTypeLabel = when (entry.buildType) {
        BuildType.RELEASE -> "release"
        BuildType.BETA -> "beta"
        BuildType.ALPHA -> "alpha"
    }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (entry.buildType == BuildType.BETA) {
                Icon(
                    imageVector = SimpleIcons.Beaker,
                    contentDescription = null,
                    tint = accent.copy(alpha = 0.12f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .size(42.dp)
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "${entry.version} • $buildTypeLabel",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent
                )
                Text(
                    entry.title,
                    style = MaterialTheme.typography.titleSmall
                )
                entry.changes.forEach { item ->
                    Text(
                        "• $item",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
