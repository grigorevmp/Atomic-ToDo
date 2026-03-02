package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.ui.components.AppTab
import com.grigorevmp.simpletodo.ui.components.HomeIcon
import com.grigorevmp.simpletodo.ui.components.NotesIcon
import com.grigorevmp.simpletodo.ui.components.ProjectsIcon
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.nav_home
import simpletodo.composeapp.generated.resources.nav_notes
import simpletodo.composeapp.generated.resources.nav_projects
import simpletodo.composeapp.generated.resources.settings_sections_desc
import simpletodo.composeapp.generated.resources.settings_sections_title

@Composable
fun NavTabsCard(
    prefs: AppPrefs,
    scope: CoroutineScope,
    repo: TodoRepository
) {
    SettingsCard(
        title = stringResource(Res.string.settings_sections_title),
        description = stringResource(Res.string.settings_sections_desc)
    ) {
        val onToggleTab: (AppTab) -> Unit = { target ->
            val showHome =
                if (target == AppTab.HOME) !prefs.showHomeTab else prefs.showHomeTab
            val showNotes =
                if (target == AppTab.NOTES) !prefs.showNotesTab else prefs.showNotesTab
            val showProjects =
                if (target == AppTab.PROJECTS) !prefs.showProjectsTab else prefs.showProjectsTab
            if (showHome || showNotes || showProjects) {
                scope.launch {
                    repo.setSectionTabsVisibility(
                        showHome = showHome,
                        showNotes = showNotes,
                        showProjects = showProjects
                    )
                }
            }
        }
        SectionVisibilityBar(
            showHome = prefs.showHomeTab,
            showNotes = prefs.showNotesTab,
            showProjects = prefs.showProjectsTab,
            onToggleTab = onToggleTab
        )
    }
}



@Composable
private fun SectionVisibilityBar(
    showHome: Boolean,
    showNotes: Boolean,
    showProjects: Boolean,
    onToggleTab: (AppTab) -> Unit
) {
    val enabledCount = listOf(showHome, showNotes, showProjects).count { it }
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SectionTabToggleButton(
                label = stringResource(Res.string.nav_home),
                icon = HomeIcon,
                enabled = showHome,
                canToggle = !showHome || enabledCount > 1,
                onClick = { onToggleTab(AppTab.HOME) }
            )
            SectionTabToggleButton(
                label = stringResource(Res.string.nav_notes),
                icon = NotesIcon,
                enabled = showNotes,
                canToggle = !showNotes || enabledCount > 1,
                onClick = { onToggleTab(AppTab.NOTES) }
            )
            SectionTabToggleButton(
                label = stringResource(Res.string.nav_projects),
                icon = ProjectsIcon,
                enabled = showProjects,
                canToggle = !showProjects || enabledCount > 1,
                onClick = { onToggleTab(AppTab.PROJECTS) }
            )
        }
    }
}

@Composable
private fun RowScope.SectionTabToggleButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    canToggle: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.26f,
        animationSpec = tween(180),
        label = "section-tab-alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.97f,
        animationSpec = tween(180),
        label = "section-tab-scale"
    )
    val containerColor = if (enabled) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.08f)
    }
    Surface(
        onClick = {
            if (canToggle) onClick()
        },
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        modifier = Modifier
            .weight(1f)
            .graphicsLayer(
                alpha = alpha,
                scaleX = scale,
                scaleY = scale
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}