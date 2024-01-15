package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object TerminalOnHandlerForEdit {
    fun handle(
        terminalFragment: TerminalFragment
    ){
        if(
            !terminalFragment.isVisible
        ) return
        val isCmdValEdit =
            terminalFragment.editType ==
                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        when(isCmdValEdit){
            true -> {
                val isShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
                    terminalFragment.readSharedPreferences,
                    SharePrefferenceSetting.on_shortcut
                ) == EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
                if(
                    isShortcut
                    && terminalFragment.terminalOn !=
                    SettingVariableSelects.TerminalDoSelects.OFF.name
                ) return
            }
            else -> {}
        }
        val context = terminalFragment.context
        val listener = context as? TerminalFragment.OnTermSizeMinimumListenerForTerm
        listener?.onTermSizeMinimumForTerm()
    }
}