package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.intent.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.Shell2Http
import com.puutaro.commandclick.proccess.ubuntu.SshManager
import com.puutaro.commandclick.proccess.ubuntu.UbuntuController
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.shell.LinuxCmd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class JsUbuntu(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val cmdTerminalUrl = "http://127.0.0.1:${UsePort.WEB_SSH_TERM_PORT}"

    @JavascriptInterface
    fun execScript(
        executeShellPath:String,
        tabSepaArgs: String = String(),
    ): String {
        return execScriptF(
            executeShellPath,
            tabSepaArgs,
            2000,
        )
    }


    @JavascriptInterface
    fun execScriptF(
        executeShellPath:String,
        tabSepaArgs: String = String(),
        timeMilisec: Int,
    ): String {
        if (
            context == null
        ) return String()
        if (
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ) {
            Toast.makeText(
                context,
                "Launch ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return String()
        }
        return Shell2Http.runCmd(
            executeShellPath,
            tabSepaArgs,
            timeMilisec,
        )
    }


    @JavascriptInterface
    fun execScriptBySsh(
        executeShellPath:String,
        tabSepaArgs: String = String(),
        monitorNum: Int,
    ): String {
        if(
            context == null
        ) return  String()
        if(
            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
        ) {
            Toast.makeText(
                context,
                "Launch ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return  String()
        }
        val monitorFileName = UsePath.decideMonitorName(monitorNum)
        return SshManager.execScript(
            executeShellPath,
            tabSepaArgs,
            monitorFileName,
            true,
        )
    }

    @JavascriptInterface
    fun execScriptByBackground(
        backgroundShellPath: String,
        argsTabSepaStr:String,
        monitorNum: Int,
    ){
        UbuntuController.execScriptByBackground(
            terminalFragment,
            backgroundShellPath,
            argsTabSepaStr,
            monitorNum,
        )
    }

    @JavascriptInterface
    fun killBackground(
        cmdName: String,
    ){
        if(
            cmdName.isEmpty()
        ) return
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeUbuntu.CMD_KILL_BY_ADMIN.action,
            listOf(
                UbuntuServerIntentExtra.ubuntuCroutineJobTypeListForKill.schema to
                        cmdName
            )
        )
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
        val ubuntuFiles = UbuntuFiles(context)
        if(
            !ubuntuFiles.ubuntuSetupCompFile.isFile
        ){
            jsUrl.loadUrl(jsScriptUrl)
            return
        }
        var retryTimesProcess = 0
        val firstSuccess = 0
        val bootFailureTimes = 50
        UbuntuController.boot(terminalFragment)
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
                        if( i % 10 != 0) return@boot
                        Toast.makeText(
                            context,
                            "boot..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    delay(300)
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
                for(i in 0..10) {
                    val isActive = try {
                        CurlManager.get(
                            cmdTerminalUrl,
                            String(),
                            String(),
                            200,
                        ).let{
                            CurlManager.convertResToStrByConn(it)
                        }.isNotEmpty()
                    } catch (e: Exception){
                        false
                    }
                    if(isActive) break
                    withContext(Dispatchers.Main) boot@ {
                        val remainder = i % 10
                        if( remainder != 0) return@boot
                        val quotient = i / 10
                        Toast.makeText(
                            context,
                            "ready${".".repeat(quotient)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    delay(500)
                }
                withContext(Dispatchers.Main) {
                    delay(delayMiliTime.toLong())
                    jsUrl.loadUrl(jsScriptUrl)
                }
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
            !UbuntuFiles(context).ubuntuSetupCompFile.isFile
        ){
            Toast.makeText(
                context,
                "Setup ubuntu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if(
            LinuxCmd.isBasicProcess()
        ){
            return
        }
        UbuntuController.boot(terminalFragment)
        runBlocking {
            withContext(Dispatchers.IO) {
                for (i in 1..50) {
                    withContext(Dispatchers.Main) toast@ {
                        if(i % 5 != 0) return@toast
                        Toast.makeText(
                            context,
                            "boot${".".repeat(i / 10 + 1)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (
                        LinuxCmd.isBasicProcess()
                    ) {
                        isBootSuccess = true
                        break
                    }
                    delay(300)
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
        Toast.makeText(
            context,
            "boot ok",
            Toast.LENGTH_SHORT
        ).show()
    }

    @JavascriptInterface
    fun isProc(
        processName: String
    ): Boolean {
        return LinuxCmd.isProcessCheck(processName)
    }
}