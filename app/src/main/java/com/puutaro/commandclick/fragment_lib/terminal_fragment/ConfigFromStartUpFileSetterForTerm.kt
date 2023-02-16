package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FirstUrlHistoryFile
import com.puutaro.commandclick.util.*


class ConfigFromStartUpFileSetterForTerm {
    companion object {


        fun set(
            terminalFragment: TerminalFragment,
        ){
            val activity = terminalFragment.activity
            val sharePref = activity?.getPreferences(Context.MODE_PRIVATE)
            terminalFragment.currentAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            )


            val settingVariableListFromConfig = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    UsePath.cmdclickConfigDirPath,
                    UsePath.cmdclickConfigFileName
                ).textToList(),
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END
            )

            terminalFragment.fontZoomPercent =  MakeVariableNumValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE,
                "1"
            )

            terminalFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.TERMINAL_COLOR,
                CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
            )
            terminalFragment.terminalFontColor = MakeVariableStringValue.make(
                settingVariableListFromConfig,
                CommandClickShellScript.TERMINAL_FONT_COLOR,
                CommandClickShellScript.TERMINAL_FONT_COLOR_DEFAULT_VALUE
            )

            val currentShellFileNameSource = SharePreffrenceMethod.getStringFromSharePreffrence(
                sharePref,
                SharePrefferenceSetting.current_shell_file_name
            )

            val editFragmentInstance =
                TargetFragmentInstance().getFromFragment<EditFragment>(
                    terminalFragment.activity,
                    terminalFragment.getString(R.string.cmd_variable_edit_fragment)
                )
            val currentShellFileName = if (
                terminalFragment.tag ==
                terminalFragment.context?.getString(
                    R.string.index_terminal_fragment
                )
            ) UsePath.cmdclickStartupShellName
            else currentShellFileNameSource


            val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    terminalFragment.currentAppDirPath,
                    currentShellFileName
                ).textToList(),
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END
            )

            terminalFragment.fontZoomPercent =  MakeVariableNumValue.make(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM,
                terminalFragment.fontZoomPercent,
                "1"
            )
            terminalFragment.binding.terminalWebView.settings.textZoom =
                terminalFragment.fontZoomPercent

            terminalFragment.onHistoryUrlTitle = CommandClickVariables.substituteCmdClickVariable(
                settingVariableList,
                CommandClickShellScript.CMDCLICK_ON_HISTORY_URL_TITLE
            ) ?: terminalFragment.onHistoryUrlTitle
            FirstUrlHistoryFile.delete(
                terminalFragment,
                terminalFragment.currentAppDirPath
            )

            terminalFragment.terminalColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_COLOR,
                terminalFragment.terminalColor
            )

            terminalFragment.terminalFontColor = MakeVariableStringValue.make(
                settingVariableList,
                CommandClickShellScript.TERMINAL_FONT_COLOR,
                terminalFragment.terminalFontColor
            )
        }
    }
}