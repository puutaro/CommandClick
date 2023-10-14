package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import java.io.File

object SetVariableTyper {

    private val filePrefix = EditSettings.filePrefix
    private val setVariableTypesConfigPathSrc = "${UsePath.fannelSettingVariablsDirPath}/${UsePath.setVariableTypesConfig}"

    fun makeRecordNumToSetVariableMaps(
        setVariableTypeList: List<String>?,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?
    ): Map<Int, Map<String, String>>? {
        if(
            setVariableTypeList == null
        ) return null
        val usedRecordNumSet = mutableSetOf<Int>()
        val setVariableTypeListLength = setVariableTypeList.size - 1
        if(
            setVariableTypeListLength < 0
        ) return null
        return (0..setVariableTypeListLength).map {
            val currentFetchSetVariableType = setVariableTypeList[it]
            val currentFetchSetVariableTypeLength = currentFetchSetVariableType.length
            val equalIndex = currentFetchSetVariableType.indexOf('=')
            if(equalIndex == -1) {
                return null
            }
            val variableNameAddType = currentFetchSetVariableType.substring(
                0, equalIndex
            )
            val variableNameAddTypeLength = variableNameAddType.length;
            val colonIndex = variableNameAddType.indexOf(':')
            if(colonIndex == -1) {
                return null
            }
            val variableName = variableNameAddType.substring(
                0, colonIndex
            )
            val variableType = variableNameAddType.substring(
                colonIndex+1, variableNameAddTypeLength
            )
            val variableTypeValue = currentFetchSetVariableType.substring(
                equalIndex + 1, currentFetchSetVariableTypeLength
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
                        to variableType,
                SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
                        to variableTypeValue,
            )
        }.toMap()
    }


    fun makeSetVariableTypeList(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        currentAppDirPath: String,
        fannelDirName: String,
        currentShellFileName: String,
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
            makeSetVariableValueFromFile(
                currentAppDirPath,
                currentShellFileName,
                fannelDirName
            )
        }?.joinToString(",")
            ?.split(',')
            ?.filter { it.isNotEmpty() }
    }

    private fun makeSetVariableValueFromFile(
        currentAppDirPath: String,
        currentShellFileName: String,
        fannelDirName: String
    ): String {
        val setVariableTypesConfigPath =
            ScriptPreWordReplacer.replace(
                setVariableTypesConfigPathSrc
                    .removePrefix(
                        filePrefix
                    ),
                currentAppDirPath,
                fannelDirName,
                currentShellFileName,
            )
        val setVariableTypesConfigObj = File(setVariableTypesConfigPath)
        val setVariableTypesConfigDirPath = setVariableTypesConfigObj.parent
            ?: String()
        val setVariableTypesConfigName = setVariableTypesConfigObj.name
        return SettingFile.read(
            setVariableTypesConfigDirPath,
            setVariableTypesConfigName
        )
    }
}