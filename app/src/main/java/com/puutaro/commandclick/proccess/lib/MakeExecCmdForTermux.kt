package com.puutaro.commandclick.proccess.lib


import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class MakeExecCmdForTermux {
    companion object {
        fun make(
            currentFragment: Fragment,
            terminalDo: String,
            substituteSettingVariableList: List<String>?,
            recentAppdirPath: String,
            selectedShellFileName: String,
            runShell: String,
        ): String {
            val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()

            val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
            val terminalOutputMode = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.TERMINAL_OUTPUT_MODE,
            )?.trim(' ') ?: CommandClickShellScript.TERMINAL_OUTPUT_MODE_DEFAULT_VALUE
            val normalOutputMark = OutputMark.NORMAL_OUTPUT_MARK.str
            val refleshOutputMark = OutputMark.REFLESH_OUTPUT_MARK.str
            val monitorFileReDirectMark = when(terminalOutputMode) {
                SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH.name,
                SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name -> refleshOutputMark
                else -> normalOutputMark
            }
            val debugSign = if(
                terminalOutputMode ==
                SettingVariableSelects.Companion.TerminalOutPutModeSelects.DEBUG.name
            ) " 2>&1 "
            else String()
            terminalViewModel.onBottomScrollbyJs = !(
                    terminalOutputMode ==
                            SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
                    )

            val beforeCommandSource = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.BEFORE_COMMAND,
            )?.let{
                trimBothEdgeQuote(it)
            }?.trim(' ') ?: String()

            val afterCommandSource = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.AFTER_COMMAND,
            )?.let{
                trimBothEdgeQuote(it)
            }?.trim(' ') ?: String()

            val execBashScript = "${recentAppdirPath}/" +
                    selectedShellFileName
            val factExecCmd = "${runShell} \"${execBashScript}\""
            val execTitle = "echo \"\n### \$(date \"+%Y/%m/%d-%H:%M:%S\") ${selectedShellFileName}\" " +
                    " ${monitorFileReDirectMark} \"${outputPath}\";"


            val execCmdSource = if(
                terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
                || terminalOutputMode == SettingVariableSelects.Companion.TerminalOutPutModeSelects.NO.name
                || terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name
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
            return execCmdSource.trim(' ').trim(';').replace(";;", ";")
        }
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

