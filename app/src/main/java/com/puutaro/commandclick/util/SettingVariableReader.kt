package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable

object SettingVariableReader {
    fun getStrValue(
        cmdVariableList: List<String>?,
        variableName: String,
        variableDefaultStrValue: String,
    ): String {
        val runShellSource =  CommandClickVariables.substituteCmdClickVariable(
            cmdVariableList,
            variableName
        ) ?: variableDefaultStrValue
        val runShellSourceTrim = BothEdgeQuote.trim(runShellSource)
        return if(
            runShellSourceTrim == String()
        ) variableDefaultStrValue
        else runShellSourceTrim
    }

    fun getCbValue(
        cmdVariableList: List<String>?,
        variableName: String,
        defaultVariableStrValue: String,
        inheritVariableValue: String,
        inheritVariableReturnValue: String,
        noDefaultValueList: List<String>,
    ): String {
        val historySwitchSource = CommandClickVariables.substituteCmdClickVariable(
            cmdVariableList,
            variableName
        ) ?: defaultVariableStrValue
        val historySwitchSourceTrim = BothEdgeQuote.trim(historySwitchSource)

        return if(
            noDefaultValueList.contains(historySwitchSourceTrim)
        ) historySwitchSourceTrim
        else if(
            historySwitchSourceTrim == inheritVariableValue
        ) inheritVariableReturnValue
        else defaultVariableStrValue
    }

    fun getNumValue(
        cmdVariableList: List<String>?,
        numVariableName: String,
        defaultNum: Int,
        throwNumStr: String
    ): Int {
        return try {
            val fontZoomShellValueSource = CommandClickVariables.substituteCmdClickVariable(
                cmdVariableList,
                numVariableName,
            )
            val fontZoomShellValue = if(
                fontZoomShellValueSource == throwNumStr
                || fontZoomShellValueSource.isNullOrEmpty()
                || fontZoomShellValueSource == "0"
            ){
                defaultNum.toString()
            } else fontZoomShellValueSource
            fontZoomShellValue.toInt() ?: defaultNum
        } catch (e: Exception) {
            defaultNum
        }
    }

    fun getStrListByReplace(
        settingVariableList: List<String>?,
        variableName: String,
        scriptName: String,
        currentAppDirPath: String,
    ): List<String> {
        val variableValueListSource = CommandClickVariables.substituteCmdClickVariableList(
            settingVariableList,
            variableName
        )?.joinToString(",") ?: String()
        val fannelDirName = scriptName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        return ScriptPreWordReplacer.replace(
            variableValueListSource,
            "${currentAppDirPath}/${scriptName}",
            currentAppDirPath,
            fannelDirName,
            scriptName
        ).split(",")
    }
}