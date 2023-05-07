package com.puutaro.commandclick.util

import android.content.Context
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.import.CcImportManager
import java.io.File

object JavaScriptLoadUrl {
    fun make (
        context: Context?,
        execJsPath: String,
        jsListSource: List<String>? = null
    ):String? {
        val commentOutMark = "//"
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
            val afterCcImport = CcImportManager.replace(
                context,
                it,
                execJsPath
            )
            if(
                afterCcImport.startsWith(settingSectionStart)
                && afterCcImport.endsWith(settingSectionStart)
            ) countSettingSectionStart++
            if(
                afterCcImport.startsWith(settingSectionEnd)
                && afterCcImport.endsWith(settingSectionEnd)
            ) countSettingSectionEnd++
            if(
                afterCcImport.startsWith(commandSectionStart)
                && afterCcImport.endsWith(commandSectionStart)
            ) countCmdSectionStart++
            if(
                afterCcImport.startsWith(commandSectionEnd)
                && afterCcImport.endsWith(commandSectionEnd)
            ) countCmdSectionEnd++
            if(
                countSettingSectionStart > 0
                && countSettingSectionEnd == 0
            ) "$afterCcImport;"
            else if(
                countCmdSectionStart > 0
                && countCmdSectionEnd == 0
            ) "$afterCcImport;"
            else afterCcImport
        }.joinToString("\n").split("\n").map {
            val trimJsRow = it
                .trim(' ')
                .trim('\t')
                .trim(' ')
                .trim('\t')
            if(
                trimJsRow.startsWith(commentOutMark)
            ) return@map String()
            if(
                !trimJsRow.contains(commentOutMark)
            ) return@map trimJsRow
            if(
                !trimJsRow.endsWith(";")
            ) return@map trimJsRow
            val trimJsRowList = trimJsRow.split(";")
            val includeCommentOut =  trimJsRowList
                .lastOrNull()
                ?.contains(commentOutMark)
            if(
                includeCommentOut != true
            ) return@map trimJsRow
            val trimJsRowListSize = trimJsRowList.size
            val sliceTrimJsRowList = trimJsRowList.slice(
                0..trimJsRowListSize - 2
            ).joinToString(";") + ";"
            sliceTrimJsRowList
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