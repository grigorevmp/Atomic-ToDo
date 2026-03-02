package com.grigorevmp.simpletodo.ui.home.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color


data class MeteorParticle(
    val start: Offset,
    val c1: Offset,
    val c2: Offset,
    val end: Offset,
    val startNanos: Long,
    val durationNanos: Long,
    val length: Float,
    val stroke: Float,
    val color: Color
)