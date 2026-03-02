package com.grigorevmp.simpletodo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
expect fun PlatformDeleteIcon(
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier
)
