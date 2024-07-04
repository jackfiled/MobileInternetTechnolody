package top.rrricardo.musicplayer.ui.theme

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val roundedShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val p1 = Path().apply {
            addOval(Rect(4f, 3f, size.width - 1, size.height - 1))
        }
        val thickness = size.height / 2.10f
        val p2 = Path().apply {
            addOval(
                Rect(
                    thickness,
                    thickness,
                    size.width - thickness,
                    size.height - thickness
                )
            )
        }
        val p3 = Path()
        p3.op(p1, p2, PathOperation.Difference)

        return Outline.Generic(p3)
    }
}