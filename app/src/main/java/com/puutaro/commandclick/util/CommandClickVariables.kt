package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.ReadText
import java.io.File


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

    fun isExist(
        substituteSettingVariableList: List<String>?,
        substituteVariableName: String,
    ): Boolean {
        if(substituteSettingVariableList == null) return false
        return !substituteSettingVariableList.firstOrNull {
            it.startsWith("${substituteVariableName}=")
        }.isNullOrEmpty()

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

    fun judgeJsOrShellFromSuffix(
        shellScriptName: String
    ): LanguageTypeSelects {
        if(
            shellScriptName.endsWith(
                UsePath.SHELL_FILE_SUFFIX
            )
        ) return LanguageTypeSelects.SHELL_SCRIPT
        return  LanguageTypeSelects.JAVA_SCRIPT
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

    fun returnSettingVariableList(
        shellContentsList: List<String>,
        languageTypeSelects: LanguageTypeSelects
    ): List<String>? {
        val languageTypeHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageTypeSelects
            )
        return substituteVariableListFromHolder(
                shellContentsList,
                languageTypeHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
                ),
                languageTypeHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
                )
            )
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
        return ReadText(
            File(
                currentAppDirPath,
                scriptName
            ).absolutePath
        ).readText().let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                scriptName,
            )
        }.split("\n")
    }

    fun replaceVariableInHolder(
        scriptContents: String,
        replaceTabList: String,
        startHolder: String?,
        endHolder: String?,
    ): String {
        var countStartHolder = 0
        var countEndHolder = 0
        if(
            startHolder.isNullOrEmpty()
        ) return scriptContents
        if(
            endHolder.isNullOrEmpty()
        ) return scriptContents
        if(
            replaceTabList.isEmpty()
        ) return scriptContents
        val replaceMap = replaceTabList.split("\t").map {
            val keyValueList = it.split("=")
            val keyValueListSize = keyValueList.size
            if(
                keyValueList.size < 2
            ) return@map String() to String()
            val key = keyValueList.first()
            val value = keyValueList
                .takeLast(keyValueListSize - 1)
                .joinToString("=")
            key to value
        }.toMap().filterKeys { it.isNotEmpty() }
        if(
            replaceMap.isEmpty()
        ) return scriptContents
        return scriptContents.split('\n').map {
            if(
                it.startsWith(startHolder)
                && it.endsWith(startHolder)
            ) countStartHolder++
            if(
                it.startsWith(endHolder)
                && it.endsWith(endHolder)
            ) countEndHolder++
            if(
                countStartHolder == 0
                || countEndHolder > 0
            ) return@map it
            val keyValueList = it.split("=")
            val keyValueListSize = keyValueList.size
            val key = keyValueList.first()
            val replaceValue = replaceMap.get(key)?.let{
                QuoteTool.trimBothEdgeQuote(it)
            } ?: return@map it
            if(
                keyValueListSize < 2
            ) return@map it
            "${key}=\"${replaceValue}\""
        }.joinToString("\n")
    }
}
