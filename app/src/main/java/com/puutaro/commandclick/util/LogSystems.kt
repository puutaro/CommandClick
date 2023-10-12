package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath
import java.time.LocalDateTime

object LogSystems {

    private val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    private val sysLogFileName = UsePath.cmdClickMonitorFileName_2


    fun stdSys(
        logContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            sysLogFileName,
            "### ${LocalDateTime.now()} ${st.className} ${st.methodName}\n${logContents}"
        )
    }

    fun stdErr(
        errContents: String,
    ){
        val st = Thread.currentThread().stackTrace[3]
        FileSystems.updateFile(
            cmdclickMonitorDirPath,
            sysLogFileName,
            "### ${LocalDateTime.now()} ${st.className} ${st.methodName} ERROR\n${errContents}"
        )
    }

}