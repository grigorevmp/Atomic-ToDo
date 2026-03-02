package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.model.Tag
import com.grigorevmp.simpletodo.platform.isIos
import com.grigorevmp.simpletodo.ui.components.AppIconId
import com.grigorevmp.simpletodo.ui.components.AtomSpinnerIcon
import com.grigorevmp.simpletodo.ui.components.PlatformIcon
import org.jetbrains.compose.resources.stringResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.home_filter_cd
import simpletodo.composeapp.generated.resources.home_tag_all
import simpletodo.composeapp.generated.resources.home_tag_none
import simpletodo.composeapp.generated.resources.home_tags_cd

@Composable
internal fun TopBar(
    tagsShown: Boolean,
    motivation: String,
    onSort: () -> Unit,
    onToggleTags: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AtomSpinnerIcon(
                size = 30.dp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 2.dp)
                    .fillMaxHeight()
                    .widthIn(max = 220.dp)
            ) {
                Text(
                    "Atomic ToDo",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    motivation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = if (isIos) FontStyle.Normal else FontStyle.Italic,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(
                            iterations = Int.MAX_VALUE
                        )
                )
            }
        }

        Row {
            IconButton(onClick = onToggleTags) {
                PlatformIcon(
                    id = AppIconId.Tag,
                    contentDescription = stringResource(Res.string.home_tags_cd),
                    tint = if (tagsShown) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onSort) {
                PlatformIcon(
                    id = AppIconId.Filter,
                    contentDescription = stringResource(Res.string.home_filter_cd),
                    tint = LocalContentColor.current,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
internal fun TagFilters(
    selectedTagId: String?,
    tags: List<Tag>,
    onPick: (String?) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 2.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedTagId == null,
            onClick = { onPick(null) },
            label = { Text(stringResource(Res.string.home_tag_all)) }
        )
        FilterChip(
            selected = selectedTagId == "__no_tag__",
            onClick = { onPick("__no_tag__") },
            label = { Text(stringResource(Res.string.home_tag_none)) }
        )
        tags.forEach { tag ->
            FilterChip(
                selected = selectedTagId == tag.id,
                onClick = { onPick(tag.id) },
                label = { Text(tag.name) }
            )
        }
    }
}
