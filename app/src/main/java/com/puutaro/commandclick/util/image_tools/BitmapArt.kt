package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.puutaro.commandclick.util.num.RateTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

object BitmapArt {

    suspend fun byArc(
        width: Int,
        height: Int,
        peaceBitmapList: List<Bitmap>,
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Float,
        sweepAngle: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        sizeIncline: Float,
        sizeOffset: Float,
        colorList: List<String>,
        times: Int,
        bkColor: String,
    ): Bitmap {
        val angleStep = sweepAngle / (times + 1) // 配置角度のステップ
        val opacityStep = 255 / (times + 1)
        val xyToBitmapList = withContext(Dispatchers.IO) {
            val xyToBitmapListJob = (0 until times).map { index ->
                async {
                    val opacitySrc =
                        opacityStep * index
                    val peaceBitmap = peaceBitmapList.random()
                    val opacity =
                        (opacityIncline * index + (opacitySrc + opacityOffset)).let {
                            if (it < 0) return@let 0
                            if (it > 255) return@let 255
                            it
                        }.toInt()
                    val peaceWidth =
                        (sizeIncline * index + (peaceBitmap.width + sizeOffset)).let {
                            if (it <= 1f) return@let null
                            it
                        }?.toInt() ?: return@async null
                    val peaceHeight =
                        (sizeIncline * index + (peaceBitmap.height + sizeOffset)).let {
                            if (it <= 1f) return@let null
                            it
                        }?.toInt() ?: return@async null
                    val angle = startAngle + angleStep * index

                    // 配置座標を計算
                    val x = centerX + radius * cos(
                        Math.toRadians(angle.toDouble())
                    ).toFloat() - peaceBitmap.width / 2f
                    val y = centerY + radius * sin(
                        Math.toRadians(angle.toDouble())
                    ).toFloat() - peaceBitmap.height / 2f

                    val sizingPeaceBitmap =
                        peaceBitmap.scale(peaceWidth, peaceHeight)
                    val colorBitmap = let {
                        val colorStr = colorList.random()
                        if (
                            colorStr.toColorInt() == Color.BLACK
                        ) return@let sizingPeaceBitmap
                        BitmapTool.ImageTransformer.convertBlackToColor(
                            sizingPeaceBitmap,
                            colorList.random()
                        )
                    }.let {
                        BitmapTool.ImageTransformer.ajustOpacity(
                            it,
                            opacity
                        )
                    }
                    index to Pair(
                        Pair(
                            x,
                            y
                        ),
                        colorBitmap
                    )
                }
            }
            xyToBitmapListJob.awaitAll().filter {
                it != null
            }.sortedBy { it?.first }.map {
                it?.second
            }
        }
        val combinedBitmap = BitmapTool.ImageTransformer.makeRect(
            bkColor,
            width,
            height,
        )
        val canvas = Canvas(combinedBitmap)
        xyToBitmapList.forEach {
                xYToBitmap ->
            if(
                xYToBitmap == null
            ) return@forEach
            val xY = xYToBitmap.first
            val x = xY.first
            val y = xY.second
            // Bitmapを描画
            canvas.drawBitmap(
                xYToBitmap.second,
                x,
                y,
                null
            )
        }
        return combinedBitmap
    }

