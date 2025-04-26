package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.drawable.VectorDrawable
import androidx.core.graphics.PathParser
import androidx.core.graphics.createBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.puutaro.commandclick.util.num.RateTool
import kotlin.math.atan2
import androidx.core.graphics.toColorInt
import androidx.core.graphics.get
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object LineArt {

    /**
     * Bitmapに直線状のダメージラインを特定の角度で描画する関数。
     *
     * @param bitmap 描画対象のBitmap
     * @param startX ラインの始点のX座標
     * @param startY ラインの始点のY座標
     * @param minLength ラインの長さ
     * @param minAngle ラインの角度（度数法）
     * @param colorList ラインの色
     * @param lineWidth ラインの太さ
     * @return ダメージラインが描画された新しいBitmap。元のBitmapは変更されない。
     */
    fun drawAngleyLine(
        bitmap: Bitmap,
        baseLength: Float,
        minLength: Float,
        maxLength: Float,
        minStrokeWidth: Float,
        maxStrokeWidth: Float,
        minOpacity: Float,
        maxOpacity: Float,
        minAngle: Int,
        maxAngle: Int,
        colorList: List<String>,
        times: Int,
    ): Bitmap {
        // 新しいBitmapを作成（元のBitmapを直接変更しない）
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // 可変のコピーを作成
        val bitmapWidth = mutableBitmap.width
        val bitmapHeight = mutableBitmap.height
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        val random = Random.Default
        for(i in 0..times) {
            paint.color = colorList.random().toColorInt()
            paint.strokeWidth = random.nextDouble(
                minStrokeWidth.toDouble(),
                maxStrokeWidth.toDouble()
            ).toFloat()
            paint.style = Paint.Style.STROKE // 線を描画
            paint.alpha = RateTool.randomByRate(
                255f,
                minOpacity,
                maxOpacity
            )
            // paint.isAntiAlias = true // アンチエイリアスを有効にする

            // 角度をラジアンに変換
            val angleRad = (minAngle..maxAngle).random() * PI / 180f

            // Pathを使って線を描画
            val path = Path()
            val startX = (0..bitmapWidth).random().toFloat()
            val startY = (0..bitmapHeight).random().toFloat()
            val length = RateTool.randomByRate(baseLength,minLength,maxLength)
            // 終点の座標を計算
            val endX = (startX + length * cos(angleRad)).toFloat()
            val endY = (startY + length * sin(angleRad)).toFloat()

            path.moveTo(startX, startY)
            path.lineTo(endX, endY)

            canvas.drawPath(path, paint)
      }

        return mutableBitmap
    }

    fun getPathsFromVectorDrawable(context: Context, drawableId: Int): List<String>? {
        val vectorDrawable =
            VectorDrawableCompat.create(context.resources, drawableId, context.theme)
                ?: return null
        val pathField = try {
            VectorDrawable::class.java.getDeclaredField("mVectorState")
        } catch (e: Exception){
            return null
        }
        pathField.isAccessible = true
        val vectorState = pathField.get(vectorDrawable)

        val pathsField = vectorState.javaClass.getDeclaredField("mVPathRenderer")
        pathsField.isAccessible = true
        val pathRenderer = pathsField.get(vectorState)

        val pathsListField = pathRenderer.javaClass.getDeclaredField("mPaths")
        pathsListField.isAccessible = true
        val pathsList = pathsListField.get(pathRenderer) as ArrayList<*>

        val pathDataList = pathsList.map { path ->
            val pathDataField = path.javaClass.getDeclaredField("mNodes")
            pathDataField.isAccessible = true
            val pathData = pathDataField.get(path) as Array<*>
            pathData.joinToString(" ") { it.toString() }
        }
        return pathDataList
    }

    fun convertStrToPath(pathDataStr: String): Path {
        val pathData = PathParser.createPathFromPathData(pathDataStr)
        return pathData

    }
    fun drawRandomLinesAroundPath(
        bitmap: Bitmap,
        path: Path,
        times: Int,
        colorList: List<String>,
        opacity: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        angleSrc: Float,
        minAngleShrinkRate: Float,
        maxAngleShrinkRate: Float,
        length: Int,
        minLengthShrinkRate: Float,
        maxLengthShrinkRate: Float,
    ): Bitmap {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // Bitmapをコピー
        val canvas = Canvas(resultBitmap)
        val pathWidth = GraphicPathTool.getPathWidth(
            path,
        )
        val pathHeight = GraphicPathTool.getPathHeight(
            path,
        )
        canvas.translate(
            (resultBitmap.width - pathWidth) / 2f,
            (resultBitmap.height - pathHeight ) / 2f,
        )
        val paint = Paint()
//        paint.color = Color.BLACK // 線の色
        paint.style = Paint.Style.STROKE // 線を描画

        val pathMeasure = PathMeasure(path, false) // パスの長さを計測
        val pathLength = pathMeasure.length

        val random = java.util.Random()
        for (i in 0 until times) {
            paint.alpha = RateTool.randomByRate(
                opacity,
                minOpacityRate,
                maxOpacityRate
            )
            paint.color = colorList.random().toColorInt()
            val distance = random.nextFloat() * pathLength // パス上のランダムな位置
            val pos = FloatArray(2)
            val tan = FloatArray(2)
            pathMeasure.getPosTan(distance, pos, tan) // パス上の座標と接線ベクトルを取得

            val angle = RateTool.randomByRate(
                angleSrc,
                minAngleShrinkRate,
                maxAngleShrinkRate
            )
            val lineLength = RateTool.randomByRate(
                length.toFloat(),
                minLengthShrinkRate,
                maxLengthShrinkRate
            )

            val endX = pos[0] + lineLength * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = pos[1] + lineLength * Math.sin(Math.toRadians(angle.toDouble())).toFloat()

            canvas.drawLine(pos[0], pos[1], endX, endY, paint)
        }

        return resultBitmap
    }

    fun drawTangents(
        bitmap: Bitmap,
        width: Int,
        height: Int,
        times: Int,
        colorList: List<String>,
        strokeWidth: Float,
        minStrokeWidthRate: Float,
        maxStrokeWidthRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        angleSrc: Float,
        minAngleShrinkRate: Float,
        maxAngleShrinkRate: Float,
        length: Int,
        minLengthShrinkRate: Float,
        maxLengthShrinkRate: Float,
    ): Bitmap {
        val srcWidth = bitmap.width
        val srcHeight = bitmap.height

        val boundaries = arrayListOf<Pair<Int, Int>>()

        for (x in 1 until srcWidth - 1) {
            for (y in 1 until srcHeight - 1) {
                val color = bitmap[x, y]
                if (color == Color.BLACK) {
                    // 周囲のピクセルをチェックし、境界を検出
                    if (bitmap[x - 1, y] != Color.BLACK ||
                        bitmap[x + 1, y] != Color.BLACK ||
                        bitmap[x, y - 1] != Color.BLACK ||
                        bitmap[x, y + 1] != Color.BLACK
                    ) {
                        boundaries.add(Pair(x, y))
                    }
                }
            }
        }

        val paint = Paint()
        val resultBitmap = BitmapTool.ImageTransformer.makeRect(
            "#00000000",
            width,
            height,
        )
        val canvas = Canvas(resultBitmap)
        canvas.translate(
            (width - srcWidth) / 2f,
            (height - srcHeight ) / 2f,
        )
//        for (i in 0..times){
        val selectBoundaries = when(times <= 0){
            true -> boundaries.toTypedArray()
            else -> {
                (0..times).map {
                    boundaries.random()
                }.toTypedArray()
            }
        }
        for ((x, y) in selectBoundaries) {
//            val boundary = boundaries.random()
//            val x = boundary.first
//            val y = boundary.second
            paint.color = colorList.random().toColorInt()
            paint.strokeWidth = RateTool.randomByRate(
                strokeWidth,
                minStrokeWidthRate,
                maxStrokeWidthRate
            ).toFloat()
            paint.alpha = RateTool.randomByRate(
                255f,
                minOpacityRate,
                maxOpacityRate
            )
            // 接線ベクトルを計算（簡略版）
            val lineLength = RateTool.randomByRate(
                length.toFloat(),
                minLengthShrinkRate,
                maxLengthShrinkRate
            )
            val angle = RateTool.randomByRate(
                angleSrc,
                minAngleShrinkRate,
                maxAngleShrinkRate
            )
            val dx =
                if (x > 0) bitmap[x - 1, y] - bitmap[x + 1, y]
                else 0
            val dy =
                if (y > 0) bitmap[x, y - 1] - bitmap[x, y + 1]
                else 0
            val thita = (atan2(dx.toDouble(), dy.toDouble())) + Math.toRadians(angle.toDouble())
            val endX = x + lineLength * Math.cos(thita).toFloat()
            val endY = y + lineLength * Math.sin(thita).toFloat()


            // 接線を描画
            canvas.drawLine(
                x.toFloat(),
                y.toFloat(),
                endX,
                endY,
//                (x + dx).toFloat(),
//                (y + dy).toFloat(),
                paint
            )
        }

        return resultBitmap
    }

    fun drawCrackOnBitmap(
        width: Int,
        height: Int,
        minStrokeWidthFloat: Float,
        maxStrokeWidthRate: Float,
        minSeg: Int,
        maxSeg: Int,
        minDurationRate: Float,
        maxDurationRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        colorList: List<String>,
        times: Int,
        ): Bitmap {
        val combinedBitmap = createBitmap(width, height)
        val canvas = Canvas(combinedBitmap)
        val paint = Paint().apply {
//            color = Color.BLACK
            style = Paint.Style.STROKE
//            strokeWidth = strokeWidthFloat // 亀裂の太さ
        }
        val random = kotlin.random.Random.Default
        val minWidthDuration= (width * minDurationRate).toInt()
        val maxWidthDuration= (width * maxDurationRate).toInt()
        val minHeightDuration= (height * minDurationRate).toInt()
        val maxHeightDuration= (height * maxDurationRate).toInt()
        val minStrokeWidthDouble = minStrokeWidthFloat.toDouble()
        val maxStrokeWidthDouble = maxStrokeWidthRate.toDouble()
        for(i in 0..times) {
            // 亀裂の始点をランダムに設定
            val startX = (0 until width).random().toFloat()
            val startY = (0 until height).random().toFloat()
            val path = Path()
            path.moveTo(startX, startY)

            // 亀裂の形状をlineToでランダムに描画
            var currentX = startX
            var currentY = startY
            val numSegments = (minSeg..maxSeg).random() // 亀裂のセグメント数
            (0 until numSegments).forEach { segIndex ->
                val nextX = (
                        currentX.toInt() - minWidthDuration
                                ..
                                currentX.toInt() + maxWidthDuration
                        ).random()
                    .coerceIn(0, width).toFloat() // 範囲内に制限
                val nextY = (
                        currentY.toInt() - minHeightDuration
                                ..
                                currentY.toInt() + maxHeightDuration
                        ).random()
                    .coerceIn(0, height).toFloat() // 範囲内に制限
                path.lineTo(nextX, nextY)
                currentX = nextX
                currentY = nextY
            }
            paint.alpha = (
                    (minOpacityRate * 255).toInt()
                            ..
                            (maxOpacityRate * 255).toInt()
                    ).random()
            paint.strokeWidth =
                when(minStrokeWidthDouble == maxStrokeWidthDouble) {
                    true -> maxStrokeWidthRate
                    else -> random.nextDouble(
                        minStrokeWidthDouble,
                        maxStrokeWidthDouble,
                    ).toFloat()
                }
            // 亀裂を描画
            paint.color = colorList.random().toColorInt()
            canvas.drawPath(path, paint)
        }
        return combinedBitmap
    }

    fun drawCrackFromCenter(
        width: Int,
        height: Int,
        centerX: Float,
        centerY: Float,
        minRadius: Int,
        maxRadius: Int,
        minSeg: Int,
        maxSeg: Int,
        minStrokeWidthFloat: Float,
        maxStrokeWidthFloat: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        radAngle: Int,
        colorList: List<String>,
        times: Int,
    ): Bitmap {
        val resultBitmap = createBitmap(width, height)
        val canvas = Canvas(resultBitmap)
        val paint = Paint().apply {
//            color = Color.BLACK
            style = Paint.Style.STROKE
//            strokeWidth = strokeWidthFloat // 亀裂の太さ
        }
        val random = kotlin.random.Random.Default
        val minStrokeWidthDouble = minStrokeWidthFloat.toDouble()
        val maxStrokeWidthDouble = maxStrokeWidthFloat.toDouble()
        for (i in 0 until times) {
            val path = Path()
            path.moveTo(centerX, centerY)

            // 亀裂の形状をランダムに描画
            val numSegments = (minSeg..maxSeg).random() // 亀裂のセグメント数
            var currentX = centerX
            var currentY = centerY
            for (j in 0 until numSegments) {
                val curXDiff = currentX - centerX
                val curYDiff = currentY - centerY
                val vectorAngle = when(
                    j == 0
                ) {
                    true -> (0..360).random()
                    else -> atan2(
                        curYDiff,
                        curXDiff,
                    )
                }.let {
                    Math.toDegrees(it.toDouble())
                }
                val angle = vectorAngle + when(
                    j == 0
                ) {
                    true -> (0..360)
                    else -> (-radAngle..radAngle)
                }.random().toFloat()
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "langle.txt").absolutePath,
//                    listOf(
//                        "curXDiff: ${curXDiff}",
//                        "curYDiff: ${curYDiff}",
//                        "vectorAngle: ${vectorAngle}",
//                        "angle: ${angle}",
//                    ).joinToString("\n") + "\n\n=====\n\n"
//                )
                val radius = (
                        minRadius..maxRadius
                        ).random().toFloat()
                val nextX = currentX + radius * cos(Math.toRadians(angle)).toFloat()
                val nextY = currentY + radius * sin(Math.toRadians(angle)).toFloat()
                path.lineTo(nextX, nextY)
                currentX = nextX
                currentY = nextY
            }
            // 亀裂を描画
            paint.alpha =
                (
                        (minOpacityRate * 255).toInt()
                                ..
                                (maxOpacityRate * 255).toInt()
                        ).random()
            paint.strokeWidth =
                when(minStrokeWidthDouble == maxStrokeWidthDouble) {
                    true -> maxStrokeWidthFloat
                    else -> random.nextDouble(
                        minStrokeWidthDouble,
                        maxStrokeWidthDouble,
                    ).toFloat()
                }
            paint.color = colorList.random().toColorInt()
            canvas.drawPath(path, paint)
        }
        return resultBitmap
    }

    fun drawCracksAwayFromCenter(bitmap: Bitmap, centerX: Int, centerY: Int) {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeWidth = 2f

        val numCracks = 20 // ひび割れの数
        val crackLength = 100f // ひび割れの長さ
        val crackVariation = 0.2f // ひび割れの長さのばらつき

        // 基準角度を計算
        val baseAngles = floatArrayOf(
            Math.toDegrees(atan2(-centerY.toDouble(), -centerX.toDouble())).toFloat(), // 左上
            Math.toDegrees(atan2(-centerY.toDouble(), (bitmap.width - centerX).toDouble())).toFloat(), // 右上
            Math.toDegrees(atan2((bitmap.height - centerY).toDouble(), (bitmap.width - centerX).toDouble())).toFloat(), // 右下
            Math.toDegrees(atan2((bitmap.height - centerY).toDouble(), -centerX.toDouble())).toFloat() // 左下
        )
        val random = kotlin.random.Random.Default
        for (i in 0 until numCracks) {
            // ランダムな基準角度を選択
            val baseAngle = baseAngles[random.nextInt(baseAngles.size)]

            // 基準角度を中心に、プラスマイナス90度の範囲でランダムな角度を生成
            val angle = baseAngle + random.nextFloat() * 180f - 90f

            val length = crackLength * (1f + random.nextFloat() * crackVariation - crackVariation / 2f)
            val endX = centerX + length * cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = centerY + length * sin(Math.toRadians(angle.toDouble())).toFloat()

            // 中心点から離れる方向にひび割れを描画
            canvas.drawLine(centerX.toFloat(), centerY.toFloat(), endX, endY, paint)

            // ひび割れに少し曲がりを加える
            val midX = (centerX + endX) / 2f
            val midY = (centerY + endY) / 2f
            val controlX = midX + random.nextFloat() * 20f - 10f
            val controlY = midY + random.nextFloat() * 20f - 10f

            canvas.drawLine(centerX.toFloat(), centerY.toFloat(), controlX, controlY, paint)
            canvas.drawLine(controlX, controlY, endX, endY, paint)
        }
    }

    fun drawCrack(bitmap: Bitmap, centerX: Int, centerY: Int) {
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeWidth = 2f

        val numCracks = 20 // ひび割れの数
        val crackLength = 100f // ひび割れの長さ
        val crackVariation = 0.2f // ひび割れの長さのばらつき
        val random = kotlin.random.Random.Default
        for (i in 0 until numCracks) {
            val angle = random.nextFloat() * 360f
            val length = crackLength * (1f + random.nextFloat() * crackVariation - crackVariation / 2f)
            val endX = centerX + length * cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = centerY + length * sin(Math.toRadians(angle.toDouble())).toFloat()

            canvas.drawLine(centerX.toFloat(), centerY.toFloat(), endX, endY, paint)

            // ひび割れに少し曲がりを加える
            val midX = (centerX + endX) / 2f
            val midY = (centerY + endY) / 2f
            val controlX = midX + random.nextFloat() * 20f - 10f
            val controlY = midY + random.nextFloat() * 20f - 10f

            canvas.drawLine(centerX.toFloat(), centerY.toFloat(), controlX, controlY, paint)
            canvas.drawLine(controlX, controlY, endX, endY, paint)
        }
    }
}