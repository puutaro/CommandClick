package com.puutaro.commandclick.fragment_lib.edit_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.ShortcutOnValueStr
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ConfigFromShellFileSetter {
    companion object {
        fun set(
            editFragment: EditFragment,
            readSharePreffernceMap: Map<String, String>,
        ){
            val onShortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.on_shortcut
            )
            if (
                onShortcut != ShortcutOnValueStr.ON.name
            ) return

            val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_shell_file_name
            )
            val context = editFragment.context
            val currentShellContentsList = ReadText(
                currentAppDirPath,
                currentShellFileName
            ).textToList()

            val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
                currentShellContentsList,
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END
            )

            editFragment.historySwitch = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_HISTORY_SWITCH,
                editFragment.historySwitch,
                SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name,
                editFragment.historySwitch,
                listOf(
                    SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name,
                    SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
                ),
            )

            editFragment.urlHistoryOrButtonExec = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
                editFragment.urlHistoryOrButtonExec,
                SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name,
                editFragment.urlHistoryOrButtonExec,
                listOf(
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name,
                    SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name,
                ),
            )

            editFragment.statusBarIconColorMode = MakeVariableCbValue.make(
                settingVariableList,
                CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE,
                editFragment.statusBarIconColorMode,
                SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name,
                editFragment.urlHistoryOrButtonExec,
                listOf(
                    SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
                ),
            )

            editFragment.runShell = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_RUN_SHELL,
                editFragment.runShell
            )

            editFragment.shiban = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_SHIBAN,
                editFragment.shiban
            )

            editFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_COLOR,
                editFragment.terminalColor
            )

            editFragment.fontZoomPercent = MakeVariableNumValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM,
                editFragment.fontZoomPercent,
                "1"
            )

            if(
                editFragment.tag ==
                editFragment.context?.getString(R.string.setting_variable_edit_fragment)
            ) return
            editFragment.terminalOn = CommandClickVariables.substituteCmdClickVariable(
                settingVariableList,
                CommandClickShellScript.TERMINAL_DO
            ) ?: CommandClickShellScript.TERMINAL_DO_DEFAULT_VALUE
            if(
                editFragment.terminalOn == SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
                || editFragment.terminalOn == SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name
            ) {
                editFragment.editTerminalInitType = EditInitType.TERMINAL_SHRINK
                val listener = context
                        as? EditFragment.OnTerminalWebViewInitListenerForEdit
                listener?.onTerminalWebViewInitForEdit(
                    EditInitType.TERMINAL_SHRINK,
                )
            } else {
                editFragment.editTerminalInitType = EditInitType.TERMINAL_SHOW
                val listener = context
                        as? EditFragment.OnTerminalWebViewInitListenerForEdit
                listener?.onTerminalWebViewInitForEdit(
                    EditInitType.TERMINAL_SHOW,
                )
            }

        }
    }
}