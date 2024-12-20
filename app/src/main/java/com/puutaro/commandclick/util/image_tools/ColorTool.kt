package com.puutaro.commandclick.util.image_tools

import android.content.Context
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.common.variable.res.CmdClickColor

object ColorTool {

    private val colorList = CmdClickColor.entries

    fun parseColorStr(
        context: Context?,
        colorStr: String
    ): String {
        if(
            colorStr.startsWith("#")
        ) return colorStr
        val color = colorList.firstOrNull {
            colorStr == it.str
        } ?: return colorStr
        if(context == null) return colorStr
        val colorInt = ContextCompat.getColor(context, color.id)
        return "#${Integer.toHexString(colorInt)}"
    }
}