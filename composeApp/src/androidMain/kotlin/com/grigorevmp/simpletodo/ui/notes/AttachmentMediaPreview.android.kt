package com.grigorevmp.simpletodo.ui.notes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.platform.AndroidContextHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
actual fun AttachmentMediaPreview(
    uri: String,
    mime: String?,
    modifier: Modifier
) {
    val normalized = mime?.lowercase().orEmpty()
    val isImage = normalized.startsWith("image/") || uri.contains(".jpg") || uri.contains(".jpeg") || uri.contains(".png") || uri.contains(".webp")
    val isVideo = normalized.startsWith("video/") || uri.contains(".mp4") || uri.contains(".mov") || uri.contains(".mkv")

    val bitmapState = produceState<Bitmap?>(initialValue = null, uri, mime) {
        value = withContext(Dispatchers.IO) {
            try {
                val context = AndroidContextHolder.appContext
                val parsed = Uri.parse(uri)
                when {
                    isImage -> context.contentResolver.openInputStream(parsed)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                    isVideo -> {
                        val retriever = MediaMetadataRetriever()
                        try {
                            retriever.setDataSource(context, parsed)
                            retriever.getFrameAtTime(0)
                        } finally {
                            retriever.release()
                        }
                    }
                    else -> null
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    val bitmap = bitmapState.value
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isVideo) "Видео" else if (isImage) "Изображение" else "Файл",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
