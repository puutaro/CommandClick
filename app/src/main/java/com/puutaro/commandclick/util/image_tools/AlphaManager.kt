package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.createBitmap
import kotlin.math.sin
import kotlin.math.sqrt

object AlphaManager {

    fun fadeBitmapLeftToRightToLow(
        bitmap: Bitmap,
        alphaIncline: Float,
        alphaOffset: Float,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels,0 , width, 0, 0, width, height)
        val transColor = Color.TRANSPARENT
        for (x in 0  until width) {
            for (y in 0 until height) {
                val index = y * width + x
                val color = pixels[index]
                val curAlpha = Color.alpha(color)
//                    val alphaSrc = ((x.toFloat() / width) * 254).toInt()
                val alpha =
                    when(color == transColor) {
                        true -> 0
                        else -> (alphaIncline * x.toFloat() + (curAlpha + alphaOffset)).let {
                            if (it < 0f) return@let 0f
                            if (it > 254f) return@let 255f
                            it
                        }.toInt()
                    }
                if(alpha >= curAlpha) continue
                val curRed = Color.red(color)
                val curGreen = Color.green(color)
                val curBlue = Color.blue(color)

//                    if(height/ 1 == y) {
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lalpha.txt").absolutePath,
//                            listOf(
////                                "alphaSrc: ${alphaSrc}",
//                                "alpha: ${alpha}",
//                                "curAlpha: ${curAlpha}",
//                            ).joinToString("\n")
//                        )
//                    }
                pixels[index] = Color.argb(
//                        254,
                    alpha,
//                        curAlpha,
                    curRed, curGreen, curBlue)
            }
        }
        val fadedBitmap = createBitmap(width, height)
        fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return fadedBitmap
    }

    fun fadeBitmapLeftToRight(
        bitmap: Bitmap,
        alphaIncline: Float,
        alphaOffset: Float,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels,0 , width, 0, 0, width, height)
        val transColor = Color.TRANSPARENT
        for (x in 0  until width) {
            for (y in 0 until height) {
                val index = y * width + x
                val color = pixels[index]
                val curAlpha = Color.alpha(color)
//                    val alphaSrc = ((x.toFloat() / width) * 254).toInt()
                val alpha =
                    when(color == transColor) {
                        true -> 0
                        else -> (alphaIncline * x.toFloat() + (curAlpha + alphaOffset)).let {
                            if (it < 0f) return@let 0f
                            if (it > 254f) return@let 255f
                            it
                        }.toInt()
                    }
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)

//                    if(height/ 1 == y) {
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lalpha.txt").absolutePath,
//                            listOf(
////                                "alphaSrc: ${alphaSrc}",
//                                "alpha: ${alpha}",
//                                "curAlpha: ${curAlpha}",
//                            ).joinToString("\n")
//                        )
//                    }
                pixels[index] = Color.argb(
//                        254,
                    alpha,
//                        curAlpha,
                    red, green, blue)
            }
        }
        val fadedBitmap = createBitmap(width, height)
        fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return fadedBitmap
    }
    fun overrideLeftToRight(
        bitmap: Bitmap,
        alphaIncline: Float,
        alphaOffset: Float,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val transColor = Color.TRANSPARENT
        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = y * width + x
                val color = pixels[index]
                val alphaSrc = ((x.toFloat() / width) * 254).toInt()
                val alpha =
                    when(color == transColor) {
                        true -> 0
                        else -> (alphaIncline * x.toFloat() + (alphaSrc + alphaOffset)).let {
                            if(it < 0f) return@let 0f
                            if(it > 254f) return@let 255f
                            it
                        }.toInt()
                    }
                (alphaIncline * x.toFloat() + (alphaSrc + alphaOffset)).let {
                    if(it < 0f) return@let 0f
                    if(it > 254f) return@let 255f
                    it
                }.toInt()
//                    val curAlpha = Color.alpha(color)
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)
                pixels[index] = Color.argb(
                    alpha,
//                        curAlpha,
                    red, green, blue)
            }
        }
        val fadedBitmap = createBitmap(width, height)
        fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return fadedBitmap
    }

    fun fadeBitmapFromCenter(
        bitmap: Bitmap,
        centerX: Int,
        centerY: Int,
        alphaIncline: Float,
        alphaOffset: Float,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val transColor = Color.TRANSPARENT
        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = y * width + x
                val color = pixels[index]
                val curAlpha = Color.alpha(color)
                val distance = sqrt(
                    ((x - centerX) * (x - centerX) +
                            (y - centerY) * (y - centerY)).toDouble()
                ).toFloat()
                val alpha =
                    when(color == transColor) {
                        true -> 0
                        else -> (alphaIncline * distance + (curAlpha + alphaOffset)).let {
                            if (it < 0f) return@let 0f
                            if (it > 254) return@let 255
                            it
                        }.toInt()
                    }
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)
                pixels[index] = Color.argb(alpha, red, green, blue)
            }
        }

        val fadedBitmap = createBitmap(width, height)
        fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return fadedBitmap
    }

    fun overrideFromCenter(
        bitmap: Bitmap,
        centerX: Int,
        centerY: Int,
        alphaIncline: Float,
        alphaOffset: Float,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val maxDistance = sqrt(
            (centerX * centerX + centerY * centerY).toDouble()
        ).toFloat()
        val transColor = Color.TRANSPARENT
        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = y * width + x
                val color = pixels[index]
                val distance = sqrt(
                    ((x - centerX) * (x - centerX) +
                            (y - centerY) * (y - centerY)).toDouble()
                ).toFloat()
                val alphaSrc = (distance / maxDistance * 254).toInt().coerceIn(0, 255)
                val alpha =
                    when(color == transColor) {
                        true ->0
                        else -> (alphaIncline * distance + (alphaSrc + alphaOffset)).let {
                            if (it < 0f) return@let 0f
                            if (it > 254) return@let 255
                            it

                        }.toInt()
                    }
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)
                pixels[index] = Color.argb(alpha, red, green, blue)
            }
        }

        val fadedBitmap = createBitmap(width, height)
        fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return fadedBitmap
    }

    /**
    * Bitmapに、中心から左右に広がる不規則な波線を描画し、alpha値を減衰させる関数。
    *
    * @param bitmap 描画先のBitmapオブジェクト。
    * @param centerX 波線の中心X座標。
    * @param centerY 波線の中心Y座標。
    * @param waveAmplitude 波線の振幅（波の高さ）。
    * @param waveLength 波の長さ。
    * @param alphaIncline alpha値の減衰率（0.0～1.0）。1.0で減衰なし。
     * @param alphaOffset alpha値のオフセット。
    * @param color 波線の色。
    */
    fun drawWavyLineWithAlphaDecayByHorizon(
        bitmap: Bitmap,
        centerX: Int,
        centerY: Int,
        waveAmplitude: Float,
        waveLength: Float,
        lengthDivider: Int,
        alphaIncline: Float,
        alphaOffset: Float,
    ): Bitmap {
        val copyBitmap = bitmap.copy(bitmap.config!!, true)
        val canvas = Canvas(copyBitmap)
        val paint = Paint()
        paint.style = Paint.Style.STROKE // 線を描画
        paint.strokeWidth = 3f // 線の太さ
        paint.isAntiAlias = true // アンチエイリアスを有効にする

        val width = bitmap.width
        val height = bitmap.height //未使用だが、将来的な拡張のために保持

        val path = Path()
        path.moveTo(centerX.toFloat(), centerY.toFloat()) // 開始点を中心に設定

        var x = centerX.toFloat()
        val endXLeft = 0f // 左端
        val endXRight = width.toFloat() // 右端
        var direction = -1 // 1: 右方向、-1: 左方向
        var segmentLength = (waveLength / lengthDivider).let {
            if (it > 0) return@let it
            1f
        } // 波の1/4ごとの点を打つ

        // 左右両方向に波線を描画
        val alphaOffsetInt = alphaOffset.toInt()
        while (x > endXLeft || x < endXRight) {

            // 減衰率を計算
            val distance = Math.abs(x - centerX)
            val alpha = (255 * Math.pow(
                alphaIncline.toDouble(),
                (distance / waveLength).toDouble()
            )).toInt() + alphaOffsetInt
            paint.alpha = alpha.coerceIn(0, 255) // alpha値を0～255の範囲に制限
            //Log.d("drawWavyLine", "x: $x, distance: $distance, alpha: $alpha") // デバッグログ

            // 次のX座標を計算
            val nextX = x + direction * segmentLength
            var y = centerY + waveAmplitude * sin(x / waveLength * 2 * Math.PI)

            // 終端を超えないようにする
            if (direction == -1 && nextX < endXLeft) {
                x = endXLeft;
                y = centerY + waveAmplitude * sin(x / waveLength * 2 * Math.PI)
                path.lineTo(x, y.toFloat())
                break;
            }
            if (direction == 1 && nextX > endXRight) {
                x = endXRight;
                y = centerY + waveAmplitude * sin(x / waveLength * 2 * Math.PI)
                path.lineTo(x, y.toFloat())
                break;
            }
            x = nextX
            path.lineTo(x, y.toFloat())

            // 反対方向の波を描画
            if (direction == -1) {
                direction = 1
                x = centerX.toFloat()
            } else {
                direction = -1
                x = centerX.toFloat()
            }
        }
        canvas.drawPath(path, paint)
        return copyBitmap
    }
}