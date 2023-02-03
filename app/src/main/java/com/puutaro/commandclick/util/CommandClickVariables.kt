package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects


class CommandClickVariables {
    companion object {
        fun substituteCmdClickVariable(
            substituteSettingVariableList: List<String>?,
            substituteVariableName: String,
        ): String? {
            if(substituteSettingVariableList == null) return null
            val shellFileNameRowString = substituteSettingVariableList.firstOrNull {
                it.startsWith("${substituteVariableName}=")
            } ?: return null
            val equalIndex = shellFileNameRowString.indexOf("=")
            if(equalIndex == -1) return null
            return shellFileNameRowString.substring(
                equalIndex + 1, shellFileNameRowString.length
            )
        }

        fun substituteVariableListFromHolder(
            shellContentsList: List<String>?,
            startHolderName: String,
            endHolderName: String,
        ): List<String>? {
            if(shellContentsList == null) return null
            val sectionPromptStartNum = shellContentsList.indexOf(
                startHolderName
            )
            val sectionPromptEndNum = shellContentsList.indexOf(
                endHolderName
            )
            return if(
                sectionPromptStartNum > 0
                && sectionPromptEndNum > 0
                && sectionPromptStartNum < sectionPromptEndNum
            ) {
                shellContentsList.slice(
                    sectionPromptStartNum..sectionPromptEndNum
                )
            } else null
        }

        fun returnEditExecuteValueStr(
            shellContentsList: List<String>
        ): String {
            val variablesSettingHolderList =
                substituteVariableListFromHolder(
                    shellContentsList,
                    CommandClickShellScript.SETTING_SECTION_START,
                    CommandClickShellScript.SETTING_SECTION_END
                )

            return substituteCmdClickVariable(
                variablesSettingHolderList,
                CommandClickShellScript.EDIT_EXECUTE
            ) ?: SettingVariableSelects.Companion.EditExecuteSelects.NO.name
        }
    }
}