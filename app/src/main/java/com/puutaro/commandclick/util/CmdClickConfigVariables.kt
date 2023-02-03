package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.UsePath

class CmdClickConfigVariables {
    companion object {
        fun returnRunShell(
        ): String {
            val cmdValiableList = CommandClickVariables.substituteVariableListFromHolder(
                ReadText(
                    UsePath.cmdclickConfigDirPath,
                    UsePath.cmdclickConfigFileName
                ).txetToList(),
                CommandClickShellScript.CMD_VARIABLE_SECTION_START,
                CommandClickShellScript.CMD_VARIABLE_SECTION_END
            )
            return CommandClickVariables.substituteCmdClickVariable(
                cmdValiableList,
                CommandClickShellScript.CMDCLICK_RUN_SHELL
            ) ?: CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
        }
    }
}