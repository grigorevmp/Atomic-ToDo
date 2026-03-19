package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.platform.AppInfo
import com.grigorevmp.simpletodo.ui.settings.common.AccountPill
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_changelog
import simpletodo.composeapp.generated.resources.settings_link_contact
import simpletodo.composeapp.generated.resources.settings_link_github
import simpletodo.composeapp.generated.resources.settings_open
import simpletodo.composeapp.generated.resources.settings_status_badge_beta
import simpletodo.composeapp.generated.resources.settings_status_badge_local
import simpletodo.composeapp.generated.resources.settings_status_badge_stable
import simpletodo.composeapp.generated.resources.settings_status_desc
import simpletodo.composeapp.generated.resources.settings_status_title
import simpletodo.composeapp.generated.resources.settings_version_title


@Composable
fun AppStatusAndAboutCard() {
    val versionName = remember { AppInfo.versionName }
    val uriHandler = LocalUriHandler.current
    var showChangelog by remember { mutableStateOf(false) }
    SettingsCard(
        title = stringResource(Res.string.settings_status_title),
        description = stringResource(Res.string.settings_status_desc)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AccountPill(text = stringResource(Res.string.settings_status_badge_stable))
            AccountPill(text = stringResource(Res.string.settings_status_badge_local))
        }
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResourceLink(
                    title = stringResource(Res.string.settings_version_title) + ": $versionName",
                    actionLabel = stringResource(Res.string.settings_changelog),
                    onClick = { showChangelog = true }
                )
                ResourceLink(
                    title = stringResource(Res.string.settings_link_github),
                    actionLabel = stringResource(Res.string.settings_open),
                    onClick = { uriHandler.openUri("https://github.com/grigorevmp/SimpleToDo") }
                )
                ResourceLink(
                    title = stringResource(Res.string.settings_link_contact),
                    actionLabel = stringResource(Res.string.settings_open),
                    onClick = { uriHandler.openUri("https://t.me/grigorevmp") }
                )
            }
        }
    }
    if (showChangelog) {
        ChangeLogAlertDialog(onDismiss = { showChangelog = false })
    }
}

@Composable
private fun ResourceLink(
    title: String,
    actionLabel: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Text(
            actionLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview
@Composable
fun StatusCardPreview() {
    AppStatusAndAboutCard()
}
