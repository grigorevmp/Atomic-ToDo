package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

data class SegmentedTabItem(
    val label: String,
    val badgeCount: Int = 0
)

@Composable
fun SegmentedTabs(
    items: List<SegmentedTabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val fallbackColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f)
    val active = MaterialTheme.colorScheme.primary
    val activeText = MaterialTheme.colorScheme.onPrimary
    val inactiveText = MaterialTheme.colorScheme.onSurfaceVariant


    val container = MaterialTheme.colorScheme.surfaceVariant
    val containerBrush = Brush.verticalGradient(
        listOf(
            container.copy(alpha = 0.08f),
            container.copy(alpha = 0.22f)
        )
    )
    val density = LocalDensity.current
    val blurPx = with(density) { 3.dp.toPx() }
    val lensInnerPx = with(density) { 10.dp.toPx() }
    val lensOuterPx = with(density) { 20.dp.toPx() }
    val isDarkSurface = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val glassOverlay = if (isDarkSurface) {
        Color.Black.copy(alpha = 0.5f)
    } else {
        Color.White.copy(alpha = 0.35f)
    }

    Box(
        modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(fallbackColor, RoundedCornerShape(64.dp))
            )

            Row {
                items.forEachIndexed { index, item ->
                    SegmentedButton(
                        text = item.label,
                        badgeCount = item.badgeCount,
                        selected = index == selectedIndex,
                        active = active,
                        activeText = activeText,
                        inactiveText = inactiveText,
                        onClick = { onSelect(index) }
                    )
                }
            }
        }
    }
}


@Composable
fun SegmentedButton(
    text: String,
    badgeCount: Int,
    selected: Boolean,
    active: Color,
    activeText: Color,
    inactiveText: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = MaterialTheme.shapes.large,
            color = if (selected) active else Color.Transparent
        ) {
            Box(
                Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    color = if (selected) activeText else inactiveText,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        if (badgeCount > 0) {
            val shownCount = if (badgeCount > 99) "99+" else badgeCount.toString()
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shownCount,
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
