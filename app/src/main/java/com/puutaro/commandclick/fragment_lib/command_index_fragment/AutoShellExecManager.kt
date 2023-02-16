package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ExecTerminalDo
import com.puutaro.commandclick.util.*

class AutoShellExecManager {
    companion object {
        fun fire(
            cmdIndexFragment: CommandIndexFragment,
            cmdclickStartupOrEndShellName: String,
        ){
            if(
                cmdIndexFragment.onUrlLaunchIntent
            ) return

            val readSharePreffernceMap = cmdIndexFragment.readSharePreffernceMap
            if(
                readSharePreffernceMap.isEmpty()
            ) return
            val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )

            val activity = cmdIndexFragment.activity

            TargetFragmentInstance()
                .getFromFragment<TerminalFragment>(
                    activity,
                    activity?.getString(R.string.index_terminal_fragment)
                ) ?: return
            val shellContentsList = ReadText(
                currentAppDirPath,
                cmdclickStartupOrEndShellName
            ).textToList()
            val substituteSettingVariableList =
                CommandClickVariables.substituteVariableListFromHolder(
                    shellContentsList,
                    CommandClickShellScript.SETTING_SECTION_START,
                    CommandClickShellScript.SETTING_SECTION_END,
                )
            val onAutoShell = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.CMDCLICK_ON_AUTO_EXEC
            )
            if(
                onAutoShell !=
                SettingVariableSelects.Companion.AutoExecSelects.ON.name
            ) return
            ExecTerminalDo.execTerminalDo(
                cmdIndexFragment,
                currentAppDirPath,
                cmdclickStartupOrEndShellName,
                shellContentsList
            )
        }
    }
}