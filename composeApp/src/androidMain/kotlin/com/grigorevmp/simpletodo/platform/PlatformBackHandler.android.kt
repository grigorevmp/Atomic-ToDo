package com.grigorevmp.simpletodo.platform

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    PredictiveBackHandler(enabled = enabled) { progress ->
        try {
            progress.collect { /* progress consumed by system animation */ }
            onBack()
        } catch (_: CancellationException) {
            // Gesture cancelled by user: do nothing.
        }
    }
}
