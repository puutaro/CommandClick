package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.util.BothEdgeQuote

object SetReplaceVariabler {

    fun makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?
    ): Map<String, String>? {
        val firstSetVariableMapStringList = execMakeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolder
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
