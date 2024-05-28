package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File

object SettingVariableReader {
    fun getStrValue(
        cmdVariableList: List<String>?,
        variableName: String,
        variableDefaultStrValue: String,
    ): String {
        val variableValue =  CommandClickVariables.substituteCmdClickVariable(
            cmdVariableList,
            variableName
        ) ?: variableDefaultStrValue
        val variableValueTrim = QuoteTool.trimBothEdgeQuote(variableValue)
        return if(
            variableValueTrim.isEmpty()
        ) variableDefaultStrValue
        else variableValueTrim
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
            fontZoomShellValue.toInt()
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
        return ScriptPreWordReplacer.replace(
            variableValueListSource,
            currentAppDirPath,
            scriptName
        ).let {
            QuoteTool.splitBySurroundedIgnore(
                it,
                ','
            )
        }
//            .split(",")
    }

    fun setListFromPath(
        setListFilePath: String,
    ): List<String> {
        val setListFilePathObj = File(setListFilePath)
        if(!setListFilePathObj.isFile) return emptyList()
        val setListFileDirPath = setListFilePathObj.parent
            ?: String()
        if(
            setListFileDirPath.isNotEmpty()
        ) FileSystems.createDirs(
                setListFileDirPath
            )
        return ReadText(
            setListFilePath
        ).textToList()
    }
}