package com.grigorevmp.simpletodo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.nav_home
import simpletodo.composeapp.generated.resources.nav_notes
import simpletodo.composeapp.generated.resources.nav_projects
import simpletodo.composeapp.generated.resources.nav_settings

enum class AppTab { HOME, NOTES, PROJECTS, SETTINGS }

data class CreateAction(
    val id: String,
    val label: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun FloatingNavBar(
    tab: AppTab,
    onTab: (AppTab) -> Unit,
    visibleTabs: List<AppTab>,
    createActions: List<CreateAction>,
    backdrop: LayerBackdrop,
    enableEffects: Boolean = true,
    modifier: Modifier = Modifier
) {
    val primaryAction = createActions.firstOrNull()
    val shape = RoundedCornerShape(48.dp)
    val glassColor = MaterialTheme.colorScheme.surfaceVariant
    val glassBrush = Brush.verticalGradient(
        listOf(
            glassColor.copy(alpha = 0.06f),
            glassColor.copy(alpha = 0.18f)
        )
    )
    val fallbackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val isDarkSurface = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val glassOverlay = if (isDarkSurface) {
        Color.Black.copy(alpha = 0.5f)
    } else {
        Color.White.copy(alpha = 0.35f)
    }
    val density = LocalDensity.current
    val blurPx = with(density) { 2.dp.toPx() }
    val lensInnerPx = with(density) { 16.dp.toPx() }
    val lensOuterPx = with(density) { 32.dp.toPx() }

    Surface(
        modifier = modifier
            .wrapContentWidth()
            .animateContentSize(animationSpec = spring(dampingRatio = 0.85f, stiffness = 520f))
            .padding(bottom = 8.dp)
            .padding(horizontal = 4.dp),
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
    ) {
        Box(
            Modifier
                .height(64.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .matchParentSize()
                    .then(
                        if (enableEffects) {
                            Modifier.drawBackdrop(
                                backdrop = backdrop,
                                shape = { shape },
                                effects = {
                                    vibrancy()
                                    blur(blurPx)
                                    lens(lensInnerPx, lensOuterPx)
                                },
                                onDrawSurface = {
                                    drawRect(glassBrush)
                                    drawRect(glassOverlay)
                                }
                            )
                        } else {
                            Modifier.background(fallbackColor, shape)
                        }
                    )
            )

            Row(
                Modifier
                    .wrapContentWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .animateContentSize(animationSpec = spring(dampingRatio = 0.85f, stiffness = 520f)),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (AppTab.HOME in visibleTabs) {
                    NavButton(
                        selected = tab == AppTab.HOME,
                        label = stringResource(Res.string.nav_home),
                        onClick = { onTab(AppTab.HOME) },
                        iconSize = 24.dp,
                        icon = {
                            Icon(
                                HomeIcon,
                                contentDescription = stringResource(Res.string.nav_home),
                                tint = if (tab == AppTab.HOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                            )
                        }
                    )
                }

                if (AppTab.NOTES in visibleTabs) {
                    NavButton(
                        selected = tab == AppTab.NOTES,
                        label = stringResource(Res.string.nav_notes),
                        onClick = { onTab(AppTab.NOTES) },
                        iconSize = 24.dp,
                        icon = {
                            Icon(
                                NotesIcon,
                                contentDescription = stringResource(Res.string.nav_notes),
                                tint = if (tab == AppTab.NOTES) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                            )
                        }
                    )
                }

                if (AppTab.PROJECTS in visibleTabs) {
                    NavButton(
                        selected = tab == AppTab.PROJECTS,
                        label = stringResource(Res.string.nav_projects),
                        onClick = { onTab(AppTab.PROJECTS) },
                        iconSize = 24.dp,
                        icon = {
                            PlatformIcon(
                                id = AppIconId.Projects,
                                contentDescription = stringResource(Res.string.nav_projects),
                                tint = if (tab == AppTab.PROJECTS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                            )
                        }
                    )
                }

                NavButton(
                    selected = tab == AppTab.SETTINGS,
                    label = stringResource(Res.string.nav_settings),
                    onClick = { onTab(AppTab.SETTINGS) },
                    iconSize = 24.dp,
                    icon = {
                        PlatformIcon(
                            id = AppIconId.Settings,
                            contentDescription = stringResource(Res.string.nav_settings),
                            tint = if (tab == AppTab.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                AnimatedVisibility(
                    visible = primaryAction != null,
                    enter = expandHorizontally(animationSpec = tween(durationMillis = 220)) +
                        fadeIn(animationSpec = tween(durationMillis = 180)) +
                        scaleIn(initialScale = 0.86f, animationSpec = tween(durationMillis = 220)),
                    exit = shrinkHorizontally(animationSpec = tween(durationMillis = 180)) +
                        fadeOut(animationSpec = tween(durationMillis = 120)) +
                        scaleOut(targetScale = 0.86f, animationSpec = tween(durationMillis = 180))
                ) {
                    val action = primaryAction ?: return@AnimatedVisibility
                    CreateActionButton(action = action)
                }
            }
        }
    }
}

@Composable
private fun NavButton(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    iconSize: Dp = 24.dp,
    icon: @Composable () -> Unit
) {
    val active by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "nav-active"
    )
    val bg = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f * active)
    val scale = 0.98f + 0.02f * active

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(48.dp),
        color = bg,
        modifier = Modifier
            .width(56.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.width(iconSize).height(iconSize), contentAlignment = Alignment.Center) { icon() }
        }
    }
}

@Composable
private fun CreateActionButton(action: CreateAction) {
    Surface(
        onClick = action.onClick,
        shape = RoundedCornerShape(48.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.width(56.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                action.icon,
                contentDescription = action.contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
