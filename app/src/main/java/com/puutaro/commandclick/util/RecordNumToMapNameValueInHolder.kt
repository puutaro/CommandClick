package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.util.str.QuoteTool

object RecordNumToMapNameValueInHolder {

    val valNameKey = RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
    val valValueKey = RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name

    fun parse(
        scriptContentsList: List<String>?,
        startHolderName: String,
        endHolderName: String,
    ): Map<String, String>? {
        if(
            scriptContentsList.isNullOrEmpty()
        ) return null
        val commandPromptStartNum = scriptContentsList.indexOf(
            startHolderName
        )
        val commandPromptEndNum = scriptContentsList.indexOf(
            endHolderName
        )
        val substituteCmdStartEndContentList = if(
            commandPromptStartNum > 0
            && commandPromptEndNum > 0
            && commandPromptStartNum < commandPromptEndNum
        ) {
            scriptContentsList.slice(
                commandPromptStartNum..commandPromptEndNum
            )
        } else {
            return null
        }
        val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
        return substituteCmdStartEndContentList.map {
                substituteCmdStartEndContentStr ->
            val result = cmdclickVariableRegex.containsMatchIn(
                substituteCmdStartEndContentStr
            )
            if(
                !result
            ) return@map String() to String()
            makeResultEntryMap(
                substituteCmdStartEndContentStr,
            )
        }.filter { it.first.isNotEmpty() }.toMap().toMutableMap()
    }
    fun parse2(
        scriptContentsList: List<String>,
        startHolderName: String,
        endHolderName: String,
        onForSetting: Boolean = false,
    ): Map<Int, Map<String, String>?>? {
        if(
            scriptContentsList.isEmpty()
        ) return null
        val commandPromptStartNum = scriptContentsList.indexOf(
            startHolderName
        )
        val commandPromptEndNum = scriptContentsList.indexOf(
            endHolderName
        )
        val substituteCmdStartEndContentList = if(
            commandPromptStartNum > 0
            && commandPromptEndNum > 0
            && commandPromptStartNum < commandPromptEndNum
        ) {
            scriptContentsList.slice(
                commandPromptStartNum..commandPromptEndNum
            )
        } else {
            return null
        }
        val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
//        val hitedList = mutableSetOf<String>()
//        val filteredSubstituteCmdStartEndContentList = if(onForSetting) {
//            substituteCmdStartEndContentList.filter {
//                val isMultipleVariables =
//                    it.startsWith("${CommandClickScriptVariable.SET_VARIABLE_TYPE}=")
//                            || it.startsWith("${CommandClickScriptVariable.SET_REPLACE_VARIABLE}=")
//                val isNotContainHitedList = !hitedList.contains(it)
//                if (isNotContainHitedList && !isMultipleVariables) hitedList.add(it)
//                (isNotContainHitedList || isMultipleVariables)
//            }
//        } else {
//            substituteCmdStartEndContentList
//        }
        return substituteCmdStartEndContentList.mapIndexed {
                listOrderNum, substituteCmdStartEndContentStr ->
            val recordNum = commandPromptStartNum + listOrderNum
            val result = cmdclickVariableRegex.containsMatchIn(
                substituteCmdStartEndContentStr
            )
            makeResultEntryMap2(
                result,
                recordNum,
                substituteCmdStartEndContentStr,
                substituteCmdStartEndContentList,
                onForSetting,
            )
        }.let {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "irecNumtoMap.txt").absolutePath,
//                listOf(
//                    "substituteCmdStartEndContentList: ${substituteCmdStartEndContentList}",
//                    "src: ${it}",
//                    "leaveAfterValNameKey: ${leaveAfterValNameKey(it)}",
//                ).joinToString("\n\n") + "\n-----\n\n"
//            )
            leaveAfterValNameKey(
                it
            )
        }.toMap().filterValues {
            it != null
        }
    }

    private fun leaveAfterValNameKey(
        recordNumToMapNameValueListInHolder: List<Pair<Int, Map<String, String>?>>
    ): List<Pair<Int, Map<String, String>?>> {
        val prefixList = mutableListOf<String>()
        val blankPair = -1 to null
        return recordNumToMapNameValueListInHolder
            .reversed()
            .map {
                    recordNumToMapNameValueMap ->
                val recordNum = recordNumToMapNameValueMap.first
                if(
                    recordNum < 0
                ) return@map blankPair
                val valNameValueMap = recordNumToMapNameValueMap.second
                if(
                    valNameValueMap.isNullOrEmpty()
                ) return@map blankPair
                val valName = valNameValueMap.get(valNameKey)
                    ?: return@map blankPair
                if(
                    prefixList.contains(valName)
                ) return@map blankPair
                val isMultipleVariables =
                    valName == CommandClickScriptVariable.SET_VARIABLE_TYPE
                            || valName == CommandClickScriptVariable.SET_REPLACE_VARIABLE
                if(!isMultipleVariables) {
                    prefixList.add(valName)
                }
                recordNumToMapNameValueMap
            }.filter {
                it.first >= 0
                        && !it.second.isNullOrEmpty()
            }.reversed()
    }

    private fun makeResultEntryMap(
        substituteCmdStartEndContentStr: String,
    ): Pair<String, String> {

        val substituteCmdStartEndContentList = substituteCmdStartEndContentStr.split("=")
        if(
            substituteCmdStartEndContentList.size < 2
        ) return String() to String()
        val variableName =
            substituteCmdStartEndContentList.firstOrNull()
                ?: return String() to String()
        val variableValue = substituteCmdStartEndContentList.filterIndexed { index, s ->
            index > 0
        }.joinToString("=")
        return variableName to variableValue
    }


    private fun makeResultEntryMap2(
        result: Boolean,
        recordNum: Int,
        substituteCmdStartEndContentStr: String,
        substituteCmdStartEndContentList: List<String>,
        onForSetting: Boolean,
    ): Pair<Int, Map<String, String>?> {
        return when(result) {
            true -> {
                val equalIndex = substituteCmdStartEndContentStr.indexOf("=")
                val variableName =
                    if(onForSetting) {
                        MakeSettingVariableNameOrValue.returnValidVariableName(
                            substituteCmdStartEndContentStr,
                            equalIndex,
                            substituteCmdStartEndContentList,
                        ) ?: String()
                    } else {
                        substituteCmdStartEndContentStr.substring(
                            0, equalIndex
                        )
                    }

                val variableValue = substituteCmdStartEndContentStr.substring(
                    equalIndex + 1, substituteCmdStartEndContentStr.length
                )
                val insertVariableMap =
                    if (
                        variableName.isEmpty()
                    ) null
                    else {
                        mapOf(
                            valNameKey to
                                    variableName,
                            valValueKey to
                                    QuoteTool.trimBothEdgeQuote(variableValue)
                        )
                    }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "irecNumtoMap_makeResultEntryMap.txt").absolutePath,
//                    listOf(
//                        "recordNum ${recordNum}",
//                        "result: ${result}",
//                        "variableName: ${variableName}",
//                        "variableName: ${variableName}",
//                        "variableName: ${variableName}",
//                        "variableName: ${variableName}",
//                        "variableName: ${variableName}",
//                    ).joinToString("\n\n") + "\n-----\n"
//
//                )
                recordNum to insertVariableMap
            }

            else -> recordNum to null
        }
    }

}
