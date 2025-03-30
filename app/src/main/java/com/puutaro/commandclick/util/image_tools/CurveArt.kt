package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import kotlin.random.Random
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt

object CurveArt {
    fun drawCurvedCrack(
        width: Int,
        height: Int,
        rndNum: Float,
        minCurveFactor: Float,
        maxCurveFactor: Float,
        minStrokeWidthFloat: Float,
        maxStrokeWidthFloat: Float,
        minSeg: Int,
        maxSeg: Int,
        minDurationRate: Float,
        maxDurationRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        colorList: List<String>,
        times: Int,
    ): Bitmap {
        // BitmapとCanvasの初期化
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT) // 背景を透明にする
        // Paintの初期化
        val paint = Paint()
        paint.style = Paint.Style.STROKE // 線を描画
        paint.strokeCap = Paint.Cap.ROUND  // 線の端を丸くする
        paint.isAntiAlias = true // アンチエイリアスを有効にする
        val random = Random.Default
        val minStrokeWidthDouble = minStrokeWidthFloat.toDouble()
        val maxStrokeWidthDouble = maxStrokeWidthFloat.toDouble()
        // 開始点の初期化

        for(j in 0..times) {
            var currentPoint = PointF(Random.nextFloat() * width, Random.nextFloat() * height)
            val path = Path()
            path.moveTo(currentPoint.x, currentPoint.y)

            // 最初の角度をランダムに設定
            var currentAngle = Random.nextFloat() * 360f

            // 制御点のリスト
            val controlPoints = mutableListOf<PointF>()
            controlPoints.add(currentPoint)

            // ひび割れの生成ループ
            val minWidthDuration = (width * minDurationRate).toInt()
            val maxWidthDuration = (width * maxDurationRate).toInt()
            val minHeightDuration = (height * minDurationRate).toInt()
            val maxHeightDuration = (height * maxDurationRate).toInt()
            val minCurveFactorDouble = minCurveFactor.toDouble()
            val maxCurveFactorDouble = maxCurveFactor.toDouble()
            val seg = (minSeg..maxSeg).random()
            for (i in 0..seg) {
                // 次の点の計算
                val nextPointX =
                    (
                            currentPoint.x.toInt() - minWidthDuration
                                    ..
                                    currentPoint.x.toInt() + maxWidthDuration
                            ).random()
                        .coerceIn(0, width).toFloat() // 範囲内に制限
//                currentPoint.x + segmentLength * cos(Math.toRadians(currentAngle.toDouble())).toFloat()
                val nextPointY =
                    (
                            currentPoint.y.toInt() - minHeightDuration
                                    ..
                                    currentPoint.y.toInt() + maxHeightDuration
                            ).random()
                        .coerceIn(0, height).toFloat() // 範囲内に制限
//                currentPoint.y + segmentLength * sin(Math.toRadians(currentAngle.toDouble())).toFloat()
                val nextPoint = PointF(nextPointX, nextPointY)
                controlPoints.add(nextPoint)

                // 画面外に出ないように点を調整
                if (
                    nextPointX < 0
                    || nextPointX > width
                    || nextPointY < 0
                    || nextPointY > height
                ) {
                    break // 画面外に出たら終了
                }

                // 次の角度をランダムに変化させる
                currentAngle += (Random.nextFloat() * 2 - 1) * rndNum

                currentPoint = nextPoint
            }

            // ベジェ曲線で滑らかなひび割れを描画
            for (i in 0 until controlPoints.size - 1) {
                val startPoint = controlPoints[i]
                val endPoint = controlPoints[i + 1]

                // 制御点の計算 (曲率を制御)
                val curveFactor = random.nextDouble(
                    minCurveFactorDouble,
                    maxCurveFactorDouble,
                ).toFloat()
                val controlX1 = startPoint.x + (endPoint.x - startPoint.x) / 3 * curveFactor
                val controlY1 = startPoint.y + (endPoint.y - startPoint.y) / 3 * curveFactor
                val controlX2 = endPoint.x - (endPoint.x - startPoint.x) / 3 * curveFactor
                val controlY2 = endPoint.y - (endPoint.y - startPoint.y) / 3 * curveFactor

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, endPoint.x, endPoint.y)
            }
            paint.alpha = (
                    (minOpacityRate * 255).toInt()
                            ..
                            (maxOpacityRate * 255).toInt()
                    ).random()
            paint.strokeWidth =
                when (minStrokeWidthDouble == maxStrokeWidthDouble) {
                    true -> maxStrokeWidthFloat
                    else -> random.nextDouble(
                        minStrokeWidthDouble,
                        maxStrokeWidthDouble,
                    ).toFloat()
                }
            paint.color = colorList.random().toColorInt()
            canvas.drawPath(path, paint)
        }
        return bitmap
    }

}