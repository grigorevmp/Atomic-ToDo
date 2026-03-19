package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.model.ThemeMode
import com.grigorevmp.simpletodo.ui.components.SimpleIcons
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import com.grigorevmp.simpletodo.ui.theme.authorAccentColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_dim_scroll_desc
import simpletodo.composeapp.generated.resources.settings_dim_scroll_title
import simpletodo.composeapp.generated.resources.settings_disable_dark_desc
import simpletodo.composeapp.generated.resources.settings_disable_dark_title
import simpletodo.composeapp.generated.resources.settings_liquid_glass_desc
import simpletodo.composeapp.generated.resources.settings_liquid_glass_title
import simpletodo.composeapp.generated.resources.settings_selected
import simpletodo.composeapp.generated.resources.settings_theme_author
import simpletodo.composeapp.generated.resources.settings_theme_auto
import simpletodo.composeapp.generated.resources.settings_theme_custom
import simpletodo.composeapp.generated.resources.settings_theme_dark_gray
import simpletodo.composeapp.generated.resources.settings_theme_dim
import simpletodo.composeapp.generated.resources.settings_theme_dynamic
import simpletodo.composeapp.generated.resources.settings_theme_material_you
import simpletodo.composeapp.generated.resources.settings_theme_system
import simpletodo.composeapp.generated.resources.settings_theme_title


@Composable
fun ThemeSelectorCard(
    prefs: AppPrefs,
    scope: CoroutineScope,
    repo: TodoRepository
) {
    SettingsCard(
        title = stringResource(Res.string.settings_theme_title)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ThemeCard(
                    title = stringResource(Res.string.settings_theme_system),
                    subtitle = stringResource(Res.string.settings_theme_auto),
                    selected = prefs.themeMode == ThemeMode.SYSTEM,
                    onClick = { scope.launch { repo.setTheme(ThemeMode.SYSTEM) } }
                )
                ThemeCard(
                    title = stringResource(Res.string.settings_theme_dynamic),
                    subtitle = stringResource(Res.string.settings_theme_material_you),
                    selected = prefs.themeMode == ThemeMode.DYNAMIC,
                    onClick = { scope.launch { repo.setTheme(ThemeMode.DYNAMIC) } }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val dimEnabled = !prefs.disableDarkTheme
                val dimModifier = Modifier
                    .alpha(if (dimEnabled) 1f else 0.5f)
                    .padding(bottom = if (dimEnabled) 0.dp else 1.dp)
                ThemeCard(
                    title = stringResource(Res.string.settings_theme_dim),
                    subtitle = stringResource(Res.string.settings_theme_dark_gray),
                    selected = prefs.themeMode == ThemeMode.DIM,
                    onClick = {
                        if (dimEnabled) {
                            scope.launch { repo.setTheme(ThemeMode.DIM) }
                        }
                    },
                    modifier = dimModifier
                )
                ThemeCard(
                    title = stringResource(Res.string.settings_theme_author),
                    subtitle = stringResource(Res.string.settings_theme_custom),
                    selected = prefs.themeMode == ThemeMode.AUTHOR,
                    onClick = { scope.launch { repo.setTheme(ThemeMode.AUTHOR) } }
                )
            }
        }
        SettingToggle(
            title = stringResource(Res.string.settings_disable_dark_title),
            subtitle = stringResource(Res.string.settings_disable_dark_desc),
            checked = prefs.disableDarkTheme,
            onToggle = { enabled -> scope.launch { repo.setDisableDarkTheme(enabled) } }
        )
        if (prefs.themeMode == ThemeMode.AUTHOR) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                authorAccentColors().forEachIndexed { idx, color ->
                    AccentSwatch(
                        color = color,
                        selected = prefs.authorAccentIndex == idx,
                        onClick = { scope.launch { repo.setAuthorAccent(idx) } }
                    )
                }
            }
        }
        SettingToggle(
            title = stringResource(Res.string.settings_dim_scroll_title),
            subtitle = stringResource(Res.string.settings_dim_scroll_desc),
            checked = prefs.dimScroll,
            onToggle = { enabled -> scope.launch { repo.setDimScroll(enabled) } }
        )
        SettingToggle(
            title = stringResource(Res.string.settings_liquid_glass_title),
            subtitle = stringResource(Res.string.settings_liquid_glass_desc),
            checked = prefs.liquidGlass,
            onToggle = { enabled -> scope.launch { repo.setLiquidGlass(enabled) } }
        )
    }
}


@Composable
fun RowScope.ThemeCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(240),
        label = "theme-bg"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (selected) 0.7f else 0.15f,
        animationSpec = tween(240),
        label = "theme-border"
    )
    val elevation by animateDpAsState(
        targetValue = if (selected) 8.dp else 1.dp,
        animationSpec = tween(240),
        label = "theme-elev"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = tween(240),
        label = "theme-scale"
    )
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp,
        shadowElevation = elevation,
        color = bg,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha)),
        modifier = modifier
            .weight(1f)
            .graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        Box(Modifier.padding(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(20.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(22.dp)
            ) {
                val checkBg by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(200),
                    label = "theme-check-bg"
                )
                val checkScale by animateFloatAsState(
                    targetValue = if (selected) 1f else 0.9f,
                    animationSpec = tween(200),
                    label = "theme-check-scale"
                )
                Surface(
                    shape = CircleShape,
                    color = checkBg,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = checkScale, scaleY = checkScale)
                ) {
                    if (selected) {
                        Icon(
                            SimpleIcons.Check,
                            contentDescription = stringResource(Res.string.settings_selected),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val accent by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "toggle-accent"
    )
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = accent)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}


@Composable
private fun AccentSwatch(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = color,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null,
        modifier = Modifier.size(32.dp)
    ) {}
}