package com.freekeyboardballoutcode.ime.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Gboard-like shift glyphs (filled / outline / caps). */
object KeyboardIcons {
    val Shift: ImageVector by lazy {
        ImageVector.Builder(
            name = "Shift",
            defaultWidth = 28.dp,
            defaultHeight = 28.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero,
            ) {
                // Wide filled up-arrow (shift)
                moveTo(12f, 2f)
                lineTo(2.5f, 13.2f)
                horizontalLineTo(7.8f)
                verticalLineTo(22f)
                horizontalLineTo(16.2f)
                verticalLineTo(13.2f)
                horizontalLineTo(21.5f)
                close()
            }
        }.build()
    }

    val ShiftOutline: ImageVector by lazy {
        ImageVector.Builder(
            name = "ShiftOutline",
            defaultWidth = 28.dp,
            defaultHeight = 28.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 3f)
                lineTo(3.5f, 13f)
                horizontalLineTo(8.2f)
                verticalLineTo(21f)
                horizontalLineTo(15.8f)
                verticalLineTo(13f)
                horizontalLineTo(20.5f)
                close()
            }
        }.build()
    }

    val CapsLock: ImageVector by lazy {
        ImageVector.Builder(
            name = "CapsLock",
            defaultWidth = 28.dp,
            defaultHeight = 28.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 1.5f)
                lineTo(2.5f, 12.5f)
                horizontalLineTo(7.8f)
                verticalLineTo(17.8f)
                horizontalLineTo(16.2f)
                verticalLineTo(12.5f)
                horizontalLineTo(21.5f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(7f, 19.5f)
                horizontalLineTo(17f)
                verticalLineTo(22f)
                horizontalLineTo(7f)
                close()
            }
        }.build()
    }
}
