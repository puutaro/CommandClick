package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn


class RecordNumToMapNameValueInHolder {

    companion object {

        fun parse(
            shellContentsList: List<String>,
            startHolderName: String,
            endHolderName: String,
            onForSetting: Boolean = false,
            currentShellFileName: String? = null
        ): Map<Int, Map<String, String>?>? {
            if(shellContentsList.isEmpty()) return null
            val commandPromptStartNum = shellContentsList.indexOf(
                startHolderName
            )
            val commandPromptEndNum = shellContentsList.indexOf(
                endHolderName
            )
            val substituteCmdStartEndContentList = if(
                commandPromptStartNum > 0
                && commandPromptEndNum > 0
                && commandPromptStartNum < commandPromptEndNum
            ) {
                shellContentsList.slice(
                    commandPromptStartNum..commandPromptEndNum
                )
            } else {
                return null
            }
            val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
            val hitedList = mutableSetOf<String>()
            val filteredSubstituteCmdStartEndContentList = if(onForSetting) {
                substituteCmdStartEndContentList.filter {
                    val isSetVariables =
                        it.startsWith("${CommandClickShellScript.SET_VARIABLE_TYPE}=")
                                || it.startsWith("${CommandClickShellScript.SET_REPLACE_VARIABLE}=")
                    val isNotContainHitedList = !hitedList.contains(it)
                    if (isNotContainHitedList && !isSetVariables) hitedList.add(it)
                    isNotContainHitedList || isSetVariables
                }
            } else {
                substituteCmdStartEndContentList
            }
            val substituteCmdStartEndContentListSize =
                filteredSubstituteCmdStartEndContentList.size - 1
            return (0..substituteCmdStartEndContentListSize).map {
                    listOrderNum ->
                val substituteCmdStartEndContentStr =
                    filteredSubstituteCmdStartEndContentList[listOrderNum]
                val recordNum = commandPromptStartNum + listOrderNum
                val result = cmdclickVariableRegex.containsMatchIn(
                    substituteCmdStartEndContentStr
                )
                makeResultEntryMap(
                    result,
                    recordNum,
                    substituteCmdStartEndContentStr,
                    filteredSubstituteCmdStartEndContentList,
                    onForSetting,
                    currentShellFileName
                )
            }.toMap().filterValues {
                it != null
            }
        }
    }
}


private fun makeResultEntryMap(
    result: Boolean,
    recordNum: Int,
    substituteCmdStartEndContentStr: String,
    substituteCmdStartEndContentList: List<String>,
    onForSetting: Boolean,
    currentShellFileName: String?
): Pair<Int, Map<String, String>?> {
    return if(result){
        val equalIndex = substituteCmdStartEndContentStr.indexOf("=")
        val variableName = if (onForSetting) {
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

        val variableValue = if (onForSetting) {
            returnUpdateShellFileName(
                substituteCmdStartEndContentStr,
                equalIndex,
                variableName,
                currentShellFileName
            )
        } else {
            substituteCmdStartEndContentStr.substring(
                equalIndex + 1, substituteCmdStartEndContentStr.length
            )
        }
        val insertVariableMap = if(variableName.isEmpty()) {
            null
        } else {
            mapOf(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name to
                        variableName,
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name to
                        BothEdgeQuote.trim(variableValue)
            )
        }
        recordNum to insertVariableMap
    } else {
        recordNum to null
    }
}


private fun returnUpdateShellFileName(
        substituteCmdStartEndContentStr: String,
        equalIndex: Int,
        variableName: String?,
        currentShellFileName: String?
    ): String {
        return if (variableName == CommandClickShellScript.SCRIPT_FILE_NAME) {
            currentShellFileName ?: substituteCmdStartEndContentStr.substring(
                equalIndex + 1, substituteCmdStartEndContentStr.length
            )
        } else {
            substituteCmdStartEndContentStr.substring(
                equalIndex + 1, substituteCmdStartEndContentStr.length
            )
        }
}
