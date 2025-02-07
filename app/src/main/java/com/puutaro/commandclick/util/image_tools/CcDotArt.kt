package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import com.puutaro.commandclick.util.image_tools.BitmapTool.ImageTransformer
import com.puutaro.commandclick.util.image_tools.BitmapTool.concatByHorizon
import com.puutaro.commandclick.util.image_tools.BitmapTool.rotate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object CcDotArt {

    fun maskSquareMaker(
        resolution: Int,
        peaceLength: Int,
        rndList: IntRange,
        cutThresholds: Int,
    ): Bitmap? {
        val loopTimes = resolution / peaceLength
        var resultRect: Bitmap? = null
        for(i in 1..loopTimes) {
            var horizontalRect: Bitmap? = null
            for(j in 1..loopTimes) {
                val colorStr = when(rndList.random() <= cutThresholds) {
                    true -> "#00000000"
                    else -> "#000000"
                }
                val bitmap = ImageTransformer.makeRect(
                    colorStr,
                    peaceLength,
                    peaceLength
                )
                horizontalRect = when(horizontalRect == null){
                    true -> bitmap
                    else -> concatByHorizon(
                        horizontalRect,
                        bitmap
                    )
                }
            }
            val verticalBitmap = rotate(
                horizontalRect as Bitmap,
                90f,
            )
            resultRect = when(resultRect == null) {
                true -> verticalBitmap
                else -> concatByHorizon(
                    resultRect,
                    verticalBitmap
                )
            }
        }
        return resultRect
    }

    suspend fun makeMatrixStorm(
//        pieceWidth: Int,
//        pieceHeight: Int,
        pieceBitmapSrc: Bitmap,
        widthMulti: Int,
        heightMulti: Int,
    ): Bitmap? {
        if(
            widthMulti <= 0
            || heightMulti <= 0
        ) return null
        return withContext(Dispatchers.IO){
            val horizonRectList = let {
                val maxOpacity = 255
//                val pieceRectSrc = ImageTransformer.makeRect(
//                    "#000000",
//                    pieceWidth,
//                    pieceHeight,
//                )
                val opacityDiff = maxOpacity / widthMulti
                val firstOpacityInf = 230
                val horizonRectJobList = (1..heightMulti).map { verticalOrder ->
                    async {
                        val fistLowerOpacity = (maxOpacity - opacityDiff).let fistLowerOpacity@ {
                            if(
                                it < firstOpacityInf
                            ) return@fistLowerOpacity firstOpacityInf
                            it
                        }
                        val firstOpacity = (fistLowerOpacity..maxOpacity).random()
                        var horizontalRect = ImageTransformer.ajustOpacity(
                            pieceBitmapSrc,
                            firstOpacity
                        )
                        (2..widthMulti).forEach { horizonOrder ->
                            val curOpacityDiff = opacityDiff * (horizonOrder - 1)
                            val supOpacity = (maxOpacity - curOpacityDiff).let makeSupOpacity@ {
                                if (it <= 0) return@makeSupOpacity 10
                                it
                            }
                            val lowerOpacity = (supOpacity - curOpacityDiff).let makeLowerOpacity@ {
                                if (it <= 0) return@makeLowerOpacity 0
                                it
                            }
                            val curOpacity = when(
                                (1..5).random() == 1
                            ) {
                                true -> (0..maxOpacity).random()
                                else -> (lowerOpacity..supOpacity).random()
                            }
                            val pieceRect = ImageTransformer.ajustOpacity(
                                pieceBitmapSrc,
                                curOpacity
                            )
                            horizontalRect = concatByHorizon(
                                horizontalRect,
                                pieceRect
                            )
                        }
                        rotate(
                            horizontalRect,
                            90f
                        )
                    }
                }
                horizonRectJobList.awaitAll()
            }
            var chunkedHorizonRectList: List<List<Bitmap>> = horizonRectList.chunked(2)
            while(true) {
                val chunkedHorizonRectJob =
                    chunkedHorizonRectList.mapIndexed { index, horizonChunkedRectList ->
                        async {
                            var partVerticalRect = horizonChunkedRectList.first()
                            horizonChunkedRectList.forEachIndexed { horizonRectListIndex, horizonBitmap ->
                                if (horizonRectListIndex == 0) return@forEachIndexed
                                partVerticalRect = concatByHorizon(
                                    partVerticalRect,
                                    horizonBitmap
                                )
                            }
                            partVerticalRect
                        }
                    }
                chunkedHorizonRectList = chunkedHorizonRectJob.awaitAll().chunked(2)
                if(
                    chunkedHorizonRectList.size == 1
                    && chunkedHorizonRectList.first().size <= 1
                ) break
            }

            val verticalRect = chunkedHorizonRectList.first().first()
            rotate(
                verticalRect,
                -90f
            )
        }
    }

    object MistMaker {

        fun makeRndBitmap(
            width: Int,
            height: Int,
            backgroundColorStr: String,
            peaceBitmap: Bitmap,
            times: Int,
        ): Bitmap {
            val rectBitmap = ImageTransformer.makeRect(
                backgroundColorStr,
                width,
                height,
            )
            var updatedRectBitmap = rectBitmap
            val addTimes = (1..times).random()
            for (i in 1..addTimes) {
                val logoBitmapRate = (5..10).random() / 10f
                val rateLogoBitmap = Bitmap.createScaledBitmap(
                    peaceBitmap,
                    (peaceBitmap.width * logoBitmapRate).toInt(),
                    (peaceBitmap.height * logoBitmapRate).toInt(),
                    false,
                ).let {
                    rotate(
                        it,
                        (0..180).random().toFloat()
                    )
                }.let {
                    ImageTransformer.ajustOpacity(
                        it,
                        (150..255).random()
                    )
                }
                updatedRectBitmap = ImageTransformer.overlayOnBkBitmap(
                    updatedRectBitmap,
                    rateLogoBitmap
                )
            }
            return updatedRectBitmap
        }

        fun makeLeftRndBitmaps(
            width: Int,
            height: Int,
            pieceBitmap: Bitmap,
            startAngle: Int,
            endAngle: Int,
            times: Int,
        ): Bitmap {
            var baseRect = ImageTransformer.makeRect(
                "#00000000",
                width,
                height
            )
            val baseRectWidth = baseRect.width
            val baseRectHeight = baseRect.height
            if (
                times <= 0
            ) return baseRect
            for (i in 1..times) {
                val curPieceRectSrc = rotate(
                    pieceBitmap,
                    (startAngle..endAngle).random().toFloat()
                ).let {
                    val rate = (5..10).random() / 10f
                    Bitmap.createScaledBitmap(
                        it,
                        (it.width * rate).toInt(),
                        (it.height * rate).toInt(),
                        true,
                    )
                }
                val curPieceRectSrcWidth = curPieceRectSrc.width
                val pivotX = culcPivotXByLeftPrior(
                    baseRectWidth,
                    curPieceRectSrcWidth,
                ) ?: continue
//                (0..(baseRectWidth - curPieceRectSrc.width)).random()
                val pivotY = (0..(baseRectHeight - curPieceRectSrc.height)).random()
                val curOpacity = culcOpacityByHorizon(
                    baseRectWidth,
                    curPieceRectSrcWidth,
                    pivotX,
                )
                val curPieceRect = ImageTransformer.ajustOpacity(
                    curPieceRectSrc,
                    curOpacity
                )
                baseRect = ImageTransformer.overlayOnBkBitmapByPivot(
                    baseRect,
                    curPieceRect,
                    pivotX.toFloat(),
                    pivotY.toFloat(),
                )
            }
            return baseRect
        }

        fun makeMist(
            width: Int,
            height: Int,
            pieceBitmap: Bitmap,
            times: Int,
        ): Bitmap {
            var baseRect = ImageTransformer.makeRect(
                "#00000000",
                width,
                height
            )
            val baseRectWidth = baseRect.width
            val baseRectHeight = baseRect.height
            if (
                times <= 0
            ) return baseRect
            for (i in 1..times) {
                val curPieceRectSrc = rotate(
                    pieceBitmap,
                    (0..180).random().toFloat()
                ).let {
                    val rate = (5..10).random() / 10f
                    Bitmap.createScaledBitmap(
                        it,
                        (it.width * rate).toInt(),
                        (it.height * rate).toInt(),
                        true,
                    )
                }
                val curPieceRectSrcWidth = curPieceRectSrc.width
                val pivotX = try {
                    (0..(baseRectWidth - curPieceRectSrcWidth)).random()
                } catch (e: Exception){
                    continue
                }
                val pivotY = try {
                    (0..(baseRectHeight - curPieceRectSrc.height)).random()
                } catch (e: Exception){
                    continue
                }
                val curOpacity = culcOpacityByHorizon(
                    baseRectWidth,
                    curPieceRectSrcWidth,
                    pivotX,
                )
                val curPieceRect = ImageTransformer.ajustOpacity(
                    curPieceRectSrc,
                    curOpacity
                )
                baseRect = ImageTransformer.overlayOnBkBitmapByPivot(
                    baseRect,
                    curPieceRect,
                    pivotX.toFloat(),
                    pivotY.toFloat(),
                )
            }
            return baseRect
        }

        fun makeLeftRectByPivotXRnd(
            width: Int,
            height: Int,
            pieceWidth: Int,
            pieceHeight: Int,
            times: Int,
        ): Bitmap {
            var baseRect = ImageTransformer.makeRect(
                "#00000000",
                width,
                height
            )
            val baseRectWidth = baseRect.width
            val baseRectHeight = baseRect.height
            val pieceRect = ImageTransformer.makeRect(
                "#000000",
                pieceWidth,
                pieceHeight
            )
            val pieceRectWidth = pieceRect.width
            val pieceRectHeight = pieceRect.height
            val totalMarginLeft = baseRectWidth - pieceRectWidth
            val totalMarginTop = baseRectHeight - pieceRectHeight
            if (
                times <= 0
            ) return baseRect
            for (i in 1..times) {
                val curMarginLeft = (0..totalMarginLeft).random()
                val curMarginTop = (0..totalMarginTop).random()
                val curOpacity = culcOpacityByHorizon(
                    baseRectWidth,
                    pieceRectWidth,
                    curMarginLeft,
                )
                val curPieceRect = ImageTransformer.ajustOpacity(
                    pieceRect,
                    curOpacity
                )
                baseRect = ImageTransformer.overlayOnBkBitmapByPivot(
                    baseRect,
                    curPieceRect,
                    curMarginLeft.toFloat(),
                    curMarginTop.toFloat(),
                )
            }
            return baseRect
        }

        private fun culcPivotXByLeftPrior(
            baseWidth: Int,
            pieceWidth: Int,
        ): Int? {
            val selectPointList = makeSelectPointList(
                baseWidth,
                pieceWidth,
            ) ?: return null
            val pivotX = try {
                val selectPoint = selectPointList.random()
                val curBaseX = ((selectPoint - 1) * pieceWidth)
                val curOffSet = (0..pieceWidth).random()
                curBaseX + curOffSet
            } catch (e: Exception){
                return null
            }
            return pivotX
        }

        private fun makeSelectPointList(
            baseRectWidth: Int,
            rectWidth: Int,
        ): List<Int>? {
            val totalRangeNum = baseRectWidth / rectWidth
            if(
                totalRangeNum <= 0
            ) return null
            return (1..totalRangeNum).map {
                val endTimes = totalRangeNum - it
                if(
                    endTimes < 1
                ) {
                    return@map when(
                        (1..10).random() == 1
                    ){
                        true -> listOf(it)
                        else -> emptyList()
                    }
                }
                (1..endTimes).map {
                    it
                }
            }.flatten()

        }

        private fun culcOpacityByHorizon(
            baseRectWidth: Int,
            rectWidth: Int,
            x: Int,
        ): Int {
            val maxOpacity = 255
            val multi = baseRectWidth / rectWidth
            val opacityDiff = maxOpacity - multi
            val currentPoint = x / rectWidth
            val firstOpacityInf = 230
            return when (currentPoint) {
                0 -> (maxOpacity - opacityDiff).let fistLowerOpacity@{
                    if (
                        it < firstOpacityInf
                    ) return@fistLowerOpacity firstOpacityInf
                    it
                }

                else -> {
                    val curOpacityDiff = opacityDiff * (currentPoint - 1)
                    val supOpacity = (maxOpacity - curOpacityDiff).let makeSupOpacity@{
                        if (it <= 0) return@makeSupOpacity 10
                        it
                    }
                    val lowerOpacity = (supOpacity - curOpacityDiff).let makeLowerOpacity@{
                        if (it <= 0) return@makeLowerOpacity 0
                        it
                    }
                    when (
                        (1..5).random() == 1
                    ) {
                        true -> (0..maxOpacity).random()
                        else -> (lowerOpacity..supOpacity).random()
                    }

                }
            }
        }
    }

    fun dotArtMaker(
        srcBitmap: Bitmap
    ): Bitmap {
        val bitmap = Bitmap.createScaledBitmap(
            srcBitmap,
            32,
            32,
            false,
        )
        val dotBitmap = Bitmap.createScaledBitmap(
            bitmap,
            192,
            192,
            false,
        )
        return bitmap
    }
}