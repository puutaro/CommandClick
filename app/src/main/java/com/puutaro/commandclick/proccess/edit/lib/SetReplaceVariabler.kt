package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File

object SetReplaceVariabler {

    private const val importPreWord = SettingFileVariables.importPreWord

    fun makeSetReplaceVariableMap(
        context: Context?,
        settingVariableList: List<String>?,
        currentScriptFileName: String,
    ): Map<String, String>? {
        if(
            settingVariableList.isNullOrEmpty()
        ) return null

        val fannelInfoMap = mapOf(
            FannelInfoSetting.current_fannel_name.name to currentScriptFileName,
        )
        val noImportRepValMap = execMakeSetReplaceVariableMap(
            context,
            settingVariableList,
            fannelInfoMap,
            null,
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsRepval.txt").absolutePath,
//            listOf(
//                "noImportRepValMap: ${noImportRepValMap}",
//                "isImportPreWord: ${noImportRepValMap?.containsKey(importPreWord)}",
//                "execMakeSetReplaceVariableMap: ${execMakeSetReplaceVariableMap(
//                    context,
//                    settingVariableList,
//                    readSharePrefMap,
//                    noImportRepValMap,
//                    true
//                )}"
//            ).joinToString("\n\n\n")
//        )
        if(
            noImportRepValMap.isNullOrEmpty()
        ) return null
        if(
            !noImportRepValMap.containsKey(importPreWord)
        ) return noImportRepValMap
        return execMakeSetReplaceVariableMap(
            context,
            settingVariableList,
            fannelInfoMap,
            noImportRepValMap,
        )
    }

    private fun execMakeSetReplaceVariableMap(
        context: Context?,
        settingVariableList: List<String>?,
        fannelInfoMap: Map<String, String>,
        noImportRepValMap: Map<String, String>?,
    ): Map<String, String>? {
        if(
            settingVariableList.isNullOrEmpty()
        ) return null
        val setReplaceVariableMapBeforeRecursiveReplace = ListSettingVariableListMaker.makeFromSettingVariableList(
            context,
            CommandClickScriptVariable.SET_REPLACE_VARIABLE,
            fannelInfoMap,
            noImportRepValMap,
            settingVariableList,
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
        val emulatedPath = UsePath.emulatedPath
        val firstSetVariableMapStringList = setReplaceVariableMapBeforeRecursiveReplace
            ?.map { "${it.key}\t${it.value}"}
            ?: return null
        val firstSetVariableMapStringListSize = firstSetVariableMapStringList.size
        val lastSetVariableMapStringList = firstSetVariableMapStringList.toMutableList()
        (0 until firstSetVariableMapStringListSize).forEach {
            val valRepList = lastSetVariableMapStringList.get(it).split("\t")
            if(valRepList.size != 2) {
                LogSystems.stdErr(
                    context,
                    "not found '=': " +
                            valRepList.joinToString("=")
                )
                return null
            }
            val varValueLikePath = valRepList.lastOrNull() ?: String()
            val isIrregularPath =
                varValueLikePath.contains(emulatedPath)
                        && varValueLikePath.contains("=")
            if(isIrregularPath) {
                LogSystems.stdErr(
                    context,
                    "found '=' in path or key: ${valRepList.joinToString("=")}"
                )
                return null
            }

            val replaceVariable = "\${${valRepList.first()}}"
            val replaceString = valRepList.last()
            lastSetVariableMapStringList.forEachIndexed { index, value ->
                lastSetVariableMapStringList[index] = value.replace(
                    replaceVariable,
                    replaceString
                )
            }
//            lastSetVariableMapStringList = lastSetVariableMapStringList.map {
//                it.replace(
//                    replaceVariable,
//                    replaceString
//                )
//            }
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
//        recentAppDirPath: String,
        fannelName: String
    ):String {
//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        val loadJsUrlSource = buildString {
//            append(replacingContents)
//            setReplaceVariableCompleteMap?.forEach {
//                val replaceVariable = Regex("[$][{]${it.key}[}]")
//                val replaceString = it.value
////                    .let {
////                        ScriptPreWordReplacer.replace(
////                            it,
//////                        recentAppDirPath,
////                            fannelName
////                        )
////                    }
//                replace(
//                    replaceVariable,
//                    replaceString
//                )
//            }
//        }
        val builder = StringBuilder(replacingContents)
        setReplaceVariableCompleteMap?.forEach {
            val replaceVariable = "\${${it.key}}"
            val replaceString = it.value
            var index = builder.indexOf(replaceVariable)
            while (index != -1) {
                builder.replace(index, index + replaceVariable.length, replaceString)
                index = builder.indexOf(replaceVariable, index + replaceString.length)
            }
        }
        return ScriptPreWordReplacer.replace(
            builder.toString(),
//            recentAppDirPath,
            fannelName
        )
//        var loadJsUrlSource = replacingContents
//        setReplaceVariableCompleteMap?.forEach {
//            val replaceVariable = "\${${it.key}}"
//            val replaceString = it.value
//                .let {
//                    ScriptPreWordReplacer.replace(
//                        it,
////                        recentAppDirPath,
//                        fannelName
//                    )
//                }
//            loadJsUrlSource = loadJsUrlSource.replace(
//                replaceVariable,
//                replaceString
//            )
//        }
//        return ScriptPreWordReplacer.replace(
//            loadJsUrlSource,
////            recentAppDirPath,
//            fannelName
//        )
    }


    fun makeSetReplaceVariableMapFromSubFannel(
        context: Context?,
        currentSubFannelPath: String
    ): Map<String, String>? {
        if(
            currentSubFannelPath.isEmpty()
        ) return null
        val mainFannlePath = CcPathTool.getMainFannelFilePath(
            currentSubFannelPath
        )
        val currentMainFannelPathObj = File(mainFannlePath)
        if(!currentMainFannelPathObj.isFile) {
            LogSystems.stdWarn("not found file: ${mainFannlePath}")
            return null
        }
//        val currentAppDirPath = currentMainFannelPathObj.parent
//            ?: let {
//                LogSystems.stdWarn("not found dir: ${mainFannlePath}")
//                return null
//            }
        val mainFannelName = currentMainFannelPathObj.name
        val mainFannelConList = ReadText(
            mainFannlePath
        ).readText().let {
            ScriptPreWordReplacer.replace(
                it,
//                currentAppDirPath,
                mainFannelName
            )
        }.split("\n")
//        val languageType = LanguageTypeSelects.JAVA_SCRIPT
//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//                languageType
//            )
        val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
        val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
//        val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//
//        val settingSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String

        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            mainFannelConList,
            settingSectionStart,
            settingSectionEnd
        )

        return makeSetReplaceVariableMap(
            context,
            settingVariableList,
//            currentAppDirPath,
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
