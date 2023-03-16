package com.puutaro.commandclick.proccess.intent.lib

import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object UrlLaunchMacro {
    fun launch(
        terminalViewModel: TerminalViewModel,
        recentAppDirPath: String,
        onUrlLaunchMacro: String,
    ) {
        val appUrlSystemPath = "${recentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        when(onUrlLaunchMacro){
            SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.RECENT.name -> {
                terminalViewModel.launchUrl += ReadText(
                    appUrlSystemPath,
                    UsePath.cmdclickUrlHistoryFileName
                ).textToList()
                    .filter {
                        EnableUrlPrefix.check(
                            it.split("\t").lastOrNull()
                        )
                    }
                    .firstOrNull()
                    ?.split("\t")?.lastOrNull()
            }
            SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.FREQUENCY.name -> {
                terminalViewModel.launchUrl += ReadText(
                    appUrlSystemPath,
                    UsePath.cmdclickUrlHistoryFileName
                ).textToList()
                    .filter {
                        EnableUrlPrefix.check(
                            it.split("\t").lastOrNull()
                        )
                    }
                    .groupBy { it }
                    .mapValues { it.value.size }
                    .maxBy { it.value }
                    .key
                    .split("\t")
                    .lastOrNull()
            }
            else -> {

            }
        }
    }

}