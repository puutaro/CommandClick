package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.edit.TypeVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.AlterConfig
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object SetVariableTyper {

    private val setValSeparator = '|'
    private val typeSeparator = '!'
    private val filePrefix = EditSettings.filePrefix
    private val setVariableTypesConfigPathSrc = "${UsePath.fannelSettingVariablsDirPath}/${UsePath.setVariableTypesConfig}"
    private val noIndexTypeList = TypeVariable.noIndexTypeList

    fun makeRecordNumToSetVariableMaps(
        context: Context?,
        setVariableTypeList: List<String>?,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?,
        replaceVariableMap: Map<String, String>?,
    ): Map<Int, Map<String, String>>? {
        if(
            setVariableTypeList == null
        ) return null
        val usedRecordNumSet = mutableSetOf<Int>()
        val setVariableTypeListLength = setVariableTypeList.size - 1
        if(
            setVariableTypeListLength < 0
        ) return null
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context as Context)
        )
        return (0..setVariableTypeListLength).map {
            val currentFetchSetVariableType = setVariableTypeList[it]
            val currentFetchSetVariableTypeLength = currentFetchSetVariableType.length
            val equalIndex = currentFetchSetVariableType.indexOf('=')
            if(equalIndex == -1) {
                LogSystems.stdErr(
                    context,
                    "not found '=': " +
                            currentFetchSetVariableType
                )
                return null
            }
            val variableNameAddType = currentFetchSetVariableType.substring(
                0, equalIndex
            )
            val variableNameAddTypeLength = variableNameAddType.length
            val colonIndex = variableNameAddType.indexOf(':')
            if(colonIndex == -1) {
                LogSystems.stdErr(
                    context,
                    "not found ':': " +
                            currentFetchSetVariableType
                )
                return null
            }
            val variableName = variableNameAddType.substring(
                0, colonIndex
            )
            val variableType = variableNameAddType.substring(
                colonIndex+1, variableNameAddTypeLength
            )
            val variableTypeValueSrc = currentFetchSetVariableType.substring(
                equalIndex + 1, currentFetchSetVariableTypeLength
            )
            val variableTypeValue = AlterToolForSetValType.makeVariableTypeValueByAlter(
                context,
                variableTypeValueSrc,
                busyboxExecutor,
                replaceVariableMap,
            )
            val hitRecordNumList = recordNumToMapNameValueInCommandHolder?.filterValues {
                    keyValueMap ->
                keyValueMap?.get(
                    RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
                ) == variableName
            }?.keys?.toList()
            val aliveHitRecordNumList = hitRecordNumList?.filter {
                !usedRecordNumSet.contains(it)
            }
            val aliveHitRecordNumFirst = aliveHitRecordNumList?.firstOrNull() ?: -1
            usedRecordNumSet.add(aliveHitRecordNumFirst)
            aliveHitRecordNumFirst to mapOf(
                SetVariableTypeColumn.VARIABLE_NAME.name
                        to variableName,
                SetVariableTypeColumn.VARIABLE_TYPE.name
                        to VisibleTool.reflectVisibleToType(
                                variableType,
                                variableTypeValue,
                            ),
                SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
                        to VisibleTool.reflectVisibleToTypeValue(
                            variableTypeValue
                        )
            )
        }.toMap().filterKeys { it >= 0 }
    }

    fun makeSetVariableTypeList(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        currentAppDirPath: String,
        currentShellFileName: String,
        setReplaceVariableMap: Map<String, String>?
    ): List<String>? {
        return recordNumToMapNameValueInSettingHolder?.filter {
                entry ->
            entry.value?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) == CommandClickScriptVariable.SET_VARIABLE_TYPE
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
            } ?: String()
            if(
                !setTargetVariableValueSource.startsWith(
                    filePrefix
                )
            ) return@map setTargetVariableValueSource
            makeSetVariableValueFromConfigFile(
                currentAppDirPath,
                currentShellFileName,
                setReplaceVariableMap
            )
        }?.joinToString(",")
            ?.let {
                QuoteTool.splitBySurroundedIgnore(
                    QuoteTool.trimBothEdgeQuote(it),
                    ','
                )
            }?.filter { it.isNotEmpty() }
    }

    private fun makeSetVariableValueFromConfigFile(
        currentAppDirPath: String,
        currentShellFileName: String,
        setReplaceVariableMap: Map<String, String>?
    ): String {
        return makeSetVariableValueFromFilePath(
            setVariableTypesConfigPathSrc.removePrefix(
                filePrefix
            ),
            currentAppDirPath,
            currentShellFileName,
            setReplaceVariableMap,
        )
    }

    private fun makeSetVariableValueFromFilePath(
        configFilePath: String,
        currentAppDirPath: String,
        currentShellFileName: String,
        setReplaceVariableMap: Map<String, String>?
    ): String {
        val setVariableTypesConfigPath =
            ScriptPreWordReplacer.replace(
                configFilePath,
                currentAppDirPath,
                currentShellFileName,
            )
        return SettingFile.read(
            File(currentAppDirPath, currentShellFileName).absolutePath,
            setVariableTypesConfigPath,
            setReplaceVariableMap,
        )
    }

    fun getCertainSetValIndexMap(
        currentSetVariableValue: String?,
        currentComponentIndex: Int
    ): Map<String, String>? {
        return currentSetVariableValue?.let {
            if(
                it.contains(
                   setValSeparator
                )
            ) return@let QuoteTool.splitBySurroundedIgnore(
                it,
                setValSeparator
            )
                .getOrNull(currentComponentIndex).let {
                QuoteTool.trimBothEdgeQuote(it)
            }
            QuoteTool.trimBothEdgeQuote(it)
        }?.let {
            CmdClickMap.createMap(
                it,
                typeSeparator
            )
        }?.toMap()
    }

    private object VisibleTool {
        const val visibleOffValue = "OFF"

        fun howDisableVisible(
            variableTypeValue: String,
        ): Boolean {
            val onRemoveKey = SetValUniqKey.ON_PUT.key
            val visibleOffStr = "${onRemoveKey}=${visibleOffValue}"
            val visibleOffOnTheWayStr = "${typeSeparator}${onRemoveKey}=${visibleOffValue}"
            return variableTypeValue.startsWith(visibleOffStr)
                    || variableTypeValue.contains(visibleOffOnTheWayStr)
        }
        fun reflectVisibleToType(
            variableType: String,
            variableTypeValue: String,
        ): String {
            val typeConcat = ":"
            val noVisibleIndexList =
                QuoteTool.splitBySurroundedIgnore(
                    variableTypeValue,
                    setValSeparator
                ).mapIndexed { index, s ->
                    val disableVisible = howDisableVisible(s)
                    when(disableVisible){
                        true -> index
                        false -> -1
                    }
                }.filter { it >= 0 }
            var currentValidIndexSeed = 0
            return variableType.split(typeConcat).filter {
                val isNoIndexType =
                    noIndexTypeList.contains(it)
                if(
                    isNoIndexType
                ) return@filter true
                val isVisibleIndex =
                    !noVisibleIndexList.contains(currentValidIndexSeed)
                currentValidIndexSeed++
                isVisibleIndex
            }.joinToString(typeConcat)
        }

        fun reflectVisibleToTypeValue(
            variableTypeValue: String,
        ): String {
            return QuoteTool.splitBySurroundedIgnore(
                variableTypeValue,
                setValSeparator
            ).filter {
                val enableVisible = !howDisableVisible(it)
                enableVisible
            }.joinToString(setValSeparator.toString())
        }
    }
}

