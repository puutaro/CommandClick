package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object MonitorFileManager {

    val monitorFileList = listOf(
        UsePath.cmdClickMonitorFileName_1,
        UsePath.cmdClickMonitorFileName_2,
        UsePath.cmdClickMonitorFileName_3,
        UsePath.cmdClickMonitorFileName_4,
    )
    fun trim(
        terminalViewModel: TerminalViewModel,
    ){
        val trimSize = 2000
        val cmdclickMonitorDirPath = UsePath.cmdclickMonitorDirPath
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                FileSystems.sortedFiles(cmdclickMonitorDirPath).filter {
                    !monitorFileList.contains(it)
                }.map{
                    FileSystems.removeFiles(
                        File(cmdclickMonitorDirPath, it).absolutePath
                    )
                }
            }
            val trimMonitorFileList = withContext(Dispatchers.IO) {
                monitorFileList.filter {
                    it != currentMonitorFileName
                }
            }
            withContext(Dispatchers.IO) {
                trimMonitorFileList.forEach {
                    if (
                        !File(
                            cmdclickMonitorDirPath,
                            it
                        ).isFile
                    ) return@forEach
                    val currentMonitorFilePath = File(
                        cmdclickMonitorDirPath,
                        it,
                    ).absolutePath
                    val trimedMonitorCon = ReadText(
                        currentMonitorFilePath,
                    ).textToList().takeLast(trimSize).joinToString("\n")
                    FileSystems.writeFile(
                        currentMonitorFilePath,
                        trimedMonitorCon,
                    )
                }
            }
        }
    }

    fun switchCurMonitorFile(
        terminalFragment: TerminalFragment,
        terminalViewModel: TerminalViewModel
    ){
        terminalViewModel.currentMonitorFileName = terminalFragment.defaultMonitorFile
    }
}