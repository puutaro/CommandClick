package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.service.lib.ubuntu.UbuntuFiles
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.time.LocalDateTime


class JsCmd(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    val currentMonitorFileName = terminalViewModel.currentMonitorFileName

    @JavascriptInterface
    fun run(
        url: String,
        executeShellPath:String,
        tabSepaArgs: String = String()
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
            val shellCon = """
                #!/bin/bash
                
                exec bash "${executeShellPath}" ${tabSepaArgs}
            """.trimIndent()

            FileSystems.writeFile(
                ubuntuFiles.filesOneRootfsHomeCmdclickCmdDir.absolutePath,
                ubuntuFiles.cmdShell,
                shellCon
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

    @JavascriptInterface
    fun runByBackground(
        backgroundShellPath: String,
        argsTabSepaStr:String,
        monitorNum: Int,
    ){
        if(
            context == null
        ) return
        val monitorFileName = decideMonitorName(monitorNum)
        val backgroundCmdIntent = Intent()
        backgroundCmdIntent.action = BroadCastIntentScheme.BACKGROUND_CMD_START.action
        backgroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.backgroundShellPath.schema,
            backgroundShellPath
        )
        backgroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema,
            argsTabSepaStr
        )
        backgroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.monitorFileName.schema,
            monitorFileName
        )
        terminalFragment.activity?.sendBroadcast(backgroundCmdIntent)
    }

    @JavascriptInterface
    fun killBackground(
        cmdName: String,
    ){
        if(
            cmdName.isEmpty()
        ) return
        val intent = Intent()
        intent.action = BroadCastIntentScheme.BACKGROUND_CMD_KILL.action
        intent.putExtra(
            UbuntuServerIntentExtra.ubuntuCroutineJobType.schema,
            cmdName
        )
        terminalFragment.activity?.sendBroadcast(intent)
    }

    @JavascriptInterface
    fun launch(){
        val intent = Intent(
            activity,
            UbuntuService::class.java
        )
        context?.let {
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private fun decideMonitorName(
        monitorNum: Int
    ): String {
        return when(monitorNum){
            1 -> UsePath.cmdClickMonitorFileName_1
            2 -> UsePath.cmdClickMonitorFileName_2
            3 -> UsePath.cmdClickMonitorFileName_3
            4 -> UsePath.cmdClickMonitorFileName_4
            else -> UsePath.cmdClickMonitorFileName_1
        }
    }
}