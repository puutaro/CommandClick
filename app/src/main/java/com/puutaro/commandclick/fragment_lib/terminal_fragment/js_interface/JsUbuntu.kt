package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.network.UsePort
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.Shell2Http
import com.puutaro.commandclick.proccess.ubuntu.UbuntuController
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.ubuntu.UbuntuProcessChecker
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
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
        tabSepaArgs: String,
    ): String {
        if(
            !UbuntuProcessChecker.isExist(
                context,
                null
            )
        ) return String()
        return Shell2Http.runScript(
            context,
            executeShellPath,
            tabSepaArgs,
            2000,
            null,
        )
    }



    @JavascriptInterface
    fun execScriptF(
        executeShellPath:String,
        tabSepaArgs: String,
        timeMilisec: Int,
        monitorNum: Int,
    ): String {
        if(
            !UbuntuProcessChecker.isExist(
                context,
                null
            )
        ) return String()
        val monitorName = when(monitorNum == 0) {
            false -> UsePath.decideMonitorName(monitorNum)
            else -> null
        }
        return Shell2Http.runScript(
            context,
            executeShellPath,
            tabSepaArgs,
            timeMilisec,
            monitorName,
        )
    }


//    @JavascriptInterface
//    fun execScriptBySsh(
//        executeShellPath:String,
//        tabSepaArgs: String,
//        monitorNum: Int,
//    ): String {
//        if(
//            context == null
//        ) return  String()
//        if(
//            !UbuntuFiles(context).ubuntuLaunchCompFile.isFile
//        ) {
//            ToastUtils.showShort("Launch ubuntu")
//            return  String()
//        }
//        val monitorFileName = UsePath.decideMonitorName(monitorNum)
//        return SshManager.execScript(
//            context,
//            executeShellPath,
//            tabSepaArgs,
//            monitorFileName,
//            true,
//        )
//    }

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
            context,
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
                        LinuxCmd.isBasicProcess(context)
                    ) {
                        retryTimesProcess = i
                        break
                    }
                    withContext(Dispatchers.Main) boot@ {
                        if( i % 10 != 0) return@boot
                        ToastUtils.showShort("boot..")
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
                    ToastUtils.showShort("boot failure")
                }
                return@launch
            }
            withContext(Dispatchers.IO){
                for(i in 0..10) {
                    val isActive = try {
                        CurlManager.get(
                            context,
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
                        ToastUtils.showShort("ready${".".repeat(quotient)}")
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
    fun isSetup(): Boolean {
        if(
            context == null
        ) return false
        val isSetupFile = UbuntuFiles(context).ubuntuSetupCompFile.isFile
        return isSetupFile
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
            ToastUtils.showShort("Setup ubuntu")
            return
        }
        if(
            LinuxCmd.isBasicProcess(context)
        ){
            return
        }
        UbuntuController.boot(terminalFragment)
        runBlocking {
            withContext(Dispatchers.IO) {
                for (i in 1..50) {
                    withContext(Dispatchers.Main) toast@ {
                        if(i % 5 != 0) return@toast
                        ToastUtils.showShort("boot${".".repeat(i / 10 + 1)}")
                    }
                    if (
                        LinuxCmd.isBasicProcess(context)
                    ) {
                        isBootSuccess = true
                        break
                    }
                    delay(300)
                }
            }
        }
        if(!isBootSuccess) {
            ToastUtils.showShort("boot failure")
            return
        }
        ToastUtils.showShort("boot ok")
    }

    @JavascriptInterface
    fun isProc(
        processName: String
    ): Boolean {
        return LinuxCmd.isProcessCheck(
            context,
            processName
        )
    }

    @JavascriptInterface
    fun untilSetupLoop(
        launchJsPath: String,
    ){
        if(
            isSetup()
        ) return
        JsUrl(terminalFragment).loadJsPath(launchJsPath, String())
    }

    @JavascriptInterface
    fun isInstall(
        installStampFilePath: String,
        expectStampCon: String,
        installConfirmTitleAndMessage: String,
        installOneList: String,
        cautionTitleAndMessage: String,
    ): Boolean {
        val stampCon =
            ReadText(installStampFilePath).readText().trim()
        if(
            stampCon == expectStampCon
        ) return true
        val jsDialog = JsDialog(terminalFragment)
        val installTitleToMsg = makeTitleToMsg(installConfirmTitleAndMessage)
        val el = jsDialog.listDialog(
            installTitleToMsg.first,
            installTitleToMsg.second,
            installOneList
        )
        if(
            el.isNotEmpty()
        ) return false
        val cautionTitleToMsg = makeTitleToMsg(cautionTitleAndMessage)
        JsDialog(terminalFragment).listDialog(
            cautionTitleToMsg.first,
            cautionTitleToMsg.second,
            String()
        )
        JsUrl(terminalFragment).exit_S()
        return false
    }

    private fun makeTitleToMsg(
        titleAndMessage: String,
    ): Pair<String, String> {
        val titleAndMsgList = titleAndMessage.split("|")
        val title = titleAndMsgList.firstOrNull() ?: String()
        val msg = titleAndMsgList.getOrNull(1) ?: String()
        return title to msg
    }
}