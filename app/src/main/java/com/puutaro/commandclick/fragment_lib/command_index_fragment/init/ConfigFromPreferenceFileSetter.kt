package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
//import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.*
import java.io.File


object ConfigFromPreferenceFileSetter {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
    ){

        val cmdclickPreferenceJsName = SystemFannel.preference

        val preferenceConList = when(
            File(UsePath.cmdclickDefaultAppDirPath, cmdclickPreferenceJsName).isFile
        ) {
            false ->
                CommandClickVariables.makeMainFannelConListFromUrl(
                    cmdIndexFragment.context,
                    cmdclickPreferenceJsName
                )
            else ->
                CommandClickVariables.makeMainFannelConList(
                    cmdclickPreferenceJsName
                )

        }

        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            preferenceConList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
        )

        cmdIndexFragment.onTermVisibleWhenKeyboard = SettingVariableReader.getCbValue(
            settingVariableList,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE,
            SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.INHERIT.name,
            CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE,
            sequenceOf(
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name,
                SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
            ),
        )

        cmdIndexFragment.terminalColor = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.TERMINAL_COLOR,
            CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
        )
    }
}