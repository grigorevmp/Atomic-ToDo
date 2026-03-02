package com.grigorevmp.simpletodo.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import simpletodo.composeapp.generated.resources.Res
import simpletodo.composeapp.generated.resources.delete

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun PlatformDeleteIcon(
    contentDescription: String?,
    tint: Color,
    modifier: Modifier
) {
    Icon(
        painter = painterResource(Res.drawable.delete),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
    )
}
