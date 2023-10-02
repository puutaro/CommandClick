package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath


object CommandClickVariables {
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
            QuoteTool.trimBothEdgeQuote(it)
        }
    }

    fun substituteCmdClickVariableList(
        substituteSettingVariableList: List<String>?,
        substituteVariableName: String,
    ): List<String>? {
        if(substituteSettingVariableList == null) return null
        return substituteSettingVariableList.filter {
            it.startsWith("${substituteVariableName}=")
        }.map {
            val targetSettingValue =
                it.removePrefix("${substituteVariableName}=")
            targetSettingValue.replace(",", "\n")
        }.joinToString("\n")
            .split("\n")
            .map {
                QuoteTool.trimBothEdgeQuote(
                    it
                )
            }.filter {
                it.isNotEmpty()
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
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageTypeSelects
            )
        val variablesSettingHolderList =
            substituteVariableListFromHolder(
                shellContentsList,
                languageTypeHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
                ),
                languageTypeHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
                )
            )

        return substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickScriptVariable.EDIT_EXECUTE
        ) ?: SettingVariableSelects.EditExecuteSelects.NO.name
    }

    fun makeScriptContentsList(
        currentAppDirPath: String,
        scriptName: String,
    ): List<String> {
        val configDirName = scriptName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        return ReadText(
            currentAppDirPath,
            scriptName
        ).readText().let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                configDirName,
                scriptName,
            )
        }.split("\n")
    }
}
