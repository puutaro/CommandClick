package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.util.LogSystems
import org.jsoup.Jsoup
import androidx.core.graphics.toColorInt
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.set

object ColorTool {

    enum class ColorRndStr{
        RND,
        LIGHT_RND,
        WHITE_RND,
        DARK_RND,
    }
    private val colorList = CmdClickColor.entries
    private val colorStrList = CmdClickColorStr.entries.map {
        it.str
    }
    private val pink = "#fcf2fb"
    private val whiteColorStrList = listOf(
        CmdClickColorStr.WHITE_GREEN.str,
        CmdClickColorStr.WHITE_BLUE.str,
        CmdClickColorStr.WHITE_BLUE_PURPLE.str,
        pink,
    )
    private val lightColorStrList = listOf(
        CmdClickColorStr.LIGHT_GREEN.str,
        CmdClickColorStr.WHITE_GREEN.str,
        CmdClickColorStr.ANDROID_GREEN.str,
        CmdClickColorStr.YELLOW_GREEN.str,
        CmdClickColorStr.GREEN.str,
        CmdClickColorStr.WATER_BLUE.str,
        CmdClickColorStr.WHITE_BLUE.str,
        CmdClickColorStr.WHITE_BLUE_PURPLE.str,
        CmdClickColorStr.ORANGE.str,
        CmdClickColorStr.YELLOW.str,
        CmdClickColorStr.SKERLET.str,
        pink,
    )
    private val blackAo = "#02303b"
    private val blackRed = "#1c0104"
    private val darkColorStrList = listOf(
        CmdClickColorStr.DARK_GREEN.str,
        blackAo,
        CmdClickColorStr.BLUE_DARK_PURPLE.str,
        CmdClickColorStr.NAVY.str,
        CmdClickColorStr.DARK_BROWN.str,
        blackRed,
    )

    fun convertColorToHex(
        colorInt: Int,
    ): String {
        return String.format("#%06X", (0xFFFFFF and colorInt))
    }

    fun parseColorStr(
        context: Context?,
        colorStr: String,
        colorKey: String,
        where: String,
    ): String {
        val parsedColorStr = execParseColorStr(
            context,
            colorStr,
        )
        val whiteColor = "#ffffff"
        return colorChecker(
            context,
            parsedColorStr,
            colorKey,
            where,
        ) ?: whiteColor
    }

    private fun execParseColorStr(
        context: Context?,
        colorStr: String
    ): String {
        if(
            context == null
        ) return colorStr
        if(
            colorStr.startsWith("#")
        ) return colorStr
        if(
            colorStr == CmdClickColor.TRANSPARENT.str
        ) return "#00000000"
        val parsedColorStr = parseColorMacro(
            colorStr
        )
        if(!parsedColorStr.isNullOrEmpty()){
            return parsedColorStr
        }
        val color = colorList.firstOrNull {
            colorStr == it.str
        } ?: return colorStr
        val colorInt = ContextCompat.getColor(
            context,
            color.id
        )
        return "#${Integer.toHexString(colorInt)}"
    }

    fun parseColorMacro(
        colorStr: String?
    ): String? {
        if(
            colorStr.isNullOrEmpty()
        ) return null
        val colorRnd = ColorRndStr.entries.firstOrNull {
            it.name == colorStr
        } ?: return null
        return when(colorRnd){
            ColorRndStr.RND -> colorStrList.random()
            ColorRndStr.WHITE_RND -> whiteColorStrList.random()
            ColorRndStr.LIGHT_RND -> lightColorStrList.random()
            ColorRndStr.DARK_RND -> darkColorStrList.random()
        }
    }

    private fun colorChecker(
        context: Context?,
        colorStr: String?,
        colorKey: String,
        where: String,
    ): String? {
        return try {
            Color.parseColor(
                colorStr
            )
            colorStr
        } catch (e: Exception){
            val spanColorKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    colorKey
                )
            val spanSrcColorStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                colorStr.toString()
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            val errGenre = "[COLOR_PARSE_ERR]"
            val errMessage =
                "${errGenre} ${spanColorKey} parse err: ${spanSrcColorStr} about ${spanWhere}"
            LogSystems.broadErrLog(
                context,
                Jsoup.parse(errMessage).text(),
                errMessage
            )
            null
        }
    }

    fun removeAlpha(hexColor: String): String {
        return if (hexColor.length == 9) {
            val colorHexBody =
                hexColor.removePrefix("#")
                    .substring(2) // アルファ値部分を削除
            "#${colorHexBody}"
        } else {
            hexColor // 元の文字列をそのまま返す (アルファ値がない場合)
        }
    }

    fun replaceOpacity(
        hexColor: String,
        opacityRateSrc: Float,
    ): String {
        val rawHexColorStr =
            removeAlpha(hexColor)
                .removePrefix("#")
        val opacityRate = when(true){
            (opacityRateSrc > 1f) -> 1f
            (opacityRateSrc < 0f) -> 0f
            else -> opacityRateSrc
        }
        val opacityHex = Integer.toHexString(
            (255 * opacityRate).toInt()
        )
        return "#${opacityHex}${rawHexColorStr}"
    }

    fun calculateAverageColor(
        bitmap: Bitmap,
//        isIgnoreTrans: Boolean
    ): Int {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var totalPixels = 1
        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        for (pixel in pixels) {
            if(
                Color.alpha(pixel) == 0
            ) continue
            redSum += Color.red(pixel)
            greenSum += Color.green(pixel)
            blueSum += Color.blue(pixel)
            totalPixels++
        }

//        val pixelCount = pixels.size
        val averageRed = redSum / totalPixels
        val averageGreen = greenSum / totalPixels
        val averageBlue = blueSum / totalPixels

        return Color.rgb(averageRed, averageGreen, averageBlue)
    }
    object ClosestColor {
        /**
         * 16進数文字列のリストから、指定された16進数文字列に最も近い色の16進数文字列を取得します。
         *
         * @param hexStringList 16進数文字列のリスト (例: ["#ffffff", "#000000", "#ff0000"])
         * @param targetHexString 比較対象の16進数文字列 (例: "#808080")
         * @return 最も近い色の16進数文字列。リストが空の場合はnullを返します。
         */
        fun findClosestColor(
            hexStringList: List<String>,
            targetHexString: String
        ): String? {
            if (hexStringList.isEmpty()) {
                return null
            }

            // ターゲットの16進数文字列をRGBに変換
            val targetRgb = hexStringToRgb(
                removeAlpha(
                    targetHexString
                )
            )
            if (targetRgb == null) {
                return null // 無効な16進数文字列
            }

            var closestColor = hexStringList[0]
            var minDistance = calculateColorDistance(targetRgb, hexStringToRgb(closestColor)
                ?: Triple(0, 0, 0))

            // リスト内の各色との距離を計算し、最も近い色を更新
            for (hexStringSrc in hexStringList) {
                val hexString = removeAlpha(hexStringSrc)
                val rgb = hexStringToRgb(hexString) ?: continue // 無効な16進数文字列はスキップ
                val distance = calculateColorDistance(targetRgb, rgb)
                if (distance < minDistance) {
                    minDistance = distance
                    closestColor = hexString
                }
            }

            return closestColor
        }

        /**
         * 16進数文字列をRGBのTripleに変換します。
         *
         * @param hexString 16進数文字列 (例: "#ffffff")
         * @return RGBのTriple (例: (255, 255, 255))。無効な場合はnullを返します。
         */
        private fun hexStringToRgb(hexString: String): Triple<Int, Int, Int>? {
            if (hexString.length != 7 || hexString[0] != '#') {
                return null // 無効な16進数文字列
            }

            return try {
                val r = hexString.substring(1, 3).toInt(16)
                val g = hexString.substring(3, 5).toInt(16)
                val b = hexString.substring(5, 7).toInt(16)
                Triple(r, g, b)
            } catch (e: NumberFormatException) {
                null // パースエラー
            }
        }

        /**
         * 2つのRGB色間の距離を計算します (ユークリッド距離)。
         *
         * @param rgb1 RGBのTriple
         * @param rgb2 RGBのTriple
         * @return 2つの色間の距離
         */
        private fun calculateColorDistance(rgb1: Triple<Int, Int, Int>, rgb2: Triple<Int, Int, Int>): Double {
            val rDiff = rgb1.first - rgb2.first
            val gDiff = rgb1.second - rgb2.second
            val bDiff = rgb1.third - rgb2.third
            return Math.sqrt((rDiff * rDiff + gDiff * gDiff + bDiff * bDiff).toDouble())
        }

        fun main() {
            val colors = listOf("#ffffff", "#000000", "#ff0000", "#00ff00", "#0000ff", "#808080")
            val targetColor = "#7f7f7f"

            val closestColor = findClosestColor(colors, targetColor)
            println("Closest color to $targetColor is $closestColor") // 出力例: Closest color to #7f7f7f is #808080
        }
    }

    fun colorToHexString(color: Int): String {
        val alpha = Color.alpha(color)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
    }

    fun averageHexColors(hexColor1: String, hexColor2: String): String {
        // 16進数カラーストリグをRGB値に変換
        val color1 = hexColor1.toColorInt()
        val color2 = hexColor2.toColorInt()

        // 各RGB値を足し合わせる
        val red = (Color.red(color1) + Color.red(color2)) / 2
        val green = (Color.green(color1) + Color.green(color2)) / 2
        val blue = (Color.blue(color1) + Color.blue(color2)) / 2

        // 平均のRGB値を16進数カラーストリグに変換
        return String.format("#%02X%02X%02X", red, green, blue)
    }

    fun weightAvHexColors(
        hexColor1: String,
        hexColor2: String,
        color1Weight: Int,
        color2Weight: Int,
    ): String {
        // 16進数カラーストリグをRGB値に変換
        val color1 = hexColor1.toColorInt()
        val color2 = hexColor2.toColorInt()

        val colorTotalWeight = color1Weight + color2Weight
        // 各RGB値を足し合わせる
        val red = (
                Color.red(color1) * color1Weight +
                        Color.red(color2) * color2Weight
                )/ colorTotalWeight
        val green = (
                Color.green(color1) * color1Weight +
                        Color.green(color2) * color2Weight
                ) / colorTotalWeight
        val blue = (
                Color.blue(color1) * color1Weight +
                        Color.blue(color2) * color2Weight
                ) / colorTotalWeight

        // 平均のRGB値を16進数カラーストリグに変換
        return String.format("#%02X%02X%02X", red, green, blue)
    }

    fun makeColorVibrant(hexColor: String, amount: Float): String {
        val color = hexColor.toColorInt()
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)

        val max = max(max(red, green), blue).toFloat()
        val min = min(min(red, green), blue).toFloat()
        val delta = (max - min) / 255f

        if (delta == 0f) {
            return hexColor
        }

        red = (red + (max - red) * amount).coerceIn(0f, 255f).toInt()
        green = (green + (max - green) * amount).coerceIn(0f, 255f).toInt()
        blue = (blue + (max - blue) * amount).coerceIn(0f, 255f).toInt()

        return String.format("#%02X%02X%02X", red, green, blue)
    }

    fun convertColorToByTol (
        bitmap: Bitmap,
        targetColor: Int,
        replacementColor: Int,
        tolerance: Int
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixelColor = pixels[i]
            // 各色の成分の差を計算
            val alpha = abs(Color.red(pixelColor) - Color.alpha(targetColor))
            if(alpha == 0) continue
            val redDiff = abs(Color.red(pixelColor) - Color.red(targetColor))
            val greenDiff = abs(Color.green(pixelColor) - Color.green(targetColor))
            val blueDiff = abs(Color.blue(pixelColor) - Color.blue(targetColor))

            // 許容範囲内であれば色を置き換える
            if (
                redDiff >= tolerance
                || greenDiff >= tolerance
                || blueDiff >= tolerance
            ) continue
            pixels[i] = replacementColor
        }

        val resultBitmap = createBitmap(width, height)
        resultBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return resultBitmap
    }

    fun maxAlpha (
        bitmap: Bitmap,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixelColor = pixels[i]
            val alpha = Color.alpha(pixelColor)
            if(alpha == 0) continue
            pixels[i] = argb(
                255,
                Color.red(pixelColor),
                Color.green(pixelColor),
                Color.blue(pixelColor),
            )
        }

        val resultBitmap = createBitmap(width, height)
        resultBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return resultBitmap
    }

    fun convertWhiteToTransparent(
        originalBitmap: Bitmap,
    ): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        // Create a mutable copy of the bitmap
        val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = resultBitmap[x, y]
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                // Check if the pixel is white or close to white
                if (red > 240 && green > 240 && blue > 240) {
                    // Set the pixel to fully transparent
                    resultBitmap[x, y] = Color.TRANSPARENT
                }
            }
        }

        return resultBitmap
    }

    fun swapTransparentAndBlack(
        originalBitmap: Bitmap,
    ): Bitmap {
        return swap(
            originalBitmap,
            "#000000",
            "#00000000",
        )
    }

    fun exchangeColorToBlack(
        originalBitmap: Bitmap,
    ): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        // Create a mutable copy of the bitmap
        val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = resultBitmap[x, y]
                if (
                    Color.alpha(pixel) == 0
                ) {
                    resultBitmap[x, y] = Color.TRANSPARENT
                    continue
                }
                resultBitmap[x, y] = Color.BLACK
            }
        }

        return resultBitmap
    }

    fun exchangeWhiteToBlack(
        originalBitmap: Bitmap,
    ): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        // Create a mutable copy of the bitmap
        val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = resultBitmap[x, y]
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                // Check if the pixel is white or close to white
                if (red > 240 && green > 240 && blue > 240) {
                    // Set the pixel to fully transparent
                    resultBitmap[x, y] = Color.BLACK
                }
            }
        }

        return resultBitmap
    }


    fun ajustOpacity(
        bitmap: Bitmap,
        opacity: Int, //0(trans)..255
    ): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val colour = (opacity and 0xFF) shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        return mutableBitmap
    }

    fun addAlpha(
        originalBitmap: Bitmap,
        alpha: Float?,
    ): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        // Create a mutable copy of the bitmap
        val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = resultBitmap[x, y]
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                Color.argb(0 ,red, green, blue)
                if (
                    Color.alpha(pixel) == 0
                ) {
                    resultBitmap[x, y] = Color.BLACK
                    continue
                }
                resultBitmap[x, y] = Color.TRANSPARENT
            }
        }

        return resultBitmap
    }

    fun convertAllToColorInTrans(
        srcBitmap: Bitmap,
        colorStr: String?
    ): Bitmap {
        val transColorStr =
            "#00000000"
        val toColor = when(
            colorStr == transColorStr
        ) {
            true -> Color.TRANSPARENT
            else -> colorStr?.toColorInt()
                ?: transColorStr.toColorInt()
        }
        val width = srcBitmap.width
        val height = srcBitmap.height
        val capacity = width * height
//            val arrayList: ArrayList<Int> = ArrayList(capacity)
        val pixels = IntArray(capacity)
        // get pixel array from source
        srcBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val bmOut = createBitmap(width, height, srcBitmap.config!!)
        val trans = Color.TRANSPARENT
        // iteration through pixels
        val toColorRed = toColor.red
        val toColorGreen = toColor.green
        val toColorBlue = toColor.blue
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                val index = y * width + x
                val pixel = pixels[index]
                val alpha = Color.alpha(pixel)
                if(pixel == trans){
                    continue
                }
                pixels[index] =  argb(
                    alpha,
                    toColorRed,
                    toColorGreen,
                    toColorBlue,
                )

                /*or change the whole color
            pixels[index] = colorThatWillReplace;*/
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    fun convertBlackToColorFixAlpha(
        originalBitmap: Bitmap,
        colorStr: String,
    ): Bitmap {
        val parsedColor = when(
            colorStr == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> colorStr.toColorInt()
        }
        val blackInt = Color.BLACK
        if(
            parsedColor == blackInt
        ) return originalBitmap
        return changeColorFixAlpha(
            originalBitmap,
            Color.BLACK,
            parsedColor
        )
    }

    fun convertColorToFixAlpha(
        originalBitmap: Bitmap,
        fromColorStr: String,
        toColorStr: String,
    ): Bitmap {
        val fromParsedColor = when(
            fromColorStr == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> fromColorStr.toColorInt()
        }
        val toParsedColor = when(
            toColorStr == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> toColorStr.toColorInt()
        }
        return changeColorFixAlpha(
            originalBitmap,
            toParsedColor,
            fromParsedColor
        )
    }

    fun convertBlackToColor(
        originalBitmap: Bitmap,
        colorStr: String,
    ): Bitmap {
        val parsedColor = when(
            colorStr == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> colorStr.toColorInt()
        }
        val blackInt = Color.BLACK
        if(
            parsedColor == blackInt
        ) return originalBitmap
        return changeColor(
            originalBitmap,
            Color.BLACK,
            parsedColor
        )
    }

    fun convertColorTo(
        originalBitmap: Bitmap,
        fromColorStr: String,
        toColorStr: String,
    ): Bitmap {
        val fromParsedColor = when(
            fromColorStr == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> fromColorStr.toColorInt()
        }
        val toParsedColor = when(
            toColorStr == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> toColorStr.toColorInt()
        }
        return changeColor(
            originalBitmap,
            toParsedColor,
            fromParsedColor
        )
    }

    fun otherToColor(
        src: Bitmap,
        saveColor: Int,
        toColor: Int,
    ): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val bmOut = createBitmap(width, height, src.config!!)

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                val index = y * width + x
                val pixel = pixels[index]
                val alpha = Color.alpha(pixel)
                if (
                    pixel != saveColor
                    && alpha != 0
                ) {
                    pixels[index] = toColor

                    /*or change the whole color
                pixels[index] = colorThatWillReplace;*/
                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    private fun changeColor(
        src: Bitmap,
        fromColor: Int,
        toColor: Int,
    ): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val bmOut = createBitmap(width, height, src.config!!)

//            var pixel: Int
        val toArgb = argb(
            toColor.alpha,
            toColor.red,
            toColor.green,
            toColor.blue,
        )
        // iteration through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                val index = y * width + x
                val pixel = pixels[index]
                if (pixel == fromColor) {
                    pixels[index] = toArgb

                    /*or change the whole color
                pixels[index] = colorThatWillReplace;*/
                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    private fun changeColorFixAlpha(
        src: Bitmap,
        fromColor: Int,
        toColor: Int,
    ): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val bmOut = createBitmap(width, height, src.config!!)

//            var pixel: Int
        // iteration through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                val index = y * width + x
                val pixel = pixels[index]
                val curAlpha = Color.alpha(pixel)
                val curRed = Color.red(pixel)
                val curGreen = Color.green(pixel)
                val curBlue = Color.blue(pixel)
                if (
                    curAlpha != 0
                    && curRed == fromColor.red
                    && curGreen == fromColor.green
                    && curBlue == fromColor.blue
                    ) {
                    pixels[index] = argb(
                        fromColor.alpha,
                        toColor.red,
                        toColor.green,
                        toColor.blue,
                    )

                    /*or change the whole color
                pixels[index] = colorThatWillReplace;*/
                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }
    fun swap(
        originalBitmap: Bitmap,
        colorStr1: String,
        colorStr2: String,
    ): Bitmap {
        val parsedColor1 = when(
            colorStr1 == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> colorStr1.toColorInt()
        }
        val parsedColor2 = when (
            colorStr2 == "#00000000"
        ) {
            true -> Color.TRANSPARENT
            else -> colorStr2.toColorInt()
        }
        val width = originalBitmap.width
        val height = originalBitmap.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val bmOut = createBitmap(width, height, originalBitmap.config!!)

//            var pixel: Int
        val argb1 = argb(
            parsedColor1.alpha,
            parsedColor1.red,
            parsedColor1.green,
            parsedColor1.blue,
        )
        val argb2 = argb(
            parsedColor2.alpha,
            parsedColor2.red,
            parsedColor2.green,
            parsedColor2.blue,
        )
        // iteration through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                val index = y * width + x
                val pixel = pixels[index]
                when(true) {
                    (pixel == parsedColor1) -> {
                        //change A-RGB individually
                        pixels[index] = argb2
                    }
                    (pixel == parsedColor2) -> {
                        //change A-RGB individually
                        pixels[index] = argb1
                    }
                    else -> {}
                }
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    fun changeAllToTrans(
        src: Bitmap,
        colorStr: String,
    ): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        // get pixel array from source
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        val bmOut = createBitmap(width, height, src.config!!)

//            var pixel: Int
        val colorInt = colorStr.toColorInt()
        val trans = Color.TRANSPARENT
        // iteration through pixels
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                val index = y * width + x
                val pixel = pixels[index]
                val alpha = Color.alpha(pixel)
                if(alpha == 0){
                    pixels[index] = colorInt
                    continue
                }
                pixels[index] = trans
            }
        }
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bmOut
    }

    fun convertGrayScaleBitmap(original: Bitmap): Bitmap {
        // You have to make the Bitmap mutable when changing the config because there will be a crash
        // That only mutable Bitmap's should be allowed to change config.
        val bmp = original.copy(Bitmap.Config.ARGB_8888, true)
        val bmpGrayscale = createBitmap(bmp.width, bmp.height)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter
        canvas.drawBitmap(bmp, 0F, 0F, paint)
        return bmpGrayscale
    }
}