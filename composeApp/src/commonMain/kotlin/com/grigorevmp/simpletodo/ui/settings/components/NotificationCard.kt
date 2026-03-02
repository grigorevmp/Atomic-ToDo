package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.platform.NotificationPermissionGate
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_lead_time_label
import simpletodo.composeapp.generated.resources.settings_notifications_desc
import simpletodo.composeapp.generated.resources.settings_notifications_title
import simpletodo.composeapp.generated.resources.settings_pinned_notifications_title


@Composable
fun NotificationCard(
    prefs: AppPrefs,
    scope: CoroutineScope,
    repo: TodoRepository
) {
    var leadMinutesText by remember { mutableStateOf(prefs.reminderLeadMinutes.toString()) }
    LaunchedEffect(prefs.reminderLeadMinutes) {
        leadMinutesText = prefs.reminderLeadMinutes.toString()
    }

    SettingsCard(
        title = stringResource(Res.string.settings_notifications_title),
        description = stringResource(Res.string.settings_notifications_desc),
        toggleChecked = prefs.remindersEnabled,
        onToggle = { enabled -> scope.launch { repo.setReminders(enabled) } }
    ) {
        NotificationPermissionGate(remindersEnabled = prefs.remindersEnabled)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(Res.string.settings_pinned_notifications_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f).padding(end = 12.dp)
            )
            Switch(
                checked = prefs.pinPinnedInNotifications,
                onCheckedChange = { enabled ->
                    scope.launch { repo.setPinnedNotifications(enabled) }
                }
            )
        }
        OutlinedTextField(
            value = leadMinutesText,
            onValueChange = { text ->
                val cleaned = text.filter { it.isDigit() }
                leadMinutesText = cleaned
                val value = cleaned.toIntOrNull() ?: return@OutlinedTextField
                scope.launch { repo.setLeadMinutes(value) }
            },
            label = { Text(stringResource(Res.string.settings_lead_time_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
