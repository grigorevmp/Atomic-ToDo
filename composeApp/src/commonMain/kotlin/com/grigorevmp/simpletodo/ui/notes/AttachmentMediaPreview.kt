package com.grigorevmp.simpletodo.ui.notes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AttachmentMediaPreview(
    uri: String,
    mime: String?,
    modifier: Modifier = Modifier
)
