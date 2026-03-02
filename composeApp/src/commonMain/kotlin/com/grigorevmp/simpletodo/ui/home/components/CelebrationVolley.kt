package com.grigorevmp.simpletodo.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.grigorevmp.simpletodo.ui.home.common.MeteorParticle
import com.grigorevmp.simpletodo.ui.home.common.cubicBezier
import com.grigorevmp.simpletodo.ui.home.common.cubicBezierTangent
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


@Composable
fun CelebrationVolley(
    trigger: Int,
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateListOf<VolleyParticle>() }
    val meteors = remember { mutableStateListOf<MeteorParticle>() }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    var nowNanos by remember { mutableStateOf(0L) }
    val rng = remember { Random(45677) }
    val palette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error
    )

    LaunchedEffect(trigger, fieldSize) {
        if (trigger == 0 || fieldSize.width == 0 || fieldSize.height == 0) return@LaunchedEffect
        val now = withFrameNanos { it }
        nowNanos = now
        repeat(18) {
            particles.add(createVolleyParticle(now, fieldSize, rng, palette))
        }
        repeat(8) {
            meteors.add(createBottomMeteor(now, fieldSize, rng, palette))
        }
    }

    LaunchedEffect(fieldSize) {
        if (fieldSize.width == 0 || fieldSize.height == 0) return@LaunchedEffect
        while (isActive) {
            val now = withFrameNanos { it }
            nowNanos = now
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                if (now - p.startNanos > p.durationNanos) {
                    iterator.remove()
                }
            }
            val meteorIterator = meteors.iterator()
            while (meteorIterator.hasNext()) {
                val p = meteorIterator.next()
                if (now - p.startNanos > p.durationNanos) {
                    meteorIterator.remove()
                }
            }
        }
    }

    Canvas(modifier = modifier.onSizeChanged { fieldSize = it }) {
        val now = nowNanos
        particles.forEach { p ->
            val t = ((now - p.startNanos).toFloat() / p.durationNanos.toFloat()).coerceIn(0f, 1f)
            drawVolleyParticle(p, t)
        }
        meteors.forEach { p ->
            val t = ((now - p.startNanos).toFloat() / p.durationNanos.toFloat()).coerceIn(0f, 1f)
            drawMeteorParticle(p, t)
        }
    }
}


private fun createBottomMeteor(
    now: Long,
    size: IntSize,
    rng: Random,
    palette: List<Color>
): MeteorParticle {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    val start = Offset(
        x = w * (0.1f + rng.nextFloat() * 0.8f),
        y = h + 18f
    )
    val end = Offset(
        x = w * (0.15f + rng.nextFloat() * 0.7f),
        y = h * (0.2f + rng.nextFloat() * 0.35f)
    )
    val c1 = Offset(
        x = start.x + (rng.nextFloat() - 0.5f) * w * 0.35f,
        y = start.y - h * (0.2f + rng.nextFloat() * 0.25f)
    )
    val c2 = Offset(
        x = end.x + (rng.nextFloat() - 0.5f) * w * 0.35f,
        y = end.y + h * (0.05f + rng.nextFloat() * 0.15f)
    )
    val duration = (700L + rng.nextInt(700)).toLong() * 1_000_000L
    val length = 12f + rng.nextFloat() * 10f
    val stroke = 1.6f + rng.nextFloat() * 1.0f
    return MeteorParticle(
        start = start,
        c1 = c1,
        c2 = c2,
        end = end,
        startNanos = now,
        durationNanos = duration,
        length = length,
        stroke = stroke,
        color = palette[rng.nextInt(palette.size)].copy(alpha = 0.65f)
    )
}

private fun DrawScope.drawMeteorParticle(p: MeteorParticle, t: Float) {
    val pos = cubicBezier(p.start, p.c1, p.c2, p.end, t)
    val dir = cubicBezierTangent(p.start, p.c1, p.c2, p.end, t)
    val tail = Offset(
        pos.x - dir.x * p.length,
        pos.y - dir.y * p.length
    )
    val alpha = (1f - t).coerceIn(0f, 1f) * 0.9f
    drawLine(
        color = p.color.copy(alpha = alpha),
        start = pos,
        end = tail,
        strokeWidth = p.stroke,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.cornerPathEffect(p.stroke)
    )
}

private data class VolleyParticle(
    val start: Offset,
    val velocity: Offset,
    val color: Color,
    val length: Float,
    val stroke: Float,
    val startNanos: Long,
    val durationNanos: Long,
    val kind: VolleyKind,
    val swayAmp: Float,
    val swayFreq: Float,
    val swayPhase: Float
)

private enum class VolleyKind { STREAK, DOT }

private fun createVolleyParticle(
    now: Long,
    size: IntSize,
    rng: Random,
    palette: List<Color>
): VolleyParticle {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    val start = Offset(
        x = w * (0.15f + rng.nextFloat() * 0.7f),
        y = h + 24f
    )
    val speed = 780f + rng.nextFloat() * 520f
    val angle = (-105f + rng.nextFloat() * 50f)
    val rad = angle * (PI / 180.0)
    val vx = cos(rad).toFloat() * speed
    val vy = sin(rad).toFloat() * speed
    val streak = rng.nextFloat() > 0.35f
    val kind = if (streak) VolleyKind.STREAK else VolleyKind.DOT
    val length = if (streak) 18f + rng.nextFloat() * 12f else 8f + rng.nextFloat() * 6f
    val stroke = if (streak) 2.2f + rng.nextFloat() * 1.2f else 3.0f + rng.nextFloat() * 1.4f
    val duration = (700L + rng.nextInt(600)).toLong() * 1_000_000L

    return VolleyParticle(
        start = start,
        velocity = Offset(vx, vy),
        color = palette[rng.nextInt(palette.size)],
        length = length,
        stroke = stroke,
        startNanos = now,
        durationNanos = duration,
        kind = kind,
        swayAmp = 6f + rng.nextFloat() * 10f,
        swayFreq = 6f + rng.nextFloat() * 8f,
        swayPhase = rng.nextFloat() * 6.28f
    )
}

private fun DrawScope.drawVolleyParticle(p: VolleyParticle, t: Float) {
    val elapsedSec = (t * p.durationNanos) / 1_000_000_000f
    val gravity = Offset(0f, 900f)
    val drift = p.velocity * elapsedSec
    val fall = gravity * (0.5f * elapsedSec * elapsedSec)
    val sway = sin(t * p.swayFreq + p.swayPhase) * p.swayAmp
    val pos = p.start + drift + fall + Offset(sway, 0f)
    val alpha = (1f - t).coerceIn(0f, 1f)
    val color = p.color.copy(alpha = alpha)

    when (p.kind) {
        VolleyKind.STREAK -> {
            val dir = p.velocity
            val len = sqrt(dir.x * dir.x + dir.y * dir.y).coerceAtLeast(0.001f)
            val nx = dir.x / len
            val ny = dir.y / len
            val tail = Offset(pos.x - nx * p.length, pos.y - ny * p.length)
            drawLine(
                color = color,
                start = pos,
                end = tail,
                strokeWidth = p.stroke,
                cap = StrokeCap.Round
            )
        }

        VolleyKind.DOT -> {
            drawCircle(
                color = color,
                radius = p.stroke * 1.2f,
                center = pos
            )
        }
    }
}