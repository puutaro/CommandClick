package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.util.LogSystems
import org.jsoup.Jsoup

object ColorTool {

    private enum class ColorRndStr{
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
}