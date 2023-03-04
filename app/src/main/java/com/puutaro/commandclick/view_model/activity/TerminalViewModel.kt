package com.puutaro.commandclick.view_model.activity

import androidx.lifecycle.ViewModel
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class TerminalViewModel: ViewModel() {

    var readlinesNum: Float = ReadLines.SHORTH
    var currentMonitorFileName = try {
        makeDetectCurrentMonitorFileName()
    } catch (e: Exception){
        UsePath.cmdClickMonitorFileName_1
    }
    var onDisplayUpdate = true
    var onBottomScrollbyJs = true
    var editExecuteOnceCurrentShellFileName: String? = null
    var launchUrl: String? = null
    var onExecInternetButtonShell = false
    var goBackFlag = false
}


internal fun makeDetectCurrentMonitorFileName(): String {
    deleteInvalieMonitorFile()
    val recentUpdatedFileSource = FileSystems.sortedFiles(
        UsePath.cmdclickMonitorDirPath,
        "on"
    ).firstOrNull() ?: UsePath.cmdClickMonitorFileName_1
    return if(recentUpdatedFileSource.isEmpty()) {
        UsePath.cmdClickMonitorFileName_1
    } else {
        recentUpdatedFileSource
    }
}


internal fun deleteInvalieMonitorFile(){
    val validMonitorFileList = listOf(
        UsePath.cmdClickMonitorFileName_1,
        UsePath.cmdClickMonitorFileName_2,
        UsePath.cmdClickMonitorFileName_3,
        UsePath.cmdClickMonitorFileName_4
    )

    val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
    val cmdclickMonitorDirPathFiles = File(
        cmdclickMonitorDirPath
    ).listFiles()?.map{ it.name } ?: return
    cmdclickMonitorDirPathFiles.forEach {
        if(
            validMonitorFileList.contains(it)
        ) return@forEach
        FileSystems.removeFiles(
            cmdclickMonitorDirPath,
            it
        )
    }
}
