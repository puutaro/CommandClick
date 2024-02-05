package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object ScrollYPosiBackUp {
    fun backup(
        terminalFragment: TerminalFragment
    ){
        val takePosiLinesForBackup = ScrollPosition.takePosiLines
        CoroutineScope(Dispatchers.Main).launch {
            val currentAppDirPath = terminalFragment.currentAppDirPath
            val cmdclickSiteScrollPosiDirPath = "${currentAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
            val cmdclickSiteScrollPosiFileName = UsePath.cmdclickSiteScrollPosiFileName
            FileSystems.createDirs(cmdclickSiteScrollPosiDirPath)
            val scrollPosiConList = ReadText(
                File(
                    cmdclickSiteScrollPosiDirPath,
                    cmdclickSiteScrollPosiFileName
                ).absolutePath
            ).textToList()
            val scrollPosiConListSize = scrollPosiConList.size
            val cmdclickSiteScrollPosiBkFileName =
                UsePath.cmdclickSiteScrollPosiBkFileName
            val scrollPosiBackupConList = ReadText(
                File(
                    cmdclickSiteScrollPosiDirPath,
                    cmdclickSiteScrollPosiBkFileName
                ).absolutePath
            ).textToList().take(takePosiLinesForBackup)
            val scrollPosiBuckupConListSize =
                scrollPosiBackupConList.size
            if(
                scrollPosiConListSize < scrollPosiBuckupConListSize
            ) {
                FileSystems.writeFile(
                    File(
                        cmdclickSiteScrollPosiDirPath,
                        cmdclickSiteScrollPosiFileName
                    ).absolutePath,
                    scrollPosiBackupConList.joinToString("\n")
                )
                return@launch
            }
            FileSystems.writeFile(
                File(
                    cmdclickSiteScrollPosiDirPath,
                    cmdclickSiteScrollPosiBkFileName
                ).absolutePath,
                scrollPosiConList.joinToString("\n")
            )
        }
    }
}