    suspend fun byLine(
        width: Int,
        height: Int,
        peaceBitmapList: List<Bitmap>,
        centerX: Float,
        centerY: Float,
        maxRadius: Float,
        angle: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        sizeIncline: Float,
        sizeOffset: Float,
        colorList: List<String>,
        times: Int,
        bkColor: String,
    ): Bitmap {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lByLine00.txt").absolutePath,
//            listOf(
//                "times: ${times}",
////                        "radiusStep: ${radiusStep}",
////                        "xyToBitmapList: ${xyToBitmapList.map {it?.first}}",
//            ).joinToString("\n")
//        )
//        val angleStep = sweepAngle / (times + 1) // 配置角度のステップ
        val radiusStep = maxRadius / (times + 1)
        val opacityStep = 255 / (times + 1)
        val xyToBitmapList = withContext(Dispatchers.IO) {
            val xyToBitmapListJob = (0 until times).map { index ->
                async {
                    val opacitySrc =
                        opacityStep * index
                    val peaceBitmap = peaceBitmapList.random()
                    val opacity =
                        (opacityIncline * index + (opacitySrc + opacityOffset)).let {
                            if (it < 0) return@let 0
                            if (it > 255) return@let 255
                            it
                        }.toInt()
                    val peaceWidth =
                        (sizeIncline * index + (peaceBitmap.width + sizeOffset)).let {
                            if (it <= 1f) return@let null
                            it
                        }?.toInt() ?: return@async null
                    val peaceHeight =
                        (sizeIncline * index + (peaceBitmap.height + sizeOffset)).let {
                            if (it <= 1f) return@let null
                            it
                        }?.toInt() ?: return@async null
//                    val angle = startAngle + angleStep * index
                    val radius = radiusStep * index
                    // 配置座標を計算
                    val x = centerX + radius * cos(
                        Math.toRadians(angle.toDouble())
                    ).toFloat() - peaceBitmap.width / 2f
                    val y = centerY + radius * sin(
                        Math.toRadians(angle.toDouble())
                    ).toFloat() - peaceBitmap.height / 2f

                    val sizingPeaceBitmap =
                        peaceBitmap.scale(peaceWidth, peaceHeight)
                    val colorBitmap = let {
                        val colorStr = colorList.random()
                        if (
                            colorStr.toColorInt() == Color.BLACK
                        ) return@let sizingPeaceBitmap
                        BitmapTool.ImageTransformer.convertBlackToColor(
                            sizingPeaceBitmap,
                            colorList.random()
                        )
                    }.let {
                        BitmapTool.ImageTransformer.ajustOpacity(
                            it,
                            opacity
                        )
                    }
                    index to Pair(
                        Pair(
                            x,
                            y
                        ),
                        colorBitmap
                    )
                }
            }
            xyToBitmapListJob.awaitAll().filter {
                it != null
            }.sortedBy { it?.first }.map {
                it?.second
            }
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lByLine.txt").absolutePath,
//            listOf(
//                "times: ${times}",
//                "radiusStep: ${radiusStep}",
//                "xyToBitmapList: ${xyToBitmapList.map {it?.first}}",
//            ).joinToString("\n")
//        )
        val combinedBitmap = BitmapTool.ImageTransformer.makeRect(
            bkColor,
            width,
            height,
        )
        val canvas = Canvas(combinedBitmap)
        xyToBitmapList.forEach {
                xYToBitmap ->
            if(
                xYToBitmap == null
            ) return@forEach
            val xY = xYToBitmap.first
            val x = xY.first
            val y = xY.second
            // Bitmapを描画
            canvas.drawBitmap(
                xYToBitmap.second,
                x,
                y,
                null
            )
        }
        return combinedBitmap
    }

    suspend fun rectPuzzle(
        srcBitmap: Bitmap,
        shakeRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        colorList: List<String>,
        passionColorList: List<String>,
        passionInt: Int,
        times: Int,
        isOverlay: Boolean,
    ): Bitmap {
        val baseWidth = srcBitmap.width
        val baseHeight = srcBitmap.height
        val cutWidth = (baseWidth * shakeRate).toInt()
        val cutHeight = (baseHeight * shakeRate).toInt()

        val xYPairToBitmapList = withContext(Dispatchers.IO) {
            val xYPairToBitmapListJob = (0..times).map {
                async {
                    val offsetX = try {
                        (1..baseWidth - cutWidth).random()
                    } catch (e: Exception) {
                        0
                    }
                    val offsetY = try {
                        (1..baseHeight - cutHeight).random()
                    } catch (e: Exception) {
                        0
                    }
                    val cutBitmap = when(
                        (0..passionInt).random() == 1
                                && passionColorList.isNotEmpty()
                    ) {
                        true -> BitmapTool.ImageTransformer.makeRect(
                            "#000000",
                            cutWidth,
                            cutHeight,
                        ).let {
                            val colorStr = passionColorList.random()
                            if(
                                colorStr.toColorInt() == Color.BLACK
                            ) return@let it
                            BitmapTool.ImageTransformer.convertBlackToColor(
                                it,
                                colorList.random(),
                            )
                        }
                        else -> BitmapTool.ImageTransformer.cutByTarget(
                            srcBitmap,
                            cutWidth,
                            cutHeight,
                            offsetX,
                            offsetY,
                        ).let {
                            val colorStr = colorList.random()
                            if(
                                colorStr.toColorInt() == Color.BLACK
                            ) return@let it
                            BitmapTool.ImageTransformer.convertBlackToColor(
                                it,
                                colorList.random(),
                            )
                        }
                    }.let remake@ {
                        val opacity = RateTool.randomByRate(
                            254f,
                            minOpacityRate,
                            maxOpacityRate,
                        ).let {
                            opacitySrc ->
//                            val weight = ((opacitySrc * offsetX) / baseWidth) * opacityBandNum
                            opacityIncline * offsetX + (opacitySrc + opacityOffset)
//                            val absCulcOpacity =
//                                ((opacitySrc * offsetX) / baseWidth)
//                            when(opacityBandNum > 0) {
//                                false -> opacitySrc - absCulcOpacity
//                                else -> absCulcOpacity
//                            }
//                            val opacityDiff = opacitySrc / opacityBandNum
//                            val absOpacityBandNum = abs(opacityBandNum)
//                            val bandWidth = (baseWidth / absOpacityBandNum)
//                            val rank = let makeRank@ {
//                                val randSrc = offsetX / bandWidth
//                                if(randSrc > 1) return@makeRank randSrc
//                                1
//                            }
//                            val curOpacityDiff = opacityDiff * (rank - 1)
//                            val supOpacity = (opacitySrc - curOpacityDiff).let makeSupOpacity@ {
//                                if (it <= 0) return@makeSupOpacity 10
//                                it
//                            }
//                            val lowerOpacity = (supOpacity - curOpacityDiff).let makeLowerOpacity@ {
//                                if (it <= 0) return@makeLowerOpacity 0
//                                it
//                            }
//                            val culcOpacity =
//                                (lowerOpacity..supOpacity).random()
//                            when(opacityBandNum > 0) {
//                                false -> opacitySrc - culcOpacity
//                                else -> culcOpacity
//                            }
//                            val rankRate = when(opacityBandNum >= 1) {
//                                false -> (absOpacityBandNum - rank) / absOpacityBandNum
//                                else -> rank / absOpacityBandNum
//                            }
                        }.toInt().let {
                            if(it <= 0) return@let 5
                            it
                        }
                        BitmapTool.ImageTransformer.ajustOpacity(
                            it,
                            opacity
                        )
                    }
                    Pair(
                        offsetX.toFloat(),
                        offsetY.toFloat()
                    ) to cutBitmap
                }
            }
            xYPairToBitmapListJob.awaitAll()
        }
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap00.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(srcBitmap)
//        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap11.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(xYPairToBitmapList[0].second)
//        )
        var resultBitmap: Bitmap = when(isOverlay) {
            true -> srcBitmap.copy(
                Bitmap.Config.ARGB_8888,
                true
            )
            else -> BitmapTool.ImageTransformer.makeRect(
                "#00000000",
                baseWidth,
                baseHeight,
            )
        }
        xYPairToBitmapList.forEach {
            (xyPair, cutBitmap) ->
            resultBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                resultBitmap,
                cutBitmap,
                xyPair.first,
                xyPair.second
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap.txt").absolutePath,
//            listOf(
//                xYPairToBitmapList.map{it.first}.joinToString("--"),
//                "shakeRate: ${shakeRate}",
//                "minOpacityRate: ${minOpacityRate}",
//                "maxOpacityRate: ${maxOpacityRate}",
//                "colorList: ${colorList}",
//                "opacityBandNum: ${opacityIncline}",
//                "times: ${times}"
//            ).joinToString("\n\n")
//        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap22.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(resultBitmap)
//        )
        return resultBitmap
    }

    enum class ShakeDirection {
        VERTICAL,
        HORIZON,
        RND,
    }

    suspend fun shake(
        targetBitmap: Bitmap,
        zoomRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        colorList: List<String>,
        times: Int,
        direction: ShakeDirection,
    ): Bitmap {
        val targetWidth = targetBitmap.width
        val targetHeight = targetBitmap.height
        val width = (targetWidth * zoomRate).toInt()
        val height = (targetHeight * zoomRate).toInt()
        val xYPairToBitmapList = withContext(Dispatchers.IO) {
            val xYPairToBitmapListJob = (0..times).map {
                async {
                    val offsetX = try {
                        val widthDiff = (width - targetWidth)
                        when(
                            direction == ShakeDirection.VERTICAL
                        ) {
                            true -> widthDiff / 2
                            else -> (0..widthDiff).random()
                        }
                    } catch (e: Exception) {
                        0
                    }
                    val offsetY = try {
                        val heightDiff = (height - targetHeight)
                        when(
                            direction == ShakeDirection.HORIZON
                        ) {
                            true -> heightDiff / 2
                            else -> (0..heightDiff).random()
                        }
                    } catch (e: Exception) {
                        0
                    }
                    val changeColorBitmap = let {
                        val colorStr = colorList.random()
                        if(
                            colorStr.toColorInt() == Color.BLACK
                        ) return@let targetBitmap
                        BitmapTool.ImageTransformer.convertBlackToColor(
                            targetBitmap,
                            colorList.random(),
                        )
                    }
                    val opacity = RateTool.randomByRate(
                        254f,
                        minOpacityRate,
                        maxOpacityRate,
                    ).let {
                            opacitySrc ->
                        opacityIncline * offsetX + (opacitySrc + opacityOffset)
                    }.toInt().let {
                        if(it <= 0) return@let 5
                        it
                    }
                    val ajustOpacityBitmap = BitmapTool.ImageTransformer.ajustOpacity(
                        changeColorBitmap,
                        opacity
                    )
                    Pair(
                        offsetX.toFloat(),
                        offsetY.toFloat()
                    ) to ajustOpacityBitmap
                }
            }
            xYPairToBitmapListJob.awaitAll()
        }
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap00.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(srcBitmap)
//        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap11.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(xYPairToBitmapList[0].second)
//        )
        var resultBitmap = BitmapTool.ImageTransformer.makeRect(
            "#00000000",
            width,
            height,
        )
        xYPairToBitmapList.forEach {
                (xyPair, cutBitmap) ->
            resultBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                resultBitmap,
                cutBitmap,
                xyPair.first,
                xyPair.second
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap.txt").absolutePath,
//            listOf(
//                xYPairToBitmapList.map{it.first}.joinToString("--"),
//                "shakeRate: ${shakeRate}",
//                "minOpacityRate: ${minOpacityRate}",
//                "maxOpacityRate: ${maxOpacityRate}",
//                "colorList: ${colorList}",
//                "opacityBandNum: ${opacityIncline}",
//                "times: ${times}"
//            ).joinToString("\n\n")
//        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap22.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(resultBitmap)
//        )
        return resultBitmap
    }

    suspend fun rotate(
        targetBitmap: Bitmap,
        zoomRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        colorList: List<String>,
        minAngle: Float,
        maxAngle: Float,
        times: Int,
    ): Bitmap {
        val targetWidth = targetBitmap.width
        val targetHeight = targetBitmap.height
        val width = (targetWidth * zoomRate).toInt()
        val height = (targetHeight * zoomRate).toInt()
        val xYPairToBitmapList = withContext(Dispatchers.IO) {
            val xYPairToBitmapListJob = (0..times).map {
                async {
                    val offsetX = try {
                        (0..(width - targetWidth)).random()
                    } catch (e: Exception) {
                        0
                    }
                    val offsetY = try {
                        (0..(height - targetHeight)).random()
                    } catch (e: Exception) {
                        0
                    }
                    val changeColorBitmap = let {
                        val colorStr = colorList.random()
                        if(
                            Color.parseColor(colorStr) == Color.BLACK
                        ) return@let targetBitmap
                        BitmapTool.ImageTransformer.convertBlackToColor(
                            targetBitmap,
                            colorList.random(),
                        )
                    }
                    val opacity = RateTool.randomByRate(
                        254f,
                        minOpacityRate,
                        maxOpacityRate,
                    ).let {
                            opacitySrc ->
                        opacityIncline * offsetX + (opacitySrc + opacityOffset)
                    }.toInt().let {
                        if(it <= 0) return@let 5
                        it
                    }
                    val ajustOpacityBitmap = BitmapTool.ImageTransformer.ajustOpacity(
                        changeColorBitmap,
                        opacity
                    )
                    Pair(
                        offsetX.toFloat(),
                        offsetY.toFloat()
                    ) to ajustOpacityBitmap
                }
            }
            xYPairToBitmapListJob.awaitAll()
        }
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap00.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(srcBitmap)
//        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap11.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(xYPairToBitmapList[0].second)
//        )
        var resultBitmap = BitmapTool.ImageTransformer.makeRect(
            "#00000000",
            width,
            height,
        )
        xYPairToBitmapList.forEach {
                (xyPair, cutBitmap) ->
            resultBitmap = BitmapTool.ImageTransformer.overlayOnBkBitmapByPivot(
                resultBitmap,
                cutBitmap,
                xyPair.first,
                xyPair.second
            )
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap.txt").absolutePath,
//            listOf(
//                xYPairToBitmapList.map{it.first}.joinToString("--"),
//                "shakeRate: ${shakeRate}",
//                "minOpacityRate: ${minOpacityRate}",
//                "maxOpacityRate: ${maxOpacityRate}",
//                "colorList: ${colorList}",
//                "opacityBandNum: ${opacityIncline}",
//                "times: ${times}"
//            ).joinToString("\n\n")
//        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap22.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(resultBitmap)
//        )
        return resultBitmap
    }

    suspend fun createFanShapedBitmap(
        peaceBitmaps: Bitmap,
        times: Int,
        radius: Int,
        fanAngle: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        colorList: List<String>,
//        shakeRate: Float,
//        minOpacityRate: Float,
//        maxOpacityRate: Float,
    ): Bitmap? {

        // 回転後のBitmapの最大サイズを計算 (例として、すべてのBitmapが同じサイズと仮定)
        val maxWidth = peaceBitmaps.width
        val maxHeight = peaceBitmaps.height

        // 合成後のBitmapのサイズを計算 (扇形が収まるように調整)
        val combinedWidth = (radius * 2).toInt()
        val combinedHeight = (radius * 2).toInt()

        // 合成後のBitmapを作成
        val combinedBitmap = createBitmap(combinedWidth, combinedHeight)
        val canvas = Canvas(combinedBitmap)

        // 扇形の中心点
        val centerX = combinedWidth / 2f
        val centerY = combinedHeight / 2f

        val opacityStep = 255 / (times + 1)
        val angleStep = fanAngle / (times + 1) // 配置角度のステップ
        val bitmapList = withContext(Dispatchers.IO) {
            val jobList = (0..times).map { index ->
                async {
                    val opacitySrc =
                        opacityStep * index
                    val opacity =
                        (opacityIncline * index + (opacitySrc + opacityOffset)).let {
                            if(it < 0) return@let 0
                            if(it > 255) return@let 255
                            it
                        }.toInt()
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lshake${index}.txt").absolutePath,
//                        listOf(
//                            "index: ${index}",
//                            "opacityStep: ${opacityStep}",
//                            "opacityIncline: ${opacityIncline}",
//                            "opacityOffset: ${opacityOffset}",
//                            "opacity: ${opacity}",
//                        ).joinToString("\n")
//                    )
                    val colorBitmap = let {
                        val colorStr = colorList.random()
                        if(
                            colorStr.toColorInt() == Color.BLACK
                        ) return@let peaceBitmaps
                        BitmapTool.ImageTransformer.convertBlackToColor(
                            peaceBitmaps,
                            colorList.random()
                        )
                    }.let {
                        BitmapTool.ImageTransformer.ajustOpacity(
                            it,
                            opacity
                        )
                    }
                    index to Pair(
                        angleStep * index,
                        colorBitmap
                    )
                }
            }
            jobList.awaitAll().sortedBy { it.first }.map {
                it.second
            }
        }
        bitmapList.forEach {
             (angle, bitmap) ->

            // 配置座標を計算
            val x = centerX + radius * cos(Math.toRadians(angle.toDouble())).toFloat() - bitmap.width / 2f
            val y = centerY + radius * sin(Math.toRadians(angle.toDouble())).toFloat() - bitmap.height / 2f

            // Bitmapを描画
            canvas.drawBitmap(bitmap, x, y, null)
        }

        return combinedBitmap
    }


}