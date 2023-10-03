package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme
import com.puutaro.commandclick.common.variable.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.UbuntuBootManager
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


class JsUbuntu(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    val currentMonitorFileName = UsePath.cmdClickMonitorFileName_2
    val cmdTerminalUrl = "http://127.0.0.1:${UsePort.WEB_SSH_TERM_PORT}"

    @JavascriptInterface
    fun runCmd(
        executeShellPath:String,
        tabSepaArgs: String = String(),
        timeoutMiliSec: Int,
    ): String {
        if(context == null) return String()
        val cmdUrl = "http://127.0.0.1:${UsePort.HTTP2_SHELL_PORT.num}/bash"
        try {
            val shellCon = """
                #!/bin/bash
                
                exec bash "${executeShellPath}" ${tabSepaArgs}
            """.trimIndent()
            FileSystems.writeFile(
                UsePath.cmdclickTempCmdDirPath,
                UsePath.cmdclickTempCmdShellName,
                shellCon
            )
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()} ${this::class.java.name}\n curl start"
            )
            val shellOutput = CurlManager.get(
                cmdUrl,
                String(),
                String(),
                timeoutMiliSec,
            )
            if(
                shellOutput.isEmpty()
            ) {
                FileSystems.updateFile(
                    cmdclickMonitorDirPath,
                    currentMonitorFileName,
                    "### ${LocalDateTime.now()} ${this::class.java.name}\n no output"
                )
                return String()
            }
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()} ${this::class.java.name}\n ${shellOutput}"
            )
            return shellOutput
        } catch (e: Exception) {
            FileSystems.updateFile(
                cmdclickMonitorDirPath,
                currentMonitorFileName,
                "### ${LocalDateTime.now()} ${this::class.java.name}\n${e.toString()}"
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
            UbuntuServerIntentExtra.ubuntuCroutineJobTypeList.schema,
            cmdName
        )
        terminalFragment.activity?.sendBroadcast(intent)
    }


    @JavascriptInterface
    fun bootOnExec(
        execCode: String,
        delayMiliTime: Int
    ){
        if(
            context == null
        ) return
        val jsUrl = JsUrl(terminalFragment)
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
            execCode.split("\n")
        ) ?: return
        if(
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ){
            jsUrl.loadUrl(jsScriptUrl)
            return
        }
        var retryTimesProcess = 0
        val firstSuccess = 0
        val bootFailureTimes = 50
        UbuntuBootManager.boot(terminalFragment)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                for (i in 0..bootFailureTimes) {
                    if (
                        LinuxCmd.isBasicProcess()
                    ) {
                        retryTimesProcess = i
                        break
                    }
                    withContext(Dispatchers.Main) boot@ {
                        if( i % 20 != 0) return@boot
                        Toast.makeText(
                            context,
                            "boot..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    delay(100)
                }
            }
            if(
                retryTimesProcess == firstSuccess
            ) {
                withContext(Dispatchers.Main){
                    jsUrl.loadUrl(jsScriptUrl)
                }
                return@launch
            }
            if(retryTimesProcess == bootFailureTimes){
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "boot failure",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }
            withContext(Dispatchers.IO){
                for(i in 0..4) {
                    val isActive = try {
                        CurlManager.get(
                            cmdTerminalUrl,
                            String(),
                            String(),
                            200,
                        ).isNotEmpty()
                    } catch (e: Exception){
                        false
                    }
                    if(isActive) break
                    withContext(Dispatchers.Main) boot@ {
                        val remainder = i % 20
                        if( remainder != 0) return@boot
                        Toast.makeText(
                            context,
                            ".".repeat(remainder),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    delay(1000)
                }
                withContext(Dispatchers.Main) {
                    jsUrl.loadUrl(jsScriptUrl)
                }
//                if(isRetryTimes == 0) return@withContext
//                withContext(Dispatchers.Main){
//                    Toast.makeText(
//                        context,
//                        "delay..${delayMiliTime} mili sec",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                delay(delayMiliTime.toLong())
            }
        }
    }

    @JavascriptInterface
    fun boot(){
        if(
            context == null
        ) return
        var isBootSuccess = false
        if(
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ){
            Toast.makeText(
                context,
                "Setup ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        UbuntuBootManager.boot(terminalFragment)
        runBlocking {
            withContext(Dispatchers.IO) {
                for (i in 1..50) {
                    if (
                        LinuxCmd.isBasicProcess()
                    ) {
                        isBootSuccess = true
                        break
                    }
                    delay(100)
                }
            }
        }
        if(!isBootSuccess) {
            Toast.makeText(
                context,
                "boot failure",
                Toast.LENGTH_SHORT
            ).show()
            return
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