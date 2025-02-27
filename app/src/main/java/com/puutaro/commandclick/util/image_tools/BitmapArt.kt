package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Color
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.num.RateTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

object BitmapArt {

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
                                Color.parseColor(colorStr) == Color.BLACK
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
                                Color.parseColor(colorStr) == Color.BLACK
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
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap.txt").absolutePath,
            listOf(
                xYPairToBitmapList.map{it.first}.joinToString("--"),
                "shakeRate: ${shakeRate}",
                "minOpacityRate: ${minOpacityRate}",
                "maxOpacityRate: ${maxOpacityRate}",
                "colorList: ${colorList}",
                "opacityBandNum: ${opacityIncline}",
                "times: ${times}"
            ).joinToString("\n\n")
        )
//        FileSystems.writeFromByteArray(
//            File(UsePath.cmdclickDefaultAppDirPath, "lbitmap22.png").absolutePath,
//            BitmapTool.convertBitmapToByteArray(resultBitmap)
//        )
        return resultBitmap
    }


}