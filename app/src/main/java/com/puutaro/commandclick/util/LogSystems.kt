package com.puutaro.commandclick.util

import android.content.Context
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object LogSystems {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private const val sysLogFileName = UsePath.cmdClickMonitorFileName_2
    val logPrefix = LogTool.logPrefix


    fun stdSys(
        logContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "${logPrefix}${LocalDateTime.now()} ${st.className} ${st.methodName}\n${logContents}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        sysLogFileName
                    ).absolutePath,
                    line
                )
            }
        }
    }

    fun stdErr(
        context: Context?,
        errContents: String,
        debugNotiJanre: String = BroadCastIntentExtraForJsDebug.DebugGenre.SYS_ERR.type,
        notiLevelSrc: String = BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH.level
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "${logPrefix}${LocalDateTime.now()} ${st.className} ${st.methodName} ${LogTool.errMark}\n${errContents}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        sysLogFileName
                    ).absolutePath,
                    line
                )
            }
            withContext(Dispatchers.IO){
                val notiLevel = when(
                    LogTool.EscapeErrMessage.howEscapeErrMessage(line)
                ){
                    true -> BroadCastIntentExtraForJsDebug.NotiLevelType.LOW.level
                    else -> notiLevelSrc
                }
                JsDebugger.sendDebugNoti(
                    context,
                    debugNotiJanre,
                    notiLevel,
                )
            }
        }
    }

    fun stdErrByLowLevelSysNoti(
        context: Context?,
        errContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "${logPrefix}${LocalDateTime.now()} ${st.className} ${st.methodName} ERROR\n${errContents}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        sysLogFileName
                    ).absolutePath,
                    line
                )
            }
            withContext(Dispatchers.IO){
                JsDebugger.sendDebugNoti(
                    context,
                    BroadCastIntentExtraForJsDebug.DebugGenre.SYS_ERR.type,
                    BroadCastIntentExtraForJsDebug.NotiLevelType.LOW.level,
                )
            }
        }
    }

    fun stdErrByNoBroad(
        errContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "${logPrefix}${LocalDateTime.now()} ${st.className} ${st.methodName} ERROR\n${errContents}"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        sysLogFileName
                    ).absolutePath,
                    line
                )
            }
        }
    }


    fun stdWarn(
        errContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "${logPrefix}${LocalDateTime.now()} ${st.className} ${st.methodName} WARNING\n${errContents}"
        FileSystems.updateFile(
            File(
                cmdclickMonitorDirPath,
                sysLogFileName
            ).absolutePath,
            line
        )
    }

}