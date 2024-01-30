package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object MonitorTextLauncher {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val cmdclickTempMonitorDirPath = UsePath.cmdclickTempMonitorDirPath
        val cmdclickTmpUpdateMonitorFileName = UsePath.cmdclickTmpUpdateMonitorFileName
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.onDisplayUpdate = true
        val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        val monitorText = ReadText(
            cmdclickMonitorDirPath,
            currentMonitorFileName
        ).readText() + "\n" + ReadText(
            cmdclickTempMonitorDirPath,
            cmdclickTmpUpdateMonitorFileName
        ).readText()
        FileSystems.writeFile(
            UsePath.cmdclickMonitorDirPath,
            currentMonitorFileName,
            monitorText
        )
        FileSystems.removeFiles(
            cmdclickTempMonitorDirPath,
            cmdclickTmpUpdateMonitorFileName
        )
    }
}