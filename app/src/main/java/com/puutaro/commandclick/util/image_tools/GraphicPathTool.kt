package com.puutaro.commandclick.util.image_tools

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF

object GraphicPathTool {
//    fun scalePath(path: Path, scaleX: Float, scaleY: Float): Path {
//        val matrix = Matrix()
//        matrix.setScale(scaleX, scaleY) // scaleXはX軸方向の拡大率、scaleYはY軸方向の拡大率
//        path.transform(matrix)
//        return path
//    }

    fun scalePath(
        path: Path,
        centerXSrc: Float?,
        centerYSrc: Float?,
        scaleX: Float,
        scaleY: Float,
    ): Path {
        val bounds = RectF()
        path.computeBounds(bounds, true) // Pathのバウンディングボックスを計算
        val centerX =
            centerXSrc
                ?: bounds.centerX()
        val centerY =
            centerYSrc
                ?: bounds.centerY()

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, centerX, centerY) // centerX, centerYを中心として拡大・縮小
        path.transform(matrix)
        return path
    }

    fun getPathWidth(path: Path): Float {
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE

        val pos = FloatArray(2)
        for (distance in 0..pathLength.toInt()) {
            pathMeasure.getPosTan(distance.toFloat(), pos, null)
            val x = pos[0]
            minX = Math.min(minX, x)
            maxX = Math.max(maxX, x)
        }

        return maxX - minX
    }

    fun getPathHeight(path: Path): Float {
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        val pos = FloatArray(2)
        for (distance in 0..pathLength.toInt()) {
            pathMeasure.getPosTan(distance.toFloat(), pos, null)
            val y = pos[1]
            minY = Math.min(minY, y)
            maxY = Math.max(maxY, y)
        }

        return maxY - minY
    }
}