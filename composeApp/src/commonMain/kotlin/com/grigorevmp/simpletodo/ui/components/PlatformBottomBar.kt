package com.grigorevmp.simpletodo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.backdrops.LayerBackdrop

@Composable
expect fun PlatformBottomBar(
    tab: AppTab,
    onTab: (AppTab) -> Unit,
    visibleTabs: List<AppTab>,
    createActions: List<CreateAction>,
    enableEffects: Boolean,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
)
