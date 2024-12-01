package com.puutaro.commandclick.util

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.broadcast.extra.ErrLogExtraForTerm
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.time.LocalDateTime

object LogSystems {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private const val sysLogFileName = UsePath.cmdClickMonitorFileName_2
    val logPrefix = CheckTool.logPrefix


    fun stdSys(
        logContents: String,
    ){
        val line = StInfo.make(
            String(),
            logContents
        )
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

    fun stdSSys(
        logContents: String,
    ){
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                FileSystems.updateFile(
                    File(
                        cmdclickMonitorDirPath,
                        sysLogFileName
                    ).absolutePath,
                    logContents
                )
            }
        }
    }

    fun broadErrLog(
        context: Context?,
        toastMessage: String?,
        errMessage: String,
    ){
        if(!toastMessage.isNullOrEmpty()) {
            BroadcastSender.normalSend(
                context,
                BroadCastIntentSchemeTerm.MONITOR_TOAST.action,
                listOf(
                    Pair(
                        BroadCastIntentSchemeTerm.MONITOR_TOAST.scheme,
                        toastMessage
                    )
                )
            )
        }
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeTerm.ERR_LOG.action,
            listOf(
                ErrLogExtraForTerm.ERR_CONTENTS.schema to
                        errMessage
            )
        )
    }

    fun stdErr(
        context: Context?,
        errContents: String,
        debugNotiJanre: String = BroadCastIntentExtraForJsDebug.DebugGenre.SYS_ERR.type,
        notiLevelSrc: String = BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH.level
    ){
        val line = StInfo.make(
            LogLebel.ERROR.name,
            errContents
        )
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
                    CheckTool.EscapeErrMessage.howEscapeErrMessage(line)
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
        val line = StInfo.make(
            LogLebel.ERROR.name,
            errContents
        )
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
        val line = StInfo.make(
            LogLebel.ERROR.name,
            errContents
        )
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
        val line = StInfo.make(
            LogLebel.WARNING.name,
            errContents
        )
        FileSystems.updateFile(
            File(
                cmdclickMonitorDirPath,
                sysLogFileName
            ).absolutePath,
            line
        )
    }

    fun stdSErr(
        errContents: String,
    ){
        FileSystems.updateFile(
            File(
                cmdclickMonitorDirPath,
                sysLogFileName
            ).absolutePath,
            "ERR $errContents"
        )
    }


    private object StInfo {
        fun make(
            logLevel: String,
            con: String
        ): String {
            val beforeLine = makeStLine(Thread.currentThread().stackTrace[5])
            val targetStLine = makeStLine(Thread.currentThread().stackTrace[4])
            return listOf(
                "${logPrefix}${LocalDateTime.now()}",
                "${beforeLine} -> ${targetStLine}",
                "${logLevel}\n${con}"
            ).joinToString(" ")
        }

        private fun makeStLine(
            st: StackTraceElement
        ): String {
            val packageNameIndex = 2
            return listOf(
                "L${st.lineNumber}",
                st.className.split(".").filterIndexed {
                        index, _ ->  index > packageNameIndex
                }.joinToString("."),
                st.methodName
            ).joinToString(" ")
        }
    }

    private enum class LogLebel{
        ERROR,
        WARNING
    }

}