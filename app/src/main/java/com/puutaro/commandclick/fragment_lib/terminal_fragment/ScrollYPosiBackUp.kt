package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                cmdclickSiteScrollPosiDirPath,
                cmdclickSiteScrollPosiFileName
            ).textToList()
            val scrollPosiConListSize = scrollPosiConList.size
            val cmdclickSiteScrollPosiBkFileName =
                UsePath.cmdclickSiteScrollPosiBkFileName
            val scrollPosiBackupConList = ReadText(
                cmdclickSiteScrollPosiDirPath,
                cmdclickSiteScrollPosiBkFileName
            ).textToList().take(takePosiLinesForBackup)
            val scrollPosiBuckupConListSize =
                scrollPosiBackupConList.size
            if(
                scrollPosiConListSize < scrollPosiBuckupConListSize
            ) {
                FileSystems.writeFile(
                    cmdclickSiteScrollPosiDirPath,
                    cmdclickSiteScrollPosiFileName,
                    scrollPosiBackupConList.joinToString("\n")
                )
                return@launch
            }
            FileSystems.writeFile(
                cmdclickSiteScrollPosiDirPath,
                cmdclickSiteScrollPosiBkFileName,
                scrollPosiConList.joinToString("\n")
            )
        }
    }
}
