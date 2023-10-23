package com.puutaro.commandclick.common.variable.icon

import com.puutaro.commandclick.R

enum class CmcClickIcons(
        val str: String,
        val id: Int,
) {
    COPY("copy", androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
    SEARCH("search", R.drawable.icons8_search),
    BACK("back", com.afollestad.materialdialogs.R.drawable.md_nav_back),
    WHEEL("wheel", com.skydoves.colorpickerview.R.drawable.wheel),
    HISTORY("history", R.drawable.icons8_history),
    OVERFLOW("oeverflow", androidx.appcompat.R.drawable.abc_ic_menu_overflow_material),
    CANCEL("cancel", R.drawable.icons8_cancel),
    FILE("file", R.drawable.icons8_file),
    OK("ok", R.drawable.icons8_check_ok),
    PUZZLE("puzzle", R.drawable.icons8_puzzle),
    TERMINAL("terminal", R.drawable.ic_terminal),
    DOWN("down", R.drawable.ic_down_allow),
    REFLESH("reflesh", R.drawable.icons8_refresh),
    EDIT("edit", R.drawable.icons8_edit),
    SET_UP("setup", R.drawable.icons8_setup),
    SHORTCUT("shortcut", R.drawable.icons8_shortcut),
    FOLDA("folda", R.drawable.icons8_folda),
    SETTING("setting", R.drawable.icons8_setting),
    PLUS("plus", R.drawable.icons8_plus),
    SUPPORT("support", R.drawable.icons8_support),
    PLAY("play", R.drawable.play),
    SHARE("share", R.drawable.icons8_share),
    LAUNCH("launch", R.drawable.icons8_launch),
    UPDATE("update", R.drawable.icons8_update),
    INFO("info", R.drawable.icons8_info),
    ABOUT("about", R.drawable.icons8_about),
}