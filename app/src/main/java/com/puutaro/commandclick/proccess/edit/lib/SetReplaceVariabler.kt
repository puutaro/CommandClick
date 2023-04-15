package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.util.BothEdgeQuote

object SetReplaceVariabler {

    fun makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?
    ): Map<String, String>? {
        val firstSetVariableMap = execMakeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolder
        )
        return firstSetVariableMap?.map {
            val replaceVariableName = it.key
            val replaceString = it.value.let {
                var innerExecCmd = it
                firstSetVariableMap.forEach {
                    val replaceVariable = "\${${it.key}}"
                    val replaceString = it.value
                    innerExecCmd = innerExecCmd.replace(
                        replaceVariable,
                        replaceString
                    )
                }
                innerExecCmd
            }
            replaceVariableName to replaceString
        }?.toMap()
    }

    private fun execMakeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?
    ): Map<String, String>? {
        return recordNumToMapNameValueInSettingHolder?.filter {
                entry ->
            entry.value?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) == CommandClickShellScript.SET_REPLACE_VARIABLE
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
            } ?: return null
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
                            BothEdgeQuote.trim(it)
                        } ?: return null
                val setTargetVariableValueListSize =
                    setTargetVariableValueList.size
                val replaceString = if(
                    setTargetVariableValueListSize > 1
                ) setTargetVariableValueList.slice(
                    1.. setTargetVariableValueListSize - 1
                ).firstOrNull()
                    ?.let { BothEdgeQuote.trim(it)}
                    ?: return null
                else return null
                replaceVariableName to replaceString
            }?.toMap()
    }
}
