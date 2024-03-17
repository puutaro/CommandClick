package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

object AlterConfig {
    const val alterKeyName = "alter"
    enum class IfKey(
        val key: String,
    ) {
        SHELL_IF_PATH("shellIfPath"),
        SHELL_IF_CON("shellIfCon"),
        IF_ARGS("ifArgs"),
    }

}