package com.grigorevmp.simpletodo.ui.projects.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.platform.isIos
import com.grigorevmp.simpletodo.ui.components.AppIconId
import com.grigorevmp.simpletodo.ui.components.PlatformIcon
import com.grigorevmp.simpletodo.ui.components.SimpleIcons
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.notes_title
import simpletodo.composeapp.generated.resources.settings_title

@Composable
fun SelectedProjectHeader(
    projectName: String,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val iconButtonSize = if (isIos) 44.dp else 48.dp
    val iconSize = if (isIos) 26.dp else 24.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(iconButtonSize)
            ) {
                Icon(
                    imageVector = SimpleIcons.ArrowLeft,
                    contentDescription = stringResource(Res.string.notes_title),
                    modifier = Modifier.size(iconSize)
                )
            }
            Text(
                projectName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(190.dp)
            )
        }
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.size(iconButtonSize)
        ) {
            PlatformIcon(
                id = AppIconId.Settings,
                contentDescription = stringResource(Res.string.settings_title),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
