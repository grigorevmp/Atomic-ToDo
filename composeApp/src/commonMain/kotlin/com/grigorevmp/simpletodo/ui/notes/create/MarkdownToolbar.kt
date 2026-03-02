package com.grigorevmp.simpletodo.ui.notes.create

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp


@Composable
fun MarkdownToolbar(
    onWrapBold: () -> Unit,
    onWrapItalic: () -> Unit,
    onWrapCode: () -> Unit,
    onH1: () -> Unit,
    onH2: () -> Unit,
    onBullet: () -> Unit,
    onTodo: () -> Unit,
    onOrdered: () -> Unit,
    onQuote: () -> Unit,
    onCodeBlock: () -> Unit,
    onLink: () -> Unit
) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp),
    ) {
        val compactPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        TextButton(onClick = onWrapBold, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "B",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onWrapItalic, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "I",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onWrapCode, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "`",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onCodeBlock, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "```",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onLink, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "Link",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onH1, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "H1",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onH2, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "H2",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onBullet, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "-",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onTodo, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "[ ]",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onOrdered, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = "1.",
                fontWeight = Bold
            )
        }
        TextButton(onClick = onQuote, contentPadding = compactPadding, colors = ButtonDefaults.textButtonColors()) {
            Text(
                text = ">",
                fontWeight = Bold
            )
        }
    }
}
