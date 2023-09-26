package com.puutaro.commandclick.common.variable

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FragmentTagManager

enum class SharePrefferenceSetting(
    val defalutStr: String
) {
    current_app_dir(UsePath.cmdclickDefaultAppDirPath),
    current_script_file_name(CommandClickScriptVariable.EMPTY_STRING),
    on_shortcut(FragmentTagManager.Suffix.OFF.name),
}