package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object FdialogToolForTerm {

    fun howExitExecThisProcess(
        terminalFragment: TerminalFragment
    ): Boolean {
        val context = terminalFragment.context
            ?: return true
        if(
            terminalFragment.tag ==
            context.getString(R.string.index_terminal_fragment)
        ) return false
        val isCmdValEdit =
            terminalFragment.editType ==
                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        if(
            !isCmdValEdit
        ) return true
        val isShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
            terminalFragment.readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        if(
            !isShortcut
        ) return true
        if(
            terminalFragment.terminalOn ==
            SettingVariableSelects.TerminalDoSelects.OFF.name
        ) return true
        return false
    }
}