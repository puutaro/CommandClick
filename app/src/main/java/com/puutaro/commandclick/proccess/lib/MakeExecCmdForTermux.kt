package com.puutaro.commandclick.proccess.lib


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
            val beforeCommand =  if(beforeCommandSource.isNotEmpty()){
                "${beforeCommandSource} ${normalOutputMark} \"${outputPath}\"${debugSign};"
            } else String()
            val afterCommandSource = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.AFTER_COMMAND,
            )?.let{
                trimBothEdgeQuote(it)
            }?.trim(' ') ?: String()

            val afterCommand =  if(afterCommandSource.isNotEmpty()){
                "${afterCommandSource} ${normalOutputMark} \"${outputPath}\"${debugSign};"
            } else String()

            val execBashScript = "${recentAppdirPath}/" +
                    selectedShellFileName
            val factExecCmd = "${runShell} \"${execBashScript}\""
            val execTitle = "echo \"\n### \$(date \"+%Y/%m/%d-%H:%M:%S\") ${selectedShellFileName}\" " +
                    " ${monitorFileReDirectMark} \"${outputPath}\";"
//            val makeHtmlCmd =
//                " | ansi2html | sed  -e '1,/^<pre class=/d' -e '/^<\\/pre>$/,\$d' | sed '\$d' "


            val execCmdSource = if(
                terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
                || terminalOutputMode == SettingVariableSelects.Companion.TerminalOutPutModeSelects.NO.name
            ) {
                beforeCommand +
                        " ${factExecCmd};" +
                        afterCommand
            } else if (
                terminalDo == SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name
            ) {
                execTitle +
                        "${beforeCommandSource};" +
                        "${factExecCmd};" +
                        "${afterCommandSource};"
            } else {
                execTitle +
                        beforeCommand +
                        " ${factExecCmd} ${normalOutputMark} \"${outputPath}\"${debugSign};" +
                        afterCommand
            }
            return execCmdSource.replace(";;", ";")
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

