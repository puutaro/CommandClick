package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn


object RecordNumToMapNameValueInHolder {
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
                val isMultipleVariables =
                    it.startsWith("${CommandClickScriptVariable.SET_VARIABLE_TYPE}=")
                            || it.startsWith("${CommandClickScriptVariable.SET_REPLACE_VARIABLE}=")
                            || it.startsWith("${CommandClickScriptVariable.HOME_SCRIPT_URL}=")
                val isNotContainHitedList = !hitedList.contains(it)
                if (isNotContainHitedList && !isMultipleVariables) hitedList.add(it)
                isNotContainHitedList || isMultipleVariables
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
        return if (variableName == CommandClickScriptVariable.SCRIPT_FILE_NAME) {
            currentShellFileName ?: substituteCmdStartEndContentStr.substring(
                equalIndex + 1, substituteCmdStartEndContentStr.length
            )
        } else {
            substituteCmdStartEndContentStr.substring(
                equalIndex + 1, substituteCmdStartEndContentStr.length
            )
        }
}
