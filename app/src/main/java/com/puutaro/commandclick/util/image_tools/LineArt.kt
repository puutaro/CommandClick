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
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.num.RateTool
import java.io.File
import java.util.Random
import kotlin.math.PI
import kotlin.math.atan2

object LineArt {

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
//
//    fun getPathFromVectorDrawable(context: Context, drawableId: Int): Path? {
//        val drawable =
//            VectorDrawableCompat.create(context.resources, drawableId, context.theme)
//                ?: return null
//
//        val pathData = drawable.pathData ?: return null  // Use safe call operator
//        return PathParser.createPathFromPathData(pathData)
//    }
//    fun getPathFromBitmap(bitmap: Bitmap): Path {
//        val mat = Mat()
//        Utils.bitmapToMat(bitmap, mat)
//
//        val grayMat = Mat()
//        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGBA2GRAY)
//
//        val edges = Mat()
//        Imgproc.Canny(grayMat, edges, 100.0, 200.0) // 輪郭検出
//
//        val contours = mutableListOf<org.opencv.core.MatOfPoint>()
//        Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
//
//        val path = Path()
//        for (contour in contours) {
//            val points = contour.toArray()
//            path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
//            for (i in 1 until points.size) {
//                path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
//            }
//            path.close()
//        }
//
//        return path
//    }

//    fun vectorResourceToPath(context: Context, resourceId: Int): Path {
//        val vectorDrawable = VectorDrawableCompat.create(context.resources, R.drawable.your_vector_drawable, null)
//
//
//        val path = Path()
//        if (pathData != null) {
//            path.set(pathData)
//        }
//
//        return path
//    }
//    fun createPathFromSVGRes(
//        context: Context,
//        pathData: String
//    ): Path {
//        val drawable = context.resources.getDrawable(drawableId, context.theme) as? VectorDrawable
//            ?: return null
//        val pathData = drawable ?: return null
//
//        return PathParser.createPathFromPathData(pathData)
////        val drawable = AnimatedVectorDrawableCompat.create(context, CmdClickIcons.GOOGLE.id)
////        val drawable = context.resources.getDrawable(CmdClickIcons.GOOGLE.id, context.theme) as? VectorDrawable
////        val vectorDrawable = drawable as? VectorDrawableCompat
////        val pathData = vectorDrawable?.pathData
////
////        return drawable.path
//    }

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

        val random = Random()
        for (i in 0 until times) {
            paint.alpha = RateTool.randomByRate(
                opacity,
                minOpacityRate,
                maxOpacityRate
            )
            paint.color = Color.parseColor(
                colorList.random()
            )
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
                val color = bitmap.getPixel(x, y)
                if (color == Color.BLACK) {
                    // 周囲のピクセルをチェックし、境界を検出
                    if (bitmap.getPixel(x - 1, y) != Color.BLACK ||
                        bitmap.getPixel(x + 1, y) != Color.BLACK ||
                        bitmap.getPixel(x, y - 1) != Color.BLACK ||
                        bitmap.getPixel(x, y + 1) != Color.BLACK
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
            paint.color = Color.parseColor(
                colorList.random()
            )
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
                if (x > 0) bitmap.getPixel(x - 1, y) - bitmap.getPixel(x + 1, y)
                else 0
            val dy =
                if (y > 0) bitmap.getPixel(x, y - 1) - bitmap.getPixel(x, y + 1)
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
}