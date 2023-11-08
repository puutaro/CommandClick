package com.puutaro.commandclick.util

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.import.JsImportManager
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
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
        ) as String
        val scriptFileName = jsFileObj.name
        val fannelDirName = CcPathTool.makeFannelDirName(
            scriptFileName
        )
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
                recordNumToMapNameValueInSettingHolder,
                recentAppDirPath,
                fannelDirName,
                scriptFileName
            )
        makeReplaceVariableTableTsv(
            setReplaceVariableMap,
            recentAppDirPath,
            fannelDirName,
            scriptFileName,
            )

        val setReplaceVariableCompleteMap = makeReplaceVariableTableTsvForJsImport(
            recentAppDirPath,
            fannelDirName,
            setReplaceVariableMap,
        )

        var countSettingSectionStart = 0
        var countSettingSectionEnd = 0
        var countCmdSectionStart = 0
        var countCmdSectionEnd = 0
        val loadJsUrl = jsList.map {
            val afterJsImport = JsImportManager.replace(
                context,
                it,
                execJsPath,
                setReplaceVariableCompleteMap
            )
            if(
                afterJsImport.startsWith(settingSectionStart)
                && afterJsImport.endsWith(settingSectionStart)
            ) countSettingSectionStart++
            if(
                afterJsImport.startsWith(settingSectionEnd)
                && afterJsImport.endsWith(settingSectionEnd)
            ) countSettingSectionEnd++
            if(
                afterJsImport.startsWith(commandSectionStart)
                && afterJsImport.endsWith(commandSectionStart)
            ) countCmdSectionStart++
            if(
                afterJsImport.startsWith(commandSectionEnd)
                && afterJsImport.endsWith(commandSectionEnd)
            ) countCmdSectionEnd++
            if(
                countSettingSectionStart > 0
                && countSettingSectionEnd == 0
            ) "$afterJsImport;"
            else if(
                countCmdSectionStart > 0
                && countCmdSectionEnd == 0
            ) "$afterJsImport;"
            else afterJsImport
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
                    recentAppDirPath,
                    fannelDirName,
                    scriptFileName
                )
            }.let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableCompleteMap,
                    recentAppDirPath,
                    fannelDirName,
                    scriptFileName
                )
            }
        if(
            loadJsUrl.isEmpty()
            || loadJsUrl.isBlank()
        ) return null
        return makeLastJsCon(loadJsUrl)
    }


    fun makeFromContents (
        jsList: List<String>
    ):String? {
        val commentOutMark = "//"
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
        ) as String
        val recordNumToMapNameValueInSettingHolder = RecordNumToMapNameValueInHolder.parse(
            jsList,
            settingSectionStart,
            settingSectionEnd,
            true
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMap(
                recordNumToMapNameValueInSettingHolder,
                String(),
                String(),
                String()
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
                    String(),
                    String(),
                    String()
                )
            }.let {
                var loadJsUrlSource = it
                setReplaceVariableMap?.forEach {
                    val replaceVariable = "\${${it.key}}"
                    val replaceString = it.value
                        .let {
                            ScriptPreWordReplacer.replace(
                                it,
                                String(),
                                String(),
                                String()
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
        return makeLastJsCon(loadJsUrl)
    }

    private fun makeLastJsCon(
        loadJsUrl: String
    ): String {
        return "javascript:(function() { " +
                    "try{${loadJsUrl}} catch(error){" +
                        "const errMessage = error.message;" +
                        "if(errMessage.includes(\"exitZero\")){return;};" +
                        "jsToast.short(`ERROR ${'$'}{errMessage}`);" +
                        "jsFileSystem.errLog(errMessage)" +
                    "};" +
                "})();"
    }

    private fun makeReplaceVariableTableTsv(
        setReplaceVariableMap:  Map<String, String>?,
        recentAppDirPath: String,
        fannelDirName: String,
        scriptFileName: String,
    ){
        if(setReplaceVariableMap.isNullOrEmpty()) return
        val preWordTsvTable = ScriptPreWordReplacer.makeTsvTable(
            recentAppDirPath,
            scriptFileName,
        )
        val replaceVariableTable = setReplaceVariableMap.entries.map {
            val replacedVal = ScriptPreWordReplacer.replace(
                it.value,
                recentAppDirPath,
                fannelDirName,
                scriptFileName,
            )
            "${it.key}\t${replacedVal}"
        }.joinToString("\n")
        val fannelSettingsDirPath = ScriptPreWordReplacer.replace(
            UsePath.fannelSettingVariablsDirPath,
            recentAppDirPath,
            fannelDirName,
            scriptFileName,
        )
        FileSystems.writeFile(
            fannelSettingsDirPath,
            UsePath.replaceVariablesTsv,
            "${preWordTsvTable}\n${replaceVariableTable}"
        )
    }

    private fun makeReplaceVariableTableTsvForJsImport(
        recentAppDirPath: String,
        fannelDirName: String,
        setReplaceVariableMap:  Map<String, String>?,
    ): Map<String, String>? {
        if(
            !setReplaceVariableMap.isNullOrEmpty()
        ) return setReplaceVariableMap
        val replaceVariableTsvCon = SetReplaceVariabler.getReplaceVariablesTsv(
            "$recentAppDirPath/$fannelDirName"
        )
        if(replaceVariableTsvCon.isEmpty()) return null
        return replaceVariableTsvCon.split("\n").map {
            val keyValueList = it.split("\t")
            if(
                keyValueList.size < 2
            ) return@map String() to String()
            val key = keyValueList.firstOrNull() ?: String()
            val value = keyValueList.lastOrNull() ?: String()
            key to value
        }.toMap().filterKeys { it.isNotEmpty() }
    }
}
