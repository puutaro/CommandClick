package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
import java.util.Random
import kotlin.math.abs
import kotlin.math.sqrt
import androidx.core.graphics.withClip

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
                        ColorTool.convertBlackToColor(
                            sizingPeaceBitmap,
                            colorList.random()
                        )
                    }.let {
                        ColorTool.ajustOpacity(
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
                        ColorTool.convertBlackToColor(
                            sizingPeaceBitmap,
                            colorList.random()
                        )
                    }.let {
                        ColorTool.ajustOpacity(
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


    suspend fun rectPuzzleAjustSize(
        srcBitmap: Bitmap,
        shakeRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        sizeCenterX: Int,
        sizeCenterY: Int,
        sizeIncline: Float,
        sizeOffset: Float,
        colorList: List<String>,
        passionColorList: List<String>,
        passionInt: Int,
        times: Int,
        isOverlay: Boolean
    ): Bitmap {
        val baseWidth = srcBitmap.width
        val baseHeight = srcBitmap.height
        val baseCutWidth = (baseWidth * shakeRate).toInt()
        val baseCutHeight = (baseHeight * shakeRate).toInt()
        val sizeRatioTotal = (baseWidth + baseHeight).toFloat()
        val widthRatio = baseWidth / sizeRatioTotal
        val heightRatio = baseHeight / sizeRatioTotal
        val xYPairToBitmapList = withContext(Dispatchers.IO) {
            val xYPairToBitmapListJob = (0..times).map {
                async {

                    val offsetX = try {
                        (1..baseWidth - baseCutWidth).random()
                    } catch (e: Exception) {
                        0
                    }
                    val offsetY = try {
                        (1..baseHeight - baseCutHeight).random()
                    } catch (e: Exception) {
                        0
                    }
                    val distance = calculateDistance(
                        sizeCenterX.toFloat(),
                        sizeCenterY.toFloat(),
                        offsetX.toFloat(),
                        offsetY.toFloat(),
                    )
                    val cutWidth = let {
                        sizeIncline * abs(distance) * widthRatio +
                                (baseCutWidth + sizeOffset * widthRatio)
                    }.toInt().let {
                        when(true){
                            (it + offsetX > baseWidth) -> baseWidth - offsetX
                            (it <= 0) -> 1
                            else -> it
                        }
                    }
                    val cutHeight = let {
                        sizeIncline * abs(distance) * heightRatio +
                                (baseCutHeight + sizeOffset * heightRatio)
                    }.toInt().let {
                        when(true){
                            (it + offsetY > baseHeight) -> baseHeight - offsetY
                            (it <= 0) -> 1
                            else -> it
                        }
                    }
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultSDebugAppDirPath, "lect${it}.txt").absolutePath,
//                        listOf(
//                            "offsetX: $offsetX",
//                            "offsetY: $offsetY",
//                            "distance: $distance",
//                            "baseCutWidth: $baseCutWidth",
//                            "baseCutHeight: ${baseCutHeight}",
//                            "cutWidth: $cutWidth",
//                            "cutHeight: ${cutHeight}",
//                           "sizeIncline: ${sizeIncline}",
//                            "sizeOffset: ${sizeOffset}",
//                            "widthRatio: ${widthRatio}",
//                            "${sizeIncline * abs(distance) * widthRatio +
//                                (baseCutWidth + sizeOffset * widthRatio)
//                        }"
//                        ).joinToString("\n")
//                    )
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
                            ColorTool.convertBlackToColor(
                                it,
                                colorList.random(),
                            )
                        }
                        else -> ImageCut.cutByTarget(
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
                            ColorTool.convertBlackToColor(
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
                            opacityIncline * offsetX + (opacitySrc + opacityOffset)
                        }.toInt().let {
                            if(it <= 0) return@let 5
                            it
                        }
                        ColorTool.ajustOpacity(
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
            resultBitmap = ImageOverlay.overlayOnBkBitmapByPivot(
                resultBitmap,
                cutBitmap,
                xyPair.first,
                xyPair.second
            )
        }
        return resultBitmap
    }
    private fun calculateDistance(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
    ): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
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
                            ColorTool.convertBlackToColor(
                                it,
                                colorList.random(),
                            )
                        }
                        else -> ImageCut.cutByTarget(
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
                            ColorTool.convertBlackToColor(
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
                        ColorTool.ajustOpacity(
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
            resultBitmap = ImageOverlay.overlayOnBkBitmapByPivot(
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

    suspend fun bitmapPuzzle(
        srcBitmap: Bitmap,
        peaceBitmapListSrc: List<Bitmap>,
        shakeRate: Float,
        minOpacityRate: Float,
        maxOpacityRate: Float,
        opacityIncline: Float,
        opacityOffset: Float,
        sizeCenterX: Int,
        sizeCenterY: Int,
        sizeIncline: Float,
        sizeOffset: Float,
        colorList: List<String>,
        passionColorList: List<String>,
        passionInt: Int,
        times: Int,
        isOverlay: Boolean,
    ): Bitmap {
        val baseWidth = srcBitmap.width
        val baseHeight = srcBitmap.height
        val baseCutWidth = (baseWidth * shakeRate).toInt()
        val baseCutHeight = (baseHeight * shakeRate).toInt()
        val sizeRatioTotal = (baseWidth + baseHeight).toFloat()
        val widthRatio = baseWidth / sizeRatioTotal
        val heightRatio = baseHeight / sizeRatioTotal
        val scalePeaceBitmapList = peaceBitmapListSrc.map {
            it.scale(baseCutWidth, baseCutHeight)
        }
        val xYPairToBitmapList = withContext(Dispatchers.IO) {
            val xYPairToBitmapListJob = (0..times).map {
                timeIndex ->
                async {
                    val offsetX = try {
                        (1..baseWidth - baseCutWidth).random()
                    } catch (e: Exception) {
                        0
                    }
                    val offsetY = try {
                        (1..baseHeight - baseCutHeight).random()
                    } catch (e: Exception) {
                        0
                    }
                    val distance = calculateDistance(
                        sizeCenterX.toFloat(),
                        sizeCenterY.toFloat(),
                        offsetX.toFloat(),
                        offsetY.toFloat(),
                    )
                    val cutWidth = let {
                        sizeIncline * abs(distance) * widthRatio +
                                (baseCutWidth + sizeOffset * widthRatio)
                    }.toInt().let {
                        when(true){
                            (it + offsetX > baseWidth) -> baseWidth - offsetX
                            (it <= 0) -> 1
                            else -> it
                        }
                    }
                    val cutHeight = let {
                        sizeIncline * abs(distance) * heightRatio +
                                (baseCutHeight + sizeOffset * heightRatio)
                    }.toInt().let {
                        when(true){
                            (it + offsetY > baseHeight) -> baseHeight - offsetY
                            (it <= 0) -> 1
                            else -> it
                        }
                    }
                    val cutBitmap = when(
                        (0..passionInt).random() == 1
                                && passionColorList.isNotEmpty()
                    ) {
                        true -> scalePeaceBitmapList.random().let {
                            val colorStr = passionColorList.random()
                            if(
                                colorStr.toColorInt() == Color.BLACK
                            ) return@let it
                            ColorTool.convertBlackToColorFixAlpha(
                                it,
                                colorList.random(),
                            )
                        }
                        else -> ImageCut.cutByTarget(
                            srcBitmap,
                            cutWidth,
                            cutHeight,
                            offsetX,
                            offsetY,
                        ).let {
                            val maskBitmap = MaskTool.mask(
                                it,
                               scalePeaceBitmapList.random(),
                            )
                            maskBitmap
                        }
                            .let {
                            val colorStr = colorList.random()
                            if(
                                colorStr.toColorInt() == Color.BLACK
                            ) return@let it
                            ColorTool.convertBlackToColorFixAlpha(
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
                            opacityIncline * offsetX + (opacitySrc + opacityOffset)
                        }.toInt().let {
                            if(it <= 0) return@let 5
                            it
                        }
                        ColorTool.ajustOpacity(
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
            resultBitmap = ImageOverlay.overlayOnBkBitmapByPivot(
                resultBitmap,
                cutBitmap,
                xyPair.first,
                xyPair.second
            )
        }
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
                        ColorTool.convertBlackToColor(
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
                    val ajustOpacityBitmap = ColorTool.ajustOpacity(
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
            resultBitmap = ImageOverlay.overlayOnBkBitmapByPivot(
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
                            colorStr.toColorInt() == Color.BLACK
                        ) return@let targetBitmap
                        ColorTool.convertBlackToColor(
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
                    val ajustOpacityBitmap = ColorTool.ajustOpacity(
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
            resultBitmap = ImageOverlay.overlayOnBkBitmapByPivot(
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
                        ColorTool.convertBlackToColor(
                            peaceBitmaps,
                            colorList.random()
                        )
                    }.let {
                        ColorTool.ajustOpacity(
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

    enum class JaggedDirection(){
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
    }
    fun createJaggedBitmap(
        bitmap: Bitmap,
        jaggedDirection: JaggedDirection,
        jaggedness: Float,
    ): Bitmap {
        // パラメータチェック
        if (jaggedness <= 0) {
            throw IllegalArgumentException("jaggednessは0より大きい値を指定してください。")
        }

        // 元のBitmapの情報を取得
        val width = bitmap.width
        val height = bitmap.height

        // ARGB_8888で新しいBitmapを作成（アルファチャネルが必要なため）
        val resultBitmap = createBitmap(width, height)
        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        paint.isAntiAlias = true // アンチエイリアスを有効にする

        // 乱数ジェネレータを初期化
        val random =  Random()

        // ぎざぎざのPathを作成する関数
        fun createJaggedPath(
            length: Float,
            isHorizontal: Boolean,
            startOffset: Float = 0f,
        ): Path {
            val path = Path()
            var currentX = if (isHorizontal) startOffset else 0f
            var currentY = if (isHorizontal) 0f else startOffset
            val segmentLength = length / 20f // セグメント数を調整
            val segmentCount = (length / segmentLength).toInt() + 1 // +1 for the last point
            path.moveTo(currentX, currentY)

            for (i in 0 until segmentCount) {
                // XまたはY座標をランダムに変動させる
                val delta = (random.nextFloat() * 2 - 1) * jaggedness
                if (isHorizontal) {
                    currentX += segmentLength
                    currentY += delta
                } else {
                    currentX += delta
                    currentY += segmentLength
                }
                path.lineTo(currentX, currentY)
            }
            // 終点を確実に指定
            if (isHorizontal) {
                path.lineTo(length, currentY)
            } else {
                path.lineTo(currentX, length)
            }
            return path
        }

//        when(jaggedDirection) {
//           JaggedDirection.TOP -> {
            // 上の辺のPathを作成
                val topPath = createJaggedPath(width.toFloat(), true)
            // 元のBitmapをPathでクリップして描画
            canvas.withClip(topPath) {

                // 上の辺をクリップ
                drawBitmap(bitmap, 0f, 0f, paint)
            }
//           }
//            JaggedDirection.RIGHT -> {
                // 右の辺のPathを作成
                val rightPath = createJaggedPath(height.toFloat(), false, width.toFloat())

                canvas.withClip(rightPath) {
                    // 右の辺をクリップ
                    drawBitmap(bitmap, 0f, 0f, paint)
                }
//            }
//            JaggedDirection.BOTTOM -> {
                // 下の辺のPathを作成
                val bottomPath = createJaggedPath(width.toFloat(), true, width.toFloat()).apply {
                    lineTo(
                        width.toFloat(),
                        height.toFloat()
                    ) // Ensure the path goes to the bottom right corner
                    lineTo(0f, height.toFloat()) // and bottom left
                }

                canvas.withClip(bottomPath) {
                    // 下の辺をクリップ
                    drawBitmap(bitmap, 0f, 0f, paint)
                }
//            }
//            JaggedDirection.LEFT -> {
                // 左の辺のPathを作成
                val leftPath = createJaggedPath(height.toFloat(), false, 0f).apply {
                    lineTo(0f, height.toFloat())
                }

                canvas.withClip(leftPath) {
                    // 左の辺をクリップ
                    drawBitmap(bitmap, 0f, 0f, paint)
                }
//            }
//        }

        return resultBitmap
    }
}