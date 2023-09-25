package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.utils.UbuntuFiles
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.time.LocalDateTime


class JsCmd(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    val currentMonitorFileName = terminalViewModel.currentMonitorFileName

    @JavascriptInterface
    fun run(
        url: String,
        executeShellPath:String
    ): String {
        if(context == null) return String()
        try {
            val ubuntuFiles = UbuntuFiles(
                context,
            )
            val executeShellObj = File(executeShellPath)
            val executeShellDirPath = executeShellObj.parent
                ?: return String()
            val executeShellName = executeShellObj.name
                ?: return String()
            FileSystems.writeFile(
                ubuntuFiles.filesOneRootfsHomeCmdclickCmdDir.absolutePath,
                ubuntuFiles.cmdShell,
                ReadText(
                    executeShellDirPath,
                    executeShellName
                ).readText()
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()}\n curl start"
            )
            val shellOutput = CurlManager.get(
                url,
                String(),
                String(),
                2000,
            )
            if(
                shellOutput.isEmpty()
            ) {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    currentMonitorFileName,
                    "### ${LocalDateTime.now()}\n no output"
                )
                return String()
            }
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()}\n ${shellOutput}"
            )
            Toast.makeText(
                context,
                shellOutput,
                Toast.LENGTH_SHORT
            ).show()
            return shellOutput

        } catch (e: Exception) {
            FileSystems.writeFile(
                cmdclickDefaultAppDirPath,
                "prootError.txt",
                "### ${LocalDateTime.now()}\n${e.toString()}"
            )
            return String()
        }
    }
}