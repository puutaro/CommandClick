package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import java.io.File

object JavaScriptLoadUrl {
    fun make (
        execJsPath: String,
        jsListSource: List<String>? = null
    ):String? {
        val jsFileObj = File(execJsPath)
        if(!jsFileObj.isFile) return null
        val recentAppDirPath = jsFileObj.parent
        if(recentAppDirPath.isNullOrEmpty()) return null
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_END
        ) as String

        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion.HolderTypeName.CMD_SEC_END
        ) as String
        val scriptFileName = jsFileObj.name
        val fannelDirName = scriptFileName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        val jsList = if(
            jsListSource.isNullOrEmpty()
        ) {
            ReadText(
                recentAppDirPath,
                scriptFileName
            ).textToList()
        } else jsListSource
        val recordNumToMapNameValueInSettingHolder = RecordNumToMapNameValueInHolder.parse(
            jsList,
            settingSectionStart,
            settingSectionEnd,
            true
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMap(
                recordNumToMapNameValueInSettingHolder
            )
        var countSettingSectionStart = 0
        var countSettingSectionEnd = 0
        var countCmdSectionStart = 0
        var countCmdSectionEnd = 0
        val loadJsUrl = jsList.map {
            if(
                it.startsWith(settingSectionStart)
                && it.endsWith(settingSectionStart)
            ) countSettingSectionStart++
            if(
                it.startsWith(settingSectionEnd)
                && it.endsWith(settingSectionEnd)
            ) countSettingSectionEnd++
            if(
                it.startsWith(commandSectionStart)
                && it.endsWith(commandSectionStart)
            ) countCmdSectionStart++
            if(
                it.startsWith(commandSectionEnd)
                && it.endsWith(commandSectionEnd)
            ) countCmdSectionEnd++
            if(
                countSettingSectionStart > 0
                && countSettingSectionEnd == 0
            ) "$it;"
            else if(
                countCmdSectionStart > 0
                && countCmdSectionEnd == 0
            ) "$it;"
            else it
        }.map {
            val trimJsRow = it
                .trim(' ')
                .trim('\t')
                .trim(' ')
                .trim('\t')
            if(
                trimJsRow.startsWith("//")
            ) return@map String()
            trimJsRow
        }.joinToString(" ")
            .let {
                ScriptPreWordReplacer.replace(
                    it,
                    execJsPath,
                    recentAppDirPath,
                    fannelDirName,
                    scriptFileName
                )
            }.let {
                var loadJsUrlSource = it
                setReplaceVariableMap?.forEach {
                    val replaceVariable = "\${${it.key}}"
                    val replaceString = it.value
                        .let {
                            ScriptPreWordReplacer.replace(
                                it,
                                execJsPath,
                                recentAppDirPath,
                                fannelDirName,
                                scriptFileName
                            )
                        }
                    loadJsUrlSource = loadJsUrlSource.replace(
                        replaceVariable,
                        replaceString
                    )
                }
                loadJsUrlSource
            }
        if(
            loadJsUrl.isEmpty()
            || loadJsUrl.isBlank()
        ) return null
        return "javascript:(function() { ${loadJsUrl} })();"
    }
}