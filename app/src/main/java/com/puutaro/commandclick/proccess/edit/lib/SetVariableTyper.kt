package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn

object SetVariableTyper {

    fun makeRecordNumToSetVariableMaps(
        setVariableTypeList: List<String>?,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?
    ): Map<Int, Map<String, String>>? {
        if(setVariableTypeList == null) return null
        val usedRecordNumSet = mutableSetOf<Int>()
        val setVariableTypeListLength = setVariableTypeList.size -1
        if(setVariableTypeListLength < 0) return null
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
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?
    ): List<String>? {
        return recordNumToMapNameValueInSettingHolder?.filter {
                entry ->
            entry.value?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) == CommandClickShellScript.SET_VARIABLE_TYPE
        }?.map {
                entry ->
            val entryValue = entry.value
            val setTargetVariableValueBeforeTrim = entryValue?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            )
            if(setTargetVariableValueBeforeTrim?.indexOf('"') == 0){
                setTargetVariableValueBeforeTrim.trim('"')
            } else if(setTargetVariableValueBeforeTrim?.indexOf('\'') == 0){
                setTargetVariableValueBeforeTrim.trim('\'')
            } else {
                setTargetVariableValueBeforeTrim
            } ?: String()
        }?.joinToString(",")
            ?.split(',')
            ?.filter { it.isNotEmpty() }
    }
}