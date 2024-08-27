package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object TerminalShowByTerminalDo {

    private val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    private const val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

    fun show(
        editFragment: EditFragment,
    ){
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelConList = ReadText(
            File(
                UsePath.cmdclickDefaultAppDirPath,
                currentFannelName
            ).absolutePath
        ).textToList()
        if(
            currentFannelConList.isEmpty()
        ) return
        val variablesSettingHolderList = CommandClickVariables.extractValListFromHolder(
            currentFannelConList,
            settingSectionStart,
            settingSectionEnd
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