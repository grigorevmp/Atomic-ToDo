package com.grigorevmp.simpletodo.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.data.TodoRepository
import com.grigorevmp.simpletodo.ui.components.FadingScrollEdges
import com.grigorevmp.simpletodo.ui.settings.components.AccountComingSoonCard
import com.grigorevmp.simpletodo.ui.settings.components.AppStatusAndAboutCard
import com.grigorevmp.simpletodo.ui.settings.components.DataManagementSection
import com.grigorevmp.simpletodo.ui.settings.components.LanguageCard
import com.grigorevmp.simpletodo.ui.settings.components.NavTabsCard
import com.grigorevmp.simpletodo.ui.settings.components.NotificationCard
import com.grigorevmp.simpletodo.ui.settings.components.TagsSection
import com.grigorevmp.simpletodo.ui.settings.components.ThemeSelectorCard
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.settings_title

@Composable
fun SettingsScreen(
    repo: TodoRepository
) {
    val prefs by repo.prefs.collectAsState()
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TopBar()
            }

            item {
                AccountComingSoonCard()
            }

            item {
                AppStatusAndAboutCard()
            }

            item {
                NotificationCard(prefs, scope, repo)
            }

            item {
                ThemeSelectorCard(prefs, scope, repo)
            }

            item {
                LanguageCard(prefs, scope, repo)
            }

            item {
                NavTabsCard(prefs, scope, repo)
            }

            item {
                TagsSection(repo = repo, prefs = prefs)
            }

            item {
                DataManagementSection(repo = repo)
            }

            item { Spacer(Modifier.height(90.dp)) }
        }

        FadingScrollEdges(
            listState = listState,
            modifier = Modifier.matchParentSize(),
            enabled = prefs.dimScroll
        )
    }

}


@Composable
private fun TopBar() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 2.dp)
                    .fillMaxHeight()
                    .widthIn(max = 220.dp)
            ) {
                Text(
                    stringResource(Res.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


