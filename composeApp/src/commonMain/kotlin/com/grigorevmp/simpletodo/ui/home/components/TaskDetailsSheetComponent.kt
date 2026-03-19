package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.grigorevmp.simpletodo.model.Importance
import com.grigorevmp.simpletodo.model.Note
import com.grigorevmp.simpletodo.model.TodoTask
import com.grigorevmp.simpletodo.platform.isIos
import com.grigorevmp.simpletodo.ui.components.AppIconId
import com.grigorevmp.simpletodo.ui.components.CircleCheckbox
import com.grigorevmp.simpletodo.ui.components.FlameIcon
import com.grigorevmp.simpletodo.ui.components.NoteIcon
import com.grigorevmp.simpletodo.ui.components.PlatformIcon
import com.grigorevmp.simpletodo.ui.components.SimpleIcons
import com.grigorevmp.simpletodo.util.formatDeadline
import com.grigorevmp.simpletodo.util.nowInstant
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_date_due
import simpletodo.composeapp.generated.resources.home_date_planned
import simpletodo.composeapp.generated.resources.home_dates_title
import simpletodo.composeapp.generated.resources.home_details
import simpletodo.composeapp.generated.resources.home_estimate
import simpletodo.composeapp.generated.resources.home_in_days
import simpletodo.composeapp.generated.resources.home_mark_done
import simpletodo.composeapp.generated.resources.home_note_plural
import simpletodo.composeapp.generated.resources.home_note_singular
import simpletodo.composeapp.generated.resources.home_overdue
import simpletodo.composeapp.generated.resources.home_subtasks
import simpletodo.composeapp.generated.resources.home_unmark_done
import simpletodo.composeapp.generated.resources.hours_short
import simpletodo.composeapp.generated.resources.task_close
import simpletodo.composeapp.generated.resources.task_edit

@Composable
internal fun TaskDetailsSheet(
    task: TodoTask,
    tagName: (String?) -> String?,
    notes: List<Note>,
    onOpenNotes: () -> Unit,
    onToggleSub: (String) -> Unit,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit,
    onClose: () -> Unit
) {
    val iconButtonSize = if (isIos) 44.dp else 48.dp
    val backIconSize = if (isIos) 30.dp else 36.dp
    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val dialogContainerColor = if (isLightTheme) Color.White else MaterialTheme.colorScheme.surface
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 760.dp)
                .heightIn(max = 820.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = dialogContainerColor,
            tonalElevation = 6.dp,
            shadowElevation = 14.dp
        ) {
            Box(Modifier.fillMaxWidth().fillMaxHeight()) {
                ImportanceFlameBackdrop(
                    importance = task.importance,
                    modifier = Modifier
                        .matchParentSize()
                        .padding(8.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(iconButtonSize)
                        ) {
                            Icon(
                                imageVector = SimpleIcons.ArrowLeft,
                                contentDescription = stringResource(Res.string.task_close),
                                modifier = Modifier.size(backIconSize)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(Res.string.home_details),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                item {
                    val tagLabel = tagName(task.tagId)
                    Box(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                tagLabel?.let { name ->
                                    TagChipLabel(name)
                                }
                                if (notes.isNotEmpty()) {
                                    if (tagLabel != null) {
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    NoteChipLabel(
                                        count = notes.size,
                                        onOpen = onOpenNotes
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    DateInfoSection(task)
                }

                task.estimateHours?.let {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.home_estimate),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    formatHours(it),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (task.plan.isNotBlank()) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.home_details),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(task.plan, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                if (task.subtasks.isNotEmpty()) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.home_subtasks),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                SubtasksInteractive(task, onToggleSub)
                            }
                        }
                    }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onEdit) { Text(stringResource(Res.string.task_edit)) }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onToggleDone) {
                            Text(
                                if (task.done) {
                                    stringResource(Res.string.home_unmark_done)
                                } else {
                                    stringResource(Res.string.home_mark_done)
                                }
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun formatHours(v: Double): String {
    val scaled = (v * 10).roundToInt()
    val intPart = scaled / 10
    val frac = abs(scaled % 10)
    val text = if (frac == 0) {
        intPart.toString()
    } else {
        "$intPart.$frac"
    }
    return "$text ${stringResource(Res.string.hours_short)}"
}

@Composable
private fun TagChipLabel(name: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlatformIcon(
                id = AppIconId.Tag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NoteChipLabel(
    count: Int,
    onOpen: () -> Unit
) {
    val label =
        if (count == 1) stringResource(Res.string.home_note_singular) else stringResource(Res.string.home_note_plural)
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
        modifier = Modifier.clickable { onOpen() }
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(NoteIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(6.dp))
            Text(
                if (count == 1) label else "$label $count",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DateInfoSection(task: TodoTask) {
    val planned = task.plannedAt
    val deadline = task.deadline
    if (planned == null && deadline == null) return

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(Res.string.home_dates_title),
                style = MaterialTheme.typography.titleMedium
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                planned?.let {
                    DateInfoCard(
                        title = stringResource(Res.string.home_date_planned),
                        instant = it
                    )
                }
                deadline?.let {
                    DateInfoCard(
                        title = stringResource(Res.string.home_date_due),
                        instant = it
                    )
                }
            }
        }
    }
}

@Composable
private fun DateInfoCard(
    title: String,
    instant: Instant
) {
    val days = daysUntil(instant)
    val isOverdue = instant.toEpochMilliseconds() < nowInstant().toEpochMilliseconds()
    val badge = if (isOverdue) {
        stringResource(Res.string.home_overdue)
    } else {
        stringResource(Res.string.home_in_days, days.toString())
    }
    val badgeColor =
        if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val badgeOn =
        if (isOverdue) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.labelLarge)
                Text(
                    formatDeadline(instant),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(shape = MaterialTheme.shapes.small, color = badgeColor) {
                Text(
                    badge,
                    style = MaterialTheme.typography.labelMedium,
                    color = badgeOn,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun  SubtasksInteractive(
    task: TodoTask,
    onToggleSub: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        task.subtasks.forEach { subtask ->
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            ) {
                Row(
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleCheckbox(
                        checked = subtask.done,
                        onCheckedChange = { onToggleSub(subtask.id) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(subtask.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ImportanceFlameBackdrop(
    importance: Importance,
    modifier: Modifier = Modifier
) {
    val count = when (importance) {
        Importance.LOW -> 0
        Importance.NORMAL -> 1
        Importance.HIGH -> 3
        Importance.CRITICAL -> 6
    }
    if (count == 0) return
    val sizes = listOf(18.dp, 22.dp, 26.dp, 30.dp, 34.dp, 38.dp)
    val positions = flamePositions(count)
    BoxWithConstraints(modifier = modifier.alpha(0.85f)) {
        val width = maxWidth
        val height = maxHeight
        positions.forEachIndexed { index, (x, y) ->
            val size = sizes[index.coerceAtMost(sizes.lastIndex)]
            Icon(
                imageVector = FlameIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                modifier = Modifier
                    .width(size)
                    .height(size)
                    .offset(x = width * x, y = height * y)
            )
        }
    }
}

private fun flamePositions(count: Int): List<Pair<Float, Float>> {
    val all = listOf(
        0.08f to 0.12f,
        0.68f to 0.10f,
        0.18f to 0.52f,
        0.62f to 0.48f,
        0.10f to 0.70f,
        0.72f to 0.68f
    )
    return all.take(count)
}

private fun daysUntil(deadline: Instant): Int {
    val now = nowInstant()
    val diffMs = deadline.toEpochMilliseconds() - now.toEpochMilliseconds()
    val dayMs = 24 * 60 * 60 * 1000L
    val days = ((diffMs + dayMs - 1) / dayMs).toInt()
    return if (days < 0) 0 else days
}
