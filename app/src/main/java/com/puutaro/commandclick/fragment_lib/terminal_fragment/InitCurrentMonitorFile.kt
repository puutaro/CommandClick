package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class InitCurrentMonitorFile {
        companion object {
            fun trim(
                terminalFragment: TerminalFragment
            ) {
                val trimLastLine = terminalFragment.trimLastLine
                val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
                val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
                val currentMonitorFileName = terminalViewModel.currentMonitorFileName
                val trimedMonitorContents = ReadText(
                    cmdclickMonitorDirPath,
                    currentMonitorFileName
                ).textToList().takeLast(trimLastLine).joinToString("\n")
                FileSystems.writeFile(
                    cmdclickMonitorDirPath,
                    currentMonitorFileName,
                    trimedMonitorContents
                )
            }
        }
}