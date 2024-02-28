package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import java.io.File

object SetReplaceVariabler {

    private val importPreWord = SettingFileVariables.importPreWord

    fun makeSetReplaceVariableMap(
        context: Context?,
        settingVariableList: List<String>?,
        currentAppDirPath: String,
        currentScriptFileName: String,
    ): Map<String, String>? {
        if(
            settingVariableList.isNullOrEmpty()
        ) return null

        val readSharePrefMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name to currentAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name to currentScriptFileName,
        )
        val noImportRepValMap = execMakeSetReplaceVariableMap(
            context,
            settingVariableList,
            readSharePrefMap,
            null,
            false
        )
        if(
            noImportRepValMap.isNullOrEmpty()
        ) return null
        if(
            !noImportRepValMap.containsKey(importPreWord)
        ) return noImportRepValMap
        return execMakeSetReplaceVariableMap(
            context,
            settingVariableList,
            readSharePrefMap,
            noImportRepValMap,
            true
        )
    }

    private fun execMakeSetReplaceVariableMap(
        context: Context?,
        settingVariableList: List<String>?,
        readSharePrefMap: Map<String, String>,
        noImportRepValMap: Map<String, String>?,
        onImport: Boolean
    ): Map<String, String>? {
        if(
            settingVariableList.isNullOrEmpty()
        ) return null
        val setReplaceVariableMapBeforeRecursiveReplace = ListSettingVariableListMaker.makeFromSettingVariableList(
            CommandClickScriptVariable.SET_REPLACE_VARIABLE,
            readSharePrefMap,
            noImportRepValMap,
            settingVariableList,
            onImport
        ).joinToString(",")
            .let {
                convertReplaceVariableConToMap(
                    it
                )
            }
        return recursiveReplaceForReplaceVariableMap(
            context,
            setReplaceVariableMapBeforeRecursiveReplace
        )
    }

    private fun recursiveReplaceForReplaceVariableMap(
        context: Context?,
        setReplaceVariableMapBeforeRecursiveReplace : Map<String, String>?
    ): Map<String, String>? {
        val firstSetVariableMapStringList = setReplaceVariableMapBeforeRecursiveReplace
            ?.map { "${it.key}\t${it.value}"}
            ?: return null
        val firstSetVariableMapStringListSize = firstSetVariableMapStringList.size
        var lastSetVariableMapStringList = firstSetVariableMapStringList
        (0 until firstSetVariableMapStringListSize).forEach {
            val valRepList = lastSetVariableMapStringList.get(it).split("\t")
            if(valRepList.size != 2) {
                LogSystems.stdErr(
                    context,
                    "not found '=': " +
                            lastSetVariableMapStringList.joinToString("\t")
                )
                return null
            }

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
            if(valRepList.size != 2) {
                LogSystems.stdErr(
                    context,
                    "not found pair: " +
                            lastSetVariableMapStringList.joinToString("\t")
                )
                return null
            }
            valRepList.first() to valRepList.last()
        }.toMap()
    }

    private fun convertReplaceVariableConToMap(
        replaceVariableCon: String?,
    ): Map<String, String>? {
        return replaceVariableCon?.split(',')
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
                ) setTargetVariableValueList.filterIndexed{
                        index, _ -> index >= 1
                }.joinToString("=")
                    .let { QuoteTool.trimBothEdgeQuote(it)}
                else return null
                replaceVariableName to replaceString
            }?.toMap()
    }

    fun execReplaceByReplaceVariables(
        replacingContents: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        recentAppDirPath: String,
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
                        scriptFileName
                    )
                }
            loadJsUrlSource = loadJsUrlSource.replace(
                replaceVariable,
                replaceString
            )
        }
        return ScriptPreWordReplacer.replace(
            loadJsUrlSource,
            recentAppDirPath,
            scriptFileName
        )
    }


    fun makeSetReplaceVariableMapFromSubFannel(
        context: Context?,
        currentSubFannelPath: String
    ): Map<String, String>? {
        val mainFannlePath = CcPathTool.getMainFannelFilePath(
            currentSubFannelPath
        )
        val currentMainFannelPathObj = File(mainFannlePath)
        if(!currentMainFannelPathObj.isFile) {
            LogSystems.stdWarn("not found file: ${mainFannlePath}")
            return null
        }
        val currentAppDirPath = currentMainFannelPathObj.parent
            ?: let {
                LogSystems.stdWarn("not found dir: ${mainFannlePath}")
                return null
            }
        val mainFannelName = currentMainFannelPathObj.name
        val mainFannelConList = ReadText(
            mainFannlePath
        ).readText().let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                mainFannelName
            )
        }.split("\n")
        val languageType = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String

        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            mainFannelConList,
            settingSectionStart,
            settingSectionEnd
        )

        return makeSetReplaceVariableMap(
            context,
            settingVariableList,
            currentAppDirPath,
            mainFannelName,
        )
    }

    fun getReplaceVariablesTsv(
        context: Context?,
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
            LogSystems.stdErr(
                context,
                "fannel dir not found: ${currentPath}"
            )
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
                context,
                "replace variable tsv not found: ${replaceVariablesTsvPath}"
            )
            return String()
        }
        return ReadText(
            replaceVariablesTsvPathObj.absolutePath
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
