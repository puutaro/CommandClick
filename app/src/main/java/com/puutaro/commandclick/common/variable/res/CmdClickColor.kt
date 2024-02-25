package com.puutaro.commandclick.common.variable.res

import com.puutaro.commandclick.R

enum class CmdClickColor(
    val str: String,
    val id: Int,
) {
    BLUE("blue", R.color.web_icon_color),
    GRAY("gray", R.color.gray_out),
    LIGHT_GREEN("lightGreen", R.color.fannel_icon_color),
    DARK_GREEN("darkGreen", R.color.terminal_color),
    RED("red", com.termux.shared.R.color.dark_red)
}