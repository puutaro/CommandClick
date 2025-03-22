package com.puutaro.commandclick.util

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.AltRegexTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.runBlocking
import java.io.File


object CommandClickVariables {

    private const val settingSecStart = CommandClickScriptVariable.SETTING_SEC_START
    private const val settingSecEnd = CommandClickScriptVariable.SETTING_SEC_END

    fun substituteCmdClickVariable(
        substituteSettingVariableList: List<String>?,
        substituteVariableName: String,
    ): String? {
        if(substituteSettingVariableList == null) return null
        val shellFileNameRowString = substituteSettingVariableList.lastOrNull {
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

    fun substituteFilePrefixPath(
        substituteSettingVariableList: List<String>?,
        substituteVariableName: String,
        pathInOnlyFilePrefix: String
    ): String? {
        val withFilePrefixSettingValue = substituteCmdClickVariable(
            substituteSettingVariableList,
            substituteVariableName
        ) ?: return null
        val filePrefix = EditSettings.filePrefix
        val isOnlyFilePrefix =
            AltRegexTool.trim(withFilePrefixSettingValue) == filePrefix
        val isFilePrefix =
            withFilePrefixSettingValue.startsWith(filePrefix)
                    && AltRegexTool.trim(withFilePrefixSettingValue) != filePrefix
        return when {
            isOnlyFilePrefix -> pathInOnlyFilePrefix
            isFilePrefix -> withFilePrefixSettingValue
                .removePrefix(filePrefix)
                .let {
                    AltRegexTool.trim(it)
                }
            else -> String()
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
        if(
            substituteSettingVariableList == null
        ) return null
        return substituteSettingVariableList.asSequence().filter {
            it.startsWith("${substituteVariableName}=")
        }.map {
            val targetSettingValue =
                it.removePrefix("${substituteVariableName}=")
            QuoteTool.replaceBySurroundedIgnore(
                QuoteTool.trimBothEdgeQuote(targetSettingValue),
                ',',
                "\n"
            )
//            targetSettingValue.replace(",", "\n")
        }.joinToString("\n")
            .split("\n")
            .asSequence()
            .map {
                QuoteTool.trimBothEdgeQuote(
                    it
                )
            }.filter {
                it.isNotEmpty()
            }.toList()
    }

//    fun judgeJsOrShellFromSuffix(
//        shellScriptName: String
//    ): LanguageTypeSelects {
//        if(
//            shellScriptName.endsWith(
//                UsePath.SHELL_FILE_SUFFIX
//            )
//        ) return LanguageTypeSelects.SHELL_SCRIPT
//        return  LanguageTypeSelects.JAVA_SCRIPT
//    }

    fun extractSettingValListByFannelName(
        shellContentsList: List<String>?,
//        fannelName: String,
    ): List<String>? {
//        val languageType =
//            judgeJsOrShellFromSuffix(fannelName)
//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//        val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//        val settingSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String
        return extractValListFromHolder(
            shellContentsList,
            settingSecStart,
            settingSecEnd,
        )
    }

    fun extractValListFromHolder(
        shellContentsList: List<String>?,
        startHolderName: String?,
        endHolderName: String?,
    ): List<String>? {
//        if(startHolderName.isNullOrEmpty()) return null
//        if(endHolderName.isNullOrEmpty()) return null
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
//        languageTypeSelects: LanguageTypeSelects
    ): List<String>? {
//        val languageTypeHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//                languageTypeSelects
//            )
        return extractValListFromHolder(
            shellContentsList,
            settingSecStart,
            settingSecEnd
//                languageTypeHolderMap?.get(
//                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//                ),
//                languageTypeHolderMap?.get(
//                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//                )
            )
    }
    fun returnEditExecuteValueStr(
        shellContentsList: List<String>,
//        languageTypeSelects: LanguageTypeSelects
    ): String {
//        val languageTypeHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//                languageTypeSelects
//            )
        val variablesSettingHolderList =
            extractValListFromHolder(
                shellContentsList,
                settingSecStart,
                settingSecEnd
//                settingSecStart,
//                settingSecEnd
            )

        return substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickScriptVariable.EDIT_EXECUTE
        ) ?: SettingVariableSelects.EditExecuteSelects.NO.name
    }

    fun makeMainFannelConList(
//        currentAppDirPath: String,
        fannelName: String,
        setReplaceVariableMap: Map<String, String>? = null,
    ): List<String> {
//        val isMainFannelDir = currentAppDirPath.removePrefix(
//            UsePath.cmdclickAppDirPath
//        ).removePrefix("/").let{
//            File(it).parent
//        }.isNullOrEmpty()
        val isFannelName = FannelInfoTool.isEmptyFannelName(fannelName)
        if(
//            !isMainFannelDir
            isFannelName
        ) return emptyList()
        val scriptCon = ReadText(
            File(
                UsePath.cmdclickDefaultAppDirPath,
                fannelName
            ).absolutePath
        ).readText()
        return replace(
            scriptCon,
            fannelName,
            setReplaceVariableMap,
        )
    }

    fun makeMainFannelConListFromUrl(
        context: Context?,
        fannelName: String,
        setReplaceVariableMap: Map<String, String>? = null,
    ): List<String> {
        val fannelCon = runBlocking {
            UrlFileSystems.getFannel(
                context,
                fannelName,
            )
        } ?: return emptyList()
        return replace(
            fannelCon,
            fannelName,
            setReplaceVariableMap,
        )
    }

    private fun replace(
        con: String,
        fannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): List<String> {

        val scriptConList = ScriptPreWordReplacer.replace(
            con,
//                currentAppDirPath,
            fannelName,
        ).split("\n")
        return when(
            setReplaceVariableMap.isNullOrEmpty()
        ){
            true -> scriptConList
            else -> SetReplaceVariabler.execReplaceByReplaceVariables(
                scriptConList.joinToString("\n"),
                setReplaceVariableMap,
//                currentAppDirPath,
                fannelName
            ).split("\n")
        }
    }

    fun replaceVariableInHolder(
        scriptContentsList: List<String>,
        replaceNewlineSepaCon: String,
        startHolder: String?,
        endHolder: String?,
    ): List<String> {
        var countStartHolder = 0
        var countEndHolder = 0
        if(
            startHolder.isNullOrEmpty()
        ) return scriptContentsList
        if(
            endHolder.isNullOrEmpty()
        ) return scriptContentsList
        if(
            replaceNewlineSepaCon.isEmpty()
        ) return scriptContentsList
        val replaceMap = replaceNewlineSepaCon.split("\n").map {
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
        ) return scriptContentsList
        return scriptContentsList.map {
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
            val compQuoteTemplate = keyValueList.filterIndexed { innerIndex, _ ->
                innerIndex > 0
            }.joinToString("=").let {
                valueLine ->
                QuoteTool.extractBothQuote(valueLine)
            }.let {
                compQuote ->
                "${compQuote}%s${compQuote}"
            }

            val replaceValue = replaceMap.get(key)?.let{
                QuoteTool.trimBothEdgeQuote(it)
            } ?: return@map it
            if(
                keyValueListSize < 2
            ) return@map it

            "${key}=${compQuoteTemplate.format(replaceValue)}"
        }
    }
}
