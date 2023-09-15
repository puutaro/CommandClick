package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.fragment.TerminalFragment

object JudgeAutoExec {
    fun judge(
        terminalFragment: TerminalFragment
    ){
        val tag = terminalFragment.tag
        val previousTerminalTag = terminalFragment.previousTerminalTag

        if(
            previousTerminalTag.isNullOrEmpty()
        ) false
        else previousTerminalTag != tag
    }
}