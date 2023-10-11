package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object InitCurrentMonitorFile {
    fun trim(
        terminalFragment: TerminalFragment
    ) {
        val leavesLineForTerm = ReadText.leavesLineForTerm
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        val trimedMonitorContents = ReadText(
            cmdclickMonitorDirPath,
            currentMonitorFileName
        ).textToList().takeLast(leavesLineForTerm).joinToString("\n")
        FileSystems.writeFile(
            cmdclickMonitorDirPath,
            currentMonitorFileName,
            trimedMonitorContents
        )
    }
}