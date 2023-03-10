package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
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
            ).let {
                BothEdgeQuote.trim(it)
            }
        }

        fun substituteVariableListFromHolder(
            shellContentsList: List<String>?,
            startHolderName: String?,
            endHolderName: String?,
        ): List<String>? {
            if(startHolderName.isNullOrEmpty()) return null
            if(endHolderName.isNullOrEmpty()) return null
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
            shellContentsList: List<String>,
            languageTypeSelects: LanguageTypeSelects
        ): String {
            val languageTypeHolderMap =
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                    languageTypeSelects
                )
            val variablesSettingHolderList =
                substituteVariableListFromHolder(
                    shellContentsList,
                    languageTypeHolderMap?.get(
                        CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
                    ),
                    languageTypeHolderMap?.get(
                        CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
                    )
                )

            return substituteCmdClickVariable(
                variablesSettingHolderList,
                CommandClickShellScript.EDIT_EXECUTE
            ) ?: SettingVariableSelects.Companion.EditExecuteSelects.NO.name
        }
    }
}