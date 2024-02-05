package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

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
            File(
                cmdclickMonitorDirPath,
                currentMonitorFileName
            ).absolutePath
        ).readText() + "\n" + ReadText(
            File(
                cmdclickTempMonitorDirPath,
                cmdclickTmpUpdateMonitorFileName
            ).absolutePath
        ).readText()
        FileSystems.writeFile(
            File(
                UsePath.cmdclickMonitorDirPath,
                currentMonitorFileName
            ).absolutePath,
            monitorText
        )
        FileSystems.removeFiles(
            File(
                cmdclickTempMonitorDirPath,
                cmdclickTmpUpdateMonitorFileName
            ).absolutePath
        )
    }
}