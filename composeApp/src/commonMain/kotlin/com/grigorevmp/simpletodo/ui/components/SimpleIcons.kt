package com.grigorevmp.simpletodo.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object SimpleIcons {
    val ArrowUp: ImageVector = ImageVector.Builder(
        name = "ArrowUp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(7f, 14f)
            lineTo(12f, 9f)
            lineTo(17f, 14f)
            lineTo(15.5f, 15.5f)
            lineTo(12f, 12f)
            lineTo(8.5f, 15.5f)
            close()
        }
    }.build()

    val ArrowDown: ImageVector = ImageVector.Builder(
        name = "ArrowDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(7f, 10f)
            lineTo(12f, 15f)
            lineTo(17f, 10f)
            lineTo(15.5f, 8.5f)
            lineTo(12f, 12f)
            lineTo(8.5f, 8.5f)
            close()
        }
    }.build()

    val ArrowLeft: ImageVector = ImageVector.Builder(
        name = "ArrowLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(14f, 7f)
            lineTo(9f, 12f)
            lineTo(14f, 17f)
            lineTo(15.5f, 15.5f)
            lineTo(12f, 12f)
            lineTo(15.5f, 8.5f)
            close()
        }
    }.build()

    val ArrowRight: ImageVector = ImageVector.Builder(
        name = "ArrowRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(10f, 7f)
            lineTo(15f, 12f)
            lineTo(10f, 17f)
            lineTo(8.5f, 15.5f)
            lineTo(12f, 12f)
            lineTo(8.5f, 8.5f)
            close()
        }
    }.build()

    val Flame: ImageVector = ImageVector.Builder(
        name = "Flame",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(12f, 3f)
            lineTo(16f, 9f)
            lineTo(14.5f, 12.5f)
            lineTo(16f, 15f)
            lineTo(12f, 21f)
            lineTo(8f, 15f)
            lineTo(9.5f, 12.5f)
            lineTo(8f, 9f)
            close()
        }
    }.build()


    val Visibility: ImageVector = ImageVector.Builder(
        name = "Visibility",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(2.5f, 12f)
            curveTo(4.8f, 8.5f, 8f, 6.5f, 12f, 6.5f)
            curveTo(16f, 6.5f, 19.2f, 8.5f, 21.5f, 12f)
            curveTo(19.2f, 15.5f, 16f, 17.5f, 12f, 17.5f)
            curveTo(8f, 17.5f, 4.8f, 15.5f, 2.5f, 12f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(12f, 9.5f)
            curveTo(13.4f, 9.5f, 14.5f, 10.6f, 14.5f, 12f)
            curveTo(14.5f, 13.4f, 13.4f, 14.5f, 12f, 14.5f)
            curveTo(10.6f, 14.5f, 9.5f, 13.4f, 9.5f, 12f)
            curveTo(9.5f, 10.6f, 10.6f, 9.5f, 12f, 9.5f)
            close()
        }
    }.build()

    val VisibilityOff: ImageVector = ImageVector.Builder(
        name = "VisibilityOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(2.5f, 12f)
            curveTo(4.8f, 8.5f, 8f, 6.5f, 12f, 6.5f)
            curveTo(16f, 6.5f, 19.2f, 8.5f, 21.5f, 12f)
            curveTo(19.2f, 15.5f, 16f, 17.5f, 12f, 17.5f)
            curveTo(8f, 17.5f, 4.8f, 15.5f, 2.5f, 12f)
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(4f, 5f)
            lineTo(20f, 19f)
        }
    }.build()

    val Star: ImageVector = ImageVector.Builder(
        name = "Star",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(12f, 3.5f)
            lineTo(14.9f, 9f)
            lineTo(21f, 9.8f)
            lineTo(16.5f, 14f)
            lineTo(17.8f, 20f)
            lineTo(12f, 16.9f)
            lineTo(6.2f, 20f)
            lineTo(7.5f, 14f)
            lineTo(3f, 9.8f)
            lineTo(9.1f, 9f)
            close()
        }
    }.build()

    val Edit: ImageVector = ImageVector.Builder(
        name = "Edit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(4f, 17f)
            lineTo(4f, 20f)
            lineTo(7f, 20f)
            lineTo(16f, 11f)
            lineTo(13f, 8f)
            close()
            moveTo(17f, 10f)
            lineTo(19f, 8f)
            lineTo(16f, 5f)
            lineTo(14f, 7f)
            close()
        }
    }.build()


    val Save: ImageVector = ImageVector.Builder(
        name = "Save",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(5f, 4f)
            lineTo(17f, 4f)
            lineTo(20f, 7f)
            lineTo(20f, 20f)
            lineTo(5f, 20f)
            close()
            moveTo(7f, 6f)
            lineTo(15f, 6f)
            lineTo(15f, 10f)
            lineTo(7f, 10f)
            close()
        }
    }.build()

    val Delete: ImageVector = ImageVector.Builder(
        name = "Delete",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(7f, 7f)
            lineTo(17f, 7f)
            lineTo(16.2f, 20f)
            lineTo(7.8f, 20f)
            close()
            moveTo(9f, 4f)
            lineTo(15f, 4f)
            lineTo(16f, 6f)
            lineTo(8f, 6f)
            close()
            moveTo(5f, 6f)
            lineTo(19f, 6f)
            lineTo(19f, 8f)
            lineTo(5f, 8f)
            close()
        }
    }.build()

    val Exclamation: ImageVector = ImageVector.Builder(
        name = "Exclamation",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(11f, 5f)
            lineTo(13f, 5f)
            lineTo(13f, 14f)
            lineTo(11f, 14f)
            close()
            moveTo(11f, 16f)
            lineTo(13f, 16f)
            lineTo(13f, 18f)
            lineTo(11f, 18f)
            close()
        }
    }.build()

    val Beaker: ImageVector = ImageVector.Builder(
        name = "Beaker",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(9f, 4f)
            lineTo(15f, 4f)
            lineTo(15f, 6f)
            lineTo(14f, 6f)
            lineTo(14f, 10f)
            lineTo(18f, 18f)
            lineTo(6f, 18f)
            lineTo(10f, 10f)
            lineTo(10f, 6f)
            lineTo(9f, 6f)
            close()
            moveTo(8.2f, 16f)
            lineTo(15.8f, 16f)
            lineTo(14.8f, 14f)
            lineTo(9.2f, 14f)
            close()
        }
    }.build()

    val Check: ImageVector = ImageVector.Builder(
        name = "Check",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(6f, 12.5f)
            lineTo(10f, 16.5f)
            lineTo(18f, 8.5f)
            lineTo(16.5f, 7f)
            lineTo(10f, 13.5f)
            lineTo(7.5f, 11f)
            close()
        }
    }.build()

    val More: ImageVector = ImageVector.Builder(
        name = "More",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(6f, 12f)
            lineTo(8f, 12f)
            lineTo(8f, 14f)
            lineTo(6f, 14f)
            close()
            moveTo(11f, 12f)
            lineTo(13f, 12f)
            lineTo(13f, 14f)
            lineTo(11f, 14f)
            close()
            moveTo(16f, 12f)
            lineTo(18f, 12f)
            lineTo(18f, 14f)
            lineTo(16f, 14f)
            close()
        }
    }.build()

    val MoreVertical: ImageVector = ImageVector.Builder(
        name = "MoreVertical",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(11f, 6f)
            lineTo(13f, 6f)
            lineTo(13f, 8f)
            lineTo(11f, 8f)
            close()
            moveTo(11f, 11f)
            lineTo(13f, 11f)
            lineTo(13f, 13f)
            lineTo(11f, 13f)
            close()
            moveTo(11f, 16f)
            lineTo(13f, 16f)
            lineTo(13f, 18f)
            lineTo(11f, 18f)
            close()
        }
    }.build()

    val Drag: ImageVector = ImageVector.Builder(
        name = "Drag",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(7f, 6f); lineTo(9f, 6f); lineTo(9f, 8f); lineTo(7f, 8f); close()
            moveTo(7f, 11f); lineTo(9f, 11f); lineTo(9f, 13f); lineTo(7f, 13f); close()
            moveTo(7f, 16f); lineTo(9f, 16f); lineTo(9f, 18f); lineTo(7f, 18f); close()
            moveTo(14f, 6f); lineTo(16f, 6f); lineTo(16f, 8f); lineTo(14f, 8f); close()
            moveTo(14f, 11f); lineTo(16f, 11f); lineTo(16f, 13f); lineTo(14f, 13f); close()
            moveTo(14f, 16f); lineTo(16f, 16f); lineTo(16f, 18f); lineTo(14f, 18f); close()
        }
    }.build()
}
