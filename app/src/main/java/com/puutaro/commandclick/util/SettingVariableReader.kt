package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

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
        val runShellSourceTrim = QuoteTool.trimBothEdgeQuote(runShellSource)
        return if(
            runShellSourceTrim == String()
        ) variableDefaultStrValue
        else runShellSourceTrim
    }

    fun isExist(
        cmdVariableList: List<String>?,
        variableName: String,
    ): Boolean {
        return CommandClickVariables.isExist(
            cmdVariableList,
            variableName
        )
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
        val historySwitchSourceTrim = QuoteTool.trimBothEdgeQuote(historySwitchSource)

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
        )?.joinToString(",").let {
            QuoteTool.removeDoubleQuoteByIgnoreBackSlash(it)
        } ?: String()
        return ScriptPreWordReplacer.replace(
            variableValueListSource,
            currentAppDirPath,
            scriptName
        ).split(",")
    }

    fun setListFromPath(
        setListFilePath: String,
    ): List<String> {
        val setListFilePathObj = File(setListFilePath)
        val setListFileDirPath = setListFilePathObj.parent
            ?: String()
        if(
            setListFileDirPath.isNotEmpty()
        ) {
            FileSystems.createDirs(
                setListFileDirPath
            )
        }
        val setListFileName = setListFilePathObj.name
        return ReadText(
            setListFileDirPath,
            setListFileName
        ).textToList()
    }
}