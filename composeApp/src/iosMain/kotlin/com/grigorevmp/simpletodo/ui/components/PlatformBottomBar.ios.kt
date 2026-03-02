package com.grigorevmp.simpletodo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.backdrops.LayerBackdrop

@Composable
actual fun PlatformBottomBar(
    tab: AppTab,
    onTab: (AppTab) -> Unit,
    visibleTabs: List<AppTab>,
    createActions: List<CreateAction>,
    enableEffects: Boolean,
    backdrop: LayerBackdrop,
    modifier: Modifier
) {
    // Keep iOS and Android rendering consistent to avoid UIKit tab bar layout/theme issues.
    FloatingNavBar(
        tab = tab,
        onTab = onTab,
        visibleTabs = visibleTabs,
        createActions = createActions,
        enableEffects = enableEffects,
        backdrop = backdrop,
        modifier = modifier
    )
}
