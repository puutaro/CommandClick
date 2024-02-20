package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object LogSystems {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private val sysLogFileName = UsePath.cmdClickMonitorFileName_2


    fun stdSys(
        logContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "### ${LocalDateTime.now()} ${st.className} ${st.methodName}\n${logContents}"
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
        errContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        val line = "### ${LocalDateTime.now()} ${st.className} ${st.methodName} ERROR\n${errContents}"
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
        val line = "### ${LocalDateTime.now()} ${st.className} ${st.methodName} WARNING\n${errContents}"
        FileSystems.updateFile(
            File(
                cmdclickMonitorDirPath,
                sysLogFileName
            ).absolutePath,
            line
        )
    }

}