private object AlterToolForSetValType {

    private const val alterKeyName = AlterConfig.alterKeyName
    private const val setValSeparator = '|'
    private const val typeSeparator = '!'
    private const val ifArgsSeparator = '&'

    fun makeVariableTypeValueByAlter(
        context: Context,
        variableTypeValue: String,
        busyboxExecutor: BusyboxExecutor?,
        replaceVariableMap: Map<String, String>?
    ): String {
        if(
            busyboxExecutor == null
        ) return variableTypeValue
        val varValueElList = QuoteTool.splitBySurroundedIgnore(
            variableTypeValue,
            setValSeparator
        )
        val alterKeyEqualStr = "${alterKeyName}="
        return varValueElList.map {
                varValueEl ->
            val typeValueList = QuoteTool.splitBySurroundedIgnore(
                varValueEl,
                typeSeparator
            )
            val alterTypeValue = typeValueList.firstOrNull {
                it.startsWith(alterKeyEqualStr)
            }
            if(
                alterTypeValue.isNullOrEmpty()
            ) return@map varValueEl
            val alterValue = QuoteTool.trimBothEdgeQuote(
                alterTypeValue.removePrefix(
                    alterKeyEqualStr
                ).trim()
            )
            val alterMap = makeAlterMap(
                alterValue,
                replaceVariableMap
            )
            val shellIfOutput = getShellIfOutput(
                context,
                alterMap,
                replaceVariableMap,
                busyboxExecutor,
            )
            val disableAlter = shellIfOutput.isEmpty()
            if(
                disableAlter
            ) return@map QuoteTool.splitBySurroundedIgnore(
                varValueEl,
                typeSeparator
            ).filter {
                !it.startsWith(alterKeyEqualStr)
            }.joinToString(typeSeparator.toString())
            val updateTypeValue = execAlter(
                typeValueList,
                alterMap,
                alterValue,
                shellIfOutput,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_makeVariableTypeValueByalter.txt").absolutePath,
//                listOf(
//                    "alterTypeValue: ${alterTypeValue}",
//                    "alterValue: ${alterValue}",
//                    "alterMap: ${alterMap}",
//                    "updateTypeValue: ${updateTypeValue}",
//                ).joinToString("\n\n-------\n")
//            )
            updateTypeValue
        }.joinToString(setValSeparator.toString())
    }

    private fun execAlter(
        typeValueList: List<String>,
        alterMapSrc: Map<String, String>,
        alterValue: String,
        shellIfOutput: String,
    ): String {
        val alterIfKeyList =
            AlterConfig.IfKey.values().map{ it.key }
        val alterMap =
            alterMapSrc +
                    CmdClickMap.createMap(
                        shellIfOutput,
                        typeSeparator
                    )
        val alterMapKeyList = alterMap.keys
        val currentTypeValueWithAlterKeyRemove = typeValueList.map {
                typeValue ->
            val alterKey = alterMapKeyList.firstOrNull {
                typeValue.startsWith("${it}=")
            }
            if(
                alterKey.isNullOrEmpty()
            ) return@map typeValue
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "setValMap_exec_alter.txt").absolutePath,
//                listOf(
//                    "alterMapKeyList: ${alterMapKeyList}",
//                    "typeValue: ${typeValue}",
//                    "alterKey: ${alterKey}",
//                ).joinToString("\n\n-------\n")
//            )
            String()
        }.joinToString(typeSeparator.toString())
        val alterValueExcludeIfKey = QuoteTool.splitBySurroundedIgnore(
            alterValue,
            typeSeparator
        ).filter {
            !alterIfKeyList.contains(it)
        }.joinToString(typeSeparator.toString())
        return listOf(
            currentTypeValueWithAlterKeyRemove,
            alterValueExcludeIfKey,
            shellIfOutput,
        ).joinToString(typeSeparator.toString())
    }

    private fun getShellIfOutput(
        context: Context,
        alterMap: Map<String, String>,
        replaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor,
    ): String {
        val shellIfCon = makeShellIfCon(
            context,
            alterMap,
            replaceVariableMap,
        )
//            val repValHashMap = replaceVariableMap?.let {
//                HashMap(it)
//            }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_shellIf.txt").absolutePath,
//            listOf(
//                "alterMap: ${alterMap}",
//                "shellIfCon: ${shellIfCon}",
//                "alterMap: ${alterMap}",
//                "shellIfCon: ${shellIfCon}",
//                "val: ${busyboxExecutor.getCmdOutput(
//                    shellIfCon,
//                )}"
//            ).joinToString("\n\n\n")
//        )
        return busyboxExecutor.getCmdOutput(
            shellIfCon,
        ).trim()
    }

    private fun makeAlterMap(
        alterValue: String,
        replaceVariableMap: Map<String, String>?
    ): Map<String, String> {
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            alterValue,
            replaceVariableMap,
            String(),
            String()
        ).let {
            CmdClickMap.createMap(
                it,
                typeSeparator,
            ).toMap()
        }
    }

    private fun makeShellIfCon(
        context: Context,
        alterMap: Map<String, String>?,
        replaceVariableMap: Map<String, String>?,
    ): String {
        if(
            alterMap.isNullOrEmpty()
        ) return String()
        val shellIfCon = alterMap.get(
            AlterConfig.IfKey.SHELL_IF_CON.key
        )
        if(
            !shellIfCon.isNullOrEmpty()
        ) return SetReplaceVariabler.execReplaceByReplaceVariables(
            shellIfCon,
            replaceVariableMap,
            String(),
            String(),
        )
        val shellPath = alterMap.get(
            AlterConfig.IfKey.SHELL_IF_PATH.key
        ) ?: return String()
        val extraRepValMap = CmdClickMap.createMap(
            alterMap.get(
                AlterConfig.IfKey.IF_ARGS.key
            ),
            ifArgsSeparator
        ).toMap()
        return ShellMacroHandler.makeShellCon(
            context,
            shellPath,
            replaceVariableMap,
            extraRepValMap,
        )
    }
}

private enum class SetValUniqKey(
    val key: String,
) {
    ON_PUT("onPut"),
}