package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.model.Tag
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import com.grigorevmp.simpletodo.ui.theme.authorAccentColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_add_tag
import simpletodo.composeapp.generated.resources.settings_delete
import simpletodo.composeapp.generated.resources.settings_done
import simpletodo.composeapp.generated.resources.settings_manage_tags_title
import simpletodo.composeapp.generated.resources.settings_new_tag_label
import simpletodo.composeapp.generated.resources.settings_tags_desc
import simpletodo.composeapp.generated.resources.settings_tags_manage
import simpletodo.composeapp.generated.resources.settings_tags_more
import simpletodo.composeapp.generated.resources.settings_tags_title

@Composable
fun TagsSection(
    repo: TodoRepository,
    prefs: AppPrefs
) {
    val scope = rememberCoroutineScope()
    var showTagsDialog by remember { mutableStateOf(false) }

    TagsCard(
        prefs = prefs,
        onManageTagsClick = { showTagsDialog = true }
    )

    if (showTagsDialog) {
        AlertTagDialog(
            scope = scope,
            repo = repo,
            prefs = prefs,
            onDismiss = { showTagsDialog = false }
        )
    }
}

@Composable
fun TagsCard(
    prefs: AppPrefs,
    onManageTagsClick: () -> Unit
) {
    SettingsCard(
        title = stringResource(Res.string.settings_tags_title),
        description = stringResource(Res.string.settings_tags_desc)
    ) {
        Column (
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prefs.tags.take(8).forEach { tag ->
                    TagChip(tag = tag)
                }
                if (prefs.tags.size > 8) {
                    Text(
                        stringResource(
                            Res.string.settings_tags_more,
                            (prefs.tags.size - 8).toString()
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                    )
                }
            }
            OutlinedButton(onClick = onManageTagsClick) {
                Text(stringResource(Res.string.settings_tags_manage))
            }
        }
    }
}

@Composable
fun AlertTagDialog(
    scope: CoroutineScope,
    repo: TodoRepository,
    prefs: AppPrefs,
    onDismiss: () -> Unit
) {
    var newTag by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_manage_tags_title)) },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    label = { Text(stringResource(Res.string.settings_new_tag_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        scope.launch {
                            repo.addTag(newTag)
                            newTag = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(Res.string.settings_add_tag)) }

                val tagsListState = rememberLazyListState()
                Box(Modifier.fillMaxWidth()) {
                    LazyColumn(
                        state = tagsListState,
                        contentPadding = PaddingValues(vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(prefs.tags, key = { it.id }) { t ->
                            TagRow(
                                tag = t,
                                onDelete = { scope.launch { repo.deleteTag(t.id) } }
                            )
                        }
                    }
                    FadingScrollEdges(
                        listState = tagsListState,
                        modifier = Modifier.matchParentSize(),
                        color = MaterialTheme.colorScheme.surface,
                        enabled = prefs.dimScroll
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
            }) { Text(stringResource(Res.string.settings_done)) }
        }
    )
}

@Composable
private fun TagChip(tag: Tag) {
    val colors = authorAccentColors()
    val base = MaterialTheme.colorScheme.primary
    val accent = colors[tag.colorIndex % colors.size]
    val border by animateColorAsState(
        targetValue = lerp(base.copy(alpha = 0.5f), accent.copy(alpha = 0.5f), 0.55f),
        animationSpec = tween(200),
        label = "tag-border"
    )
    val fill = lerp(base.copy(alpha = 0.12f), accent.copy(alpha = 0.22f), 0.6f)
    Surface(
        shape = MaterialTheme.shapes.large,
        color = fill,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(accent, shape = MaterialTheme.shapes.small)
            )
            Text(tag.name, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun TagRow(tag: Tag, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TagChip(tag = tag)
        TextButton(onClick = onDelete) { Text(stringResource(Res.string.settings_delete)) }
    }
}
