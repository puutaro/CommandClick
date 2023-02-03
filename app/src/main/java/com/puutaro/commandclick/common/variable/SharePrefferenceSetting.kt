package com.puutaro.commandclick.common.variable

enum class SharePrefferenceSetting(
    val defalutStr: String
) {
    current_app_dir(UsePath.cmdclickDefaultAppDirPath),
    current_shell_file_name(CommandClickShellScript.EMPTY_STRING),
    on_shortcut(ShortcutOnValueStr.OFF.name),
}