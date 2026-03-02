package com.grigorevmp.simpletodo.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun PlatformDeleteIcon(
    contentDescription: String?,
    tint: Color,
    modifier: Modifier
) {
    Icon(
        imageVector = DeleteIcon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
    )
}
