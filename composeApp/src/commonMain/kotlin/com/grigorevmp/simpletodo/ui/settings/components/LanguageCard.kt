package com.grigorevmp.simpletodo.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.model.AppLanguage
import com.grigorevmp.simpletodo.model.AppPrefs
import com.grigorevmp.simpletodo.ui.settings.common.SettingsCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_language_en
import simpletodo.composeapp.generated.resources.settings_language_ru
import simpletodo.composeapp.generated.resources.settings_language_system
import simpletodo.composeapp.generated.resources.settings_language_title


@Composable
fun LanguageCard(
    prefs: AppPrefs,
    scope: CoroutineScope,
    repo: TodoRepository
) {
    SettingsCard(title = stringResource(Res.string.settings_language_title)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = prefs.language == AppLanguage.SYSTEM,
                onClick = { scope.launch { repo.setLanguage(AppLanguage.SYSTEM) } },
                label = { Text(stringResource(Res.string.settings_language_system)) }
            )
            FilterChip(
                selected = prefs.language == AppLanguage.EN,
                onClick = { scope.launch { repo.setLanguage(AppLanguage.EN) } },
                label = { Text(stringResource(Res.string.settings_language_en)) }
            )
            FilterChip(
                selected = prefs.language == AppLanguage.RU,
                onClick = { scope.launch { repo.setLanguage(AppLanguage.RU) } },
                label = { Text(stringResource(Res.string.settings_language_ru)) }
            )
        }
    }
}