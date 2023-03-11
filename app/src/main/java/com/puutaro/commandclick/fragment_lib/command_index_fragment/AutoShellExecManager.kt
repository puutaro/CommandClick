package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
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
            val jsContentsList = ReadText(
                currentAppDirPath,
                cmdclickStartupOrEndShellName
            ).textToList()
            val languageType = LanguageTypeSelects.JAVA_SCRIPT
            val languageTypeToSectionHolderMap =
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                    languageType
                )
            val settingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
            ) as String

            val settingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
            ) as String

            val substituteSettingVariableList =
                CommandClickVariables.substituteVariableListFromHolder(
                    jsContentsList,
                    settingSectionStart,
                    settingSectionEnd,
                )
            val onAutoShell = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.CMDCLICK_ON_AUTO_EXEC
            )
            if(
                onAutoShell !=
                SettingVariableSelects.Companion.AutoExecSelects.ON.name
            ) return
            ExecJsLoad.execJsLoad(
                cmdIndexFragment,
                currentAppDirPath,
                cmdclickStartupOrEndShellName,
                jsContentsList
            )
        }
    }
}