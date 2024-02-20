package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object TerminalShowByTerminalDo {
    fun show(
        editFragment: EditFragment,
    ){
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val currentFannelConList = ReadText(
            File(
                currentAppDirPath,
                currentFannelName
            ).absolutePath
        ).textToList()
        if(
            currentFannelConList.isEmpty()
        ) return
        val variablesSettingHolderList = CommandClickVariables.substituteVariableListFromHolder(
            currentFannelConList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd
        )
        val terminalDo = CommandClickVariables.substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickScriptVariable.TERMINAL_DO
        )
        val onTerminalDoOffAndTermux = (
                terminalDo == SettingVariableSelects.TerminalDoSelects.OFF.name
                        || terminalDo == SettingVariableSelects.TerminalDoSelects.TERMUX.name
                )
        if(
            onTerminalDoOffAndTermux
        ) return
        val listener = editFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
        listener?.onKeyBoardVisibleChangeForEditFragment(
            false,
            true
        )
    }
}