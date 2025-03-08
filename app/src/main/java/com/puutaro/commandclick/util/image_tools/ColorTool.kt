package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.util.LogSystems
import org.jsoup.Jsoup

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

    fun calculateAverageColor(bitmap: Bitmap): Int {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        for (pixel in pixels) {
            redSum += Color.red(pixel)
            greenSum += Color.green(pixel)
            blueSum += Color.blue(pixel)
        }

        val pixelCount = pixels.size
        val averageRed = redSum / pixelCount
        val averageGreen = greenSum / pixelCount
        val averageBlue = blueSum / pixelCount

        return Color.rgb(averageRed, averageGreen, averageBlue)
    }
}