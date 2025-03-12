package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.createBitmap
import kotlin.random.Random
import androidx.core.graphics.toColorInt
import com.puutaro.commandclick.util.list.SeqTool

object SpecialArt {
    fun drawInkSplash(
        width: Int,
        height: Int,
        radiusRate: Float,
        times: Int,
        minPointNum: Int,
        maxPointNum: Int,
        colorList: List<String>,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        minAngle: Float,
        maxAngle: Float
    ): Bitmap {
        val resultBitmap = createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        val random = Random.Default
        val centerX = width / 2f
        val centerY = height / 2f
        val maxRadius = minOf(width, height) * radiusRate

//        val numSplashes = 10 // 墨汁の飛沫の数
        val baseOpacity = 255
        val angleSeed = 1000
        for (i in 0 until times) {
            val radius = random.nextFloat() * maxRadius
            val angle = random.nextInt(
                (minAngle * angleSeed).toInt(),
                (maxAngle * angleSeed).toInt(),
            )
            val x = centerX + radius * kotlin.math.cos(
                Math.toRadians(angle.toDouble())
            ).toFloat()
            val y = centerY + radius * kotlin.math.sin(
                Math.toRadians(angle.toDouble())
            ).toFloat()

            // ランダムな形状のパスを作成
            val path = Path()
            path.moveTo(x, y)
            val numPoints = random.nextInt(minPointNum, maxPointNum) // ランダムな点の数
            for (j in 0 until numPoints) {
                val offsetX = random.nextFloat() * radius / 2f - radius / 4f
                val offsetY = random.nextFloat() * radius / 2f - radius / 4f
                path.lineTo(x + offsetX, y + offsetY)
            }
            path.close()
            val opacitySrc = random.nextInt(
                (baseOpacity*minOpacityRate).toInt(),
                (baseOpacity*maxOpacityRate).toInt(),
            )
            val opacity =
                (opacityIncline * (x/width) + (opacitySrc + opacityOffset)).let {
                    if (it < 0) return@let 0
                    if (it > 255) return@let 255
                    it
                }.toInt()
            // 透明度をランダムに設定
            paint.color = colorList.random().toColorInt()
            paint.alpha = opacity

            canvas.drawPath(path, paint)
        }
        return resultBitmap
    }

    fun drawInkSplashOnColor(
        srcBitmap: Bitmap,
        targetColorStr: String,
        radiusRate: Float,
        times: Int,
        minPointNum: Int,
        maxPointNum: Int,
        colorList: List<String>,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
    ): Bitmap {
        val width = srcBitmap.width
        val height = srcBitmap.height
        val resultBitmap = createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        paint.style = Paint.Style.FILL

        val random = Random.Default
        val maxRadius = minOf(width, height) * radiusRate

//        val numSplashes = 10 // 墨汁の飛沫の数
        val baseOpacity = 255
        val pixels = IntArray(width*height)
        val targetColor = targetColorStr.toColorInt()
        // get pixel array from source
        srcBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in 0 until times) {
            val radius = random.nextFloat() * maxRadius
            val rndPixelsSeq = pixels.asSequence().shuffled()
            val xY = getColorXyByRnd(
                pixels,
                targetColor,
                width,
                height,
            ) ?: continue
            val x = xY.first.toFloat()
            val y = xY.second.toFloat()

            // ランダムな形状のパスを作成
            val path = Path()
            path.moveTo(x, y)
            val numPoints = random.nextInt(minPointNum, maxPointNum) // ランダムな点の数
            for (j in 0 until numPoints) {
                val offsetX = random.nextFloat() * radius / 2f - radius / 4f
                val offsetY = random.nextFloat() * radius / 2f - radius / 4f
                path.lineTo(x + offsetX, y + offsetY)
            }
            path.close()
            val opacitySrc = random.nextInt(
                (baseOpacity*minOpacityRate).toInt(),
                (baseOpacity*maxOpacityRate).toInt(),
            )
            val opacity =
                (opacityIncline * (x/width) + (opacitySrc + opacityOffset)).let {
                    if (it < 0) return@let 0
                    if (it > 255) return@let 255
                    it
                }.toInt()
            // 透明度をランダムに設定
            paint.color = colorList.random().toColorInt()
            paint.alpha = opacity

            canvas.drawPath(path, paint)
        }
        return resultBitmap
    }

    private fun getColorXyByRnd(
        rndPixelsSeq: IntArray,
        targetColor: Int,
        width: Int,
        height: Int,
    ): Pair<Int, Int>? {
        for (y in (0 until height).shuffled()) {
            for (x in (0 until width).shuffled()) {
                val pixel = rndPixelsSeq[y * width + x]
                if (
                    pixel != targetColor
                ) continue
                return x to y
            }
        }
        return null
    }
}