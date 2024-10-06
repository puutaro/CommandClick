package com.puutaro.commandclick.proccess.lib


import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object MakeExecCmdForTermux {
    fun make(
        currentFragment: Fragment,
        terminalDo: String,
        substituteSettingVariableList: List<String>?,
//            recentAppdirPath: String,
        selectedShellFileName: String,
        runShell: String,
    ): String {
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()

        val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val terminalOutputMode = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.TERMINAL_OUTPUT_MODE,
        )?.trim() ?: CommandClickScriptVariable.TERMINAL_OUTPUT_MODE_DEFAULT_VALUE
        val normalOutputMark = OutputMark.NORMAL_OUTPUT_MARK.str
        val refleshOutputMark = OutputMark.REFLESH_OUTPUT_MARK.str
        val monitorFileReDirectMark = when(terminalOutputMode) {
            SettingVariableSelects.TerminalOutPutModeSelects.REFLASH.name,
            SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name -> refleshOutputMark
            else -> normalOutputMark
        }
        val debugSign = if(
            terminalOutputMode ==
            SettingVariableSelects.TerminalOutPutModeSelects.DEBUG.name
        ) " 2>&1 "
        else String()
        terminalViewModel.onBottomScrollbyJs = !(
                terminalOutputMode ==
                        SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
                )

        val beforeCommandSource = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.BEFORE_COMMAND,
        )?.let{
            trimBothEdgeQuote(it)
        }?.trim() ?: String()

        val afterCommandSource = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.AFTER_COMMAND,
        )?.let{
            trimBothEdgeQuote(it)
        }?.trim() ?: String()

        val execBashScript = "${UsePath.cmdclickDefaultAppDirPath}/" +
                selectedShellFileName
        val factExecCmd = "${runShell} \"${execBashScript}\""
        val execTitle = "echo \"\n### \$(date \"+%Y/%m/%d-%H:%M:%S\") ${selectedShellFileName}\" " +
                " ${monitorFileReDirectMark} \"${outputPath}\";"


        val execCmdSource = if(
            terminalDo == SettingVariableSelects.TerminalDoSelects.OFF.name
            || terminalOutputMode == SettingVariableSelects.TerminalOutPutModeSelects.NO.name
//            || terminalDo == SettingVariableSelects.TerminalDoSelects.TERMUX.name
            || !terminalViewModel.launchUrl.isNullOrEmpty()
        ) {
            "${beforeCommandSource};" +
                    "${factExecCmd};" +
                    "${afterCommandSource};"
        } else {
            execTitle +
                    "${beforeCommandSource} ${normalOutputMark} \"${outputPath}\"${debugSign};" +
                    "${factExecCmd} ${normalOutputMark} \"${outputPath}\"${debugSign};" +
                    "${afterCommandSource} ${normalOutputMark} \"${outputPath}\"${debugSign};"
        }
        return execCmdSource.trim().trim(';').replace(";;", ";")
    }
}

private enum class OutputMark(
    val str: String
){
    NORMAL_OUTPUT_MARK(">>"),
    REFLESH_OUTPUT_MARK(">")
}


private fun trimBothEdgeQuote(
    targetStr: String,
): String {
    val singleQuote = '\''
    val doubleQuote = '"'
    return targetStr.let{
        trimBothEdge(
            it,
            singleQuote
        )
    }.let {
        trimBothEdge(
            it,
            doubleQuote
        )
    }
}

private fun trimBothEdge(
    targetStr: String,
    trimChar: Char
): String {
    val targetStrLength = targetStr.length - 1
    return if(
        targetStr.indexOf(trimChar) == 0
        && targetStr.lastIndexOf(trimChar) == targetStrLength
    ) targetStr.trim(trimChar)
    else targetStr

}

