package com.puutaro.commandclick.common.variable.settings

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.state.FragmentTagManager

enum class SharePrefferenceSetting(
    val defalutStr: String
) {
    current_app_dir(UsePath.cmdclickDefaultAppDirPath),
    current_fannel_name(CommandClickScriptVariable.EMPTY_STRING),
    on_shortcut(FragmentTagManager.OnShortcutSuffix.OFF.name),
}