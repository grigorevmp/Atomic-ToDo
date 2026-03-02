package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.grigorevmp.simpletodo.ui.components.ChromeDinoMascot
import com.grigorevmp.simpletodo.ui.home.common.MeteorParticle
import com.grigorevmp.simpletodo.ui.home.common.cubicBezier
import com.grigorevmp.simpletodo.ui.home.common.cubicBezierTangent
import kotlinx.coroutines.isActive
import kotlin.random.Random


@Composable
fun MeteorHeader() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        MeteorField(modifier = Modifier.matchParentSize())
        ChromeDinoMascot(
            size = 48.dp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MeteorField(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    val secondary = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
    val particles = remember { mutableStateListOf<MeteorParticle>() }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    var nowNanos by remember { mutableStateOf(0L) }
    val rng = remember { Random(87321) }

    LaunchedEffect(fieldSize) {
        if (fieldSize.width == 0 || fieldSize.height == 0) return@LaunchedEffect
        var lastSpawn = 0L
        while (isActive) {
            val now = withFrameNanos { it }
            nowNanos = now
            val elapsed = now - lastSpawn
            val spawnDelay = (200L + rng.nextInt(400)) * 1_000_000L
            if (elapsed >= spawnDelay && particles.size < 18) {
                particles.add(
                    createParticle(
                        now = now,
                        size = fieldSize,
                        rng = rng,
                        color = if (rng.nextBoolean()) color else secondary
                    )
                )
                lastSpawn = now
            }
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                if (now - p.startNanos > p.durationNanos) {
                    iterator.remove()
                }
            }
        }
    }

    Canvas(
        modifier = modifier.onSizeChanged { fieldSize = it }
    ) {
        val now = nowNanos
        particles.forEach { p ->
            val t = ((now - p.startNanos).toFloat() / p.durationNanos.toFloat()).coerceIn(0f, 1f)
            val pos = cubicBezier(p.start, p.c1, p.c2, p.end, t)
            val dir = cubicBezierTangent(p.start, p.c1, p.c2, p.end, t)
            val len = p.length
            val tail = Offset(
                pos.x - dir.x * len,
                pos.y - dir.y * len
            )
            val alpha = (1f - t) * 0.9f
            drawLine(
                color = p.color.copy(alpha = alpha),
                start = pos,
                end = tail,
                strokeWidth = p.stroke,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.cornerPathEffect(p.stroke)
            )
        }
    }
}

private fun createParticle(
    now: Long,
    size: IntSize,
    rng: Random,
    color: Color
): MeteorParticle {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    val margin = 6f
    val start = Offset(
        x = rng.nextFloat() * w,
        y = rng.nextFloat() * h * 0.25f
    )
    val end = Offset(
        x = rng.nextFloat() * w,
        y = h - margin
    )
    val c1 = Offset(
        x = start.x + (rng.nextFloat() - 0.5f) * w * 0.4f,
        y = start.y + h * (0.25f + rng.nextFloat() * 0.2f)
    )
    val c2 = Offset(
        x = end.x + (rng.nextFloat() - 0.5f) * w * 0.4f,
        y = end.y - h * (0.25f + rng.nextFloat() * 0.2f)
    )
    val duration = (900L + rng.nextInt(900)) * 1_000_000L
    val length = 12f + rng.nextFloat() * 10f
    val stroke = 1.8f + rng.nextFloat() * 0.8f
    return MeteorParticle(
        start = start,
        c1 = c1,
        c2 = c2,
        end = end,
        startNanos = now,
        durationNanos = duration,
        length = length,
        stroke = stroke,
        color = color
    )
}
