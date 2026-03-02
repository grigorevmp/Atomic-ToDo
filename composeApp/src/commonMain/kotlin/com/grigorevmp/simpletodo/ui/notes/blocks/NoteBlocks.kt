package com.grigorevmp.simpletodo.ui.notes.blocks

import com.grigorevmp.simpletodo.model.DividerBlock
import com.grigorevmp.simpletodo.model.NoteBlock
import com.grigorevmp.simpletodo.model.NoteBlockType
import com.grigorevmp.simpletodo.model.TextBlock
import com.grigorevmp.simpletodo.util.newId

data class NoteBlockDraft(
    val id: String,
    val type: NoteBlockType,
    val text: String,
    val checked: Boolean = false
)

fun defaultBlocksFromContent(content: String): List<NoteBlock> {
    val text = content.trim()
    return if (text.isBlank()) {
        listOf(TextBlock(id = newId("nb"), type = NoteBlockType.PARAGRAPH, text = ""))
    } else {
        listOf(TextBlock(id = newId("nb"), type = NoteBlockType.PARAGRAPH, text = text))
    }
}

fun blocksToPlainText(blocks: List<NoteBlock>): String {
    return blocks.joinToString("\n") { block ->
        when (block) {
            is DividerBlock -> "—"
            is TextBlock -> block.text
            else -> ""
        }
    }.trim()
}
