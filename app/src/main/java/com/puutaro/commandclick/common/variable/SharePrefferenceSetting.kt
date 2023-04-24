package com.puutaro.commandclick.common.variable

enum class SharePrefferenceSetting(
    val defalutStr: String
) {
    current_app_dir(UsePath.cmdclickDefaultAppDirPath),
    current_script_file_name(CommandClickScriptVariable.EMPTY_STRING),
    on_shortcut(ShortcutOnValueStr.OFF.name),
}