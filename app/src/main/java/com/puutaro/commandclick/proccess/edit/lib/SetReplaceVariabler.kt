package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import java.io.File

object SetReplaceVariabler {

    private val filePrefix = EditSettings.filePrefix
    private val setReplaceVariablesConfigPathSrc = "${UsePath.fannelSettingVariablsDirPath}/${UsePath.setReplaceVariablesConfig}"

    fun makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        currentAppDirPath: String,
        fannelDirName: String,
        currentShellFileName: String,
    ): Map<String, String>? {
        val firstSetVariableMapStringList = execMakeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolder,
            currentAppDirPath,
            currentShellFileName,
            fannelDirName
        )?.map { "${it.key}\t${it.value}"} ?: return null

        val firstSetVariableMapStringListSize = firstSetVariableMapStringList.size
        var lastSetVariableMapStringList = firstSetVariableMapStringList
        (0 until firstSetVariableMapStringListSize).forEach {
            val valRepList = lastSetVariableMapStringList.get(it).split("\t")
            if(valRepList.size != 2) return null

            val replaceVariable = "\${${valRepList.first()}}"
            val replaceString = valRepList.last()
            lastSetVariableMapStringList = lastSetVariableMapStringList.map {
                it.replace(
                    replaceVariable,
                    replaceString
                )
            }
        }
        return lastSetVariableMapStringList.map {
            val valRepList = it.split("\t")
            if(valRepList.size != 2) return null
            valRepList.first() to valRepList.last()
        }.toMap()
    }

    private fun execMakeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        currentAppDirPath: String,
        currentShellFileName: String,
        fannelDirName: String
    ): Map<String, String>? {
        return recordNumToMapNameValueInSettingHolder?.filter {
                entry ->
            entry.value?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) == CommandClickScriptVariable.SET_REPLACE_VARIABLE
        }?.map {
                entry ->
            val entryValue = entry.value
            val setTargetVariableValueBeforeTrim = entryValue?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            )
            val setTargetVariableValueSource = if(setTargetVariableValueBeforeTrim?.indexOf('"') == 0){
                setTargetVariableValueBeforeTrim.trim('"')
            } else if(setTargetVariableValueBeforeTrim?.indexOf('\'') == 0){
                setTargetVariableValueBeforeTrim.trim('\'')
            } else {
                setTargetVariableValueBeforeTrim
            } ?: return null
            if(
                !setTargetVariableValueSource.startsWith(
                    filePrefix
                )
            ) return@map setTargetVariableValueSource
            makeSetVariableValueFromFile(
                currentAppDirPath,
                currentShellFileName,
                fannelDirName
            )
        }?.joinToString(",")
            ?.split(',')
            ?.filter {
                it.isNotEmpty()
            }?.map {
                val setTargetVariableValueList =
                    it.split('=')
                val replaceVariableName =
                    setTargetVariableValueList
                        .firstOrNull()
                        ?.let {
                            QuoteTool.trimBothEdgeQuote(it)
                        } ?: return null
                val setTargetVariableValueListSize =
                    setTargetVariableValueList.size
                val replaceString = if(
                    setTargetVariableValueListSize > 1
                ) setTargetVariableValueList.slice(
                    1.. setTargetVariableValueListSize - 1
                ).firstOrNull()
                    ?.let { QuoteTool.trimBothEdgeQuote(it)}
                    ?: return null
                else return null
                replaceVariableName to replaceString
            }?.toMap()
    }

    private fun makeSetVariableValueFromFile(
        currentAppDirPath: String,
        currentShellFileName: String,
        fannelDirName: String
    ): String {
        val setReplaceVariablesConfigPath =
            ScriptPreWordReplacer.replace(
                setReplaceVariablesConfigPathSrc
                    .removePrefix(
                        filePrefix
                    ),
                currentAppDirPath,
                fannelDirName,
                currentShellFileName,
            )
        val setReplaceVariablesConfigObj = File(setReplaceVariablesConfigPath)
        val setReplaceVariableConfigDirPath = setReplaceVariablesConfigObj.parent
            ?: return String()
        val setReplaceVariableConfigName = setReplaceVariablesConfigObj.name
        return SettingFile.read(
            setReplaceVariableConfigDirPath,
            setReplaceVariableConfigName
        )
    }

    fun execReplaceByReplaceVariables(
        replacingContents: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        recentAppDirPath: String,
        fannelDirName: String,
        scriptFileName: String
    ):String {
        var loadJsUrlSource = replacingContents
        setReplaceVariableCompleteMap?.forEach {
            val replaceVariable = "\${${it.key}}"
            val replaceString = it.value
                .let {
                    ScriptPreWordReplacer.replace(
                        it,
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
        return loadJsUrlSource
    }

    fun getReplaceVariablesTsv(
        currentPath: String,
    ): String {
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val fannelDirListLength = 2
        val pathListStartAppDirName = currentPath.replace(
            "${cmdclickAppDirPath}/",
            ""
        ).split("/")
        if(
            pathListStartAppDirName.size < fannelDirListLength
        ) {
            LogSystems.stdErr("fannel dir not found: ${currentPath}")
            return String()
        }
        val fannelDirRelativePath =
            pathListStartAppDirName.take(2).joinToString("/")
        val replaceVariablesTsvRelativePath = UsePath.replaceVariablesTsvRelativePath
        val replaceVariablesTsvPath =
            listOf(
                cmdclickAppDirPath,
                fannelDirRelativePath,
                replaceVariablesTsvRelativePath
            ).joinToString("/")
        val replaceVariablesTsvPathObj = File(replaceVariablesTsvPath)
        if(
            !replaceVariablesTsvPathObj.isFile
        ) {
            LogSystems.stdErr(
                "replace variable tsv not found: ${replaceVariablesTsvPath}"
            )
            return String()
        }
        val settingVariablesDirPath = replaceVariablesTsvPathObj.parent ?: let{
            LogSystems.stdErr(
                "settingVarialesDirPath not found: ${replaceVariablesTsvPath}"
            )
            return String()
        }
        val replaceVariableTsvName = replaceVariablesTsvPathObj.name
        return ReadText(
            settingVariablesDirPath,
            replaceVariableTsvName
        ).readText()
    }

    fun getReplaceVariablesValue(
        tsvCon: String,
        targetKey: String,
    ): String {
        tsvCon.split("\n").map {
            val keyValueList = it.split("\t")
            if(keyValueList.size < 2) return String()
            val key = keyValueList.firstOrNull()
                ?: return String()
            if(
                key == targetKey
            ) {
                return keyValueList.lastOrNull()
                    ?: String()
            }
        }
        return String()
    }
}
