package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.UrlCaptureHistoryTool
import com.puutaro.commandclick.proccess.history.UrlHistoryIconTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object UrlHistoryBackUp {

    fun backup(terminalFragment: TerminalFragment){
        bodyBackup(terminalFragment)
        iconBackup(terminalFragment)
        captureBackup(terminalFragment)
    }
    private fun bodyBackup(
        terminalFragment: TerminalFragment
    ){
        execBackup(
            terminalFragment,
            UsePath.cmdclickUrlHistoryFileName,
            UsePath.cmdclickUrlHistoryBackupFileName,
            ReadText.leavesLineForTerm
        )
    }

    private fun iconBackup(
        terminalFragment: TerminalFragment
    ){
        execBackup(
            terminalFragment,
            UsePath.cmdclickUrlIconFileName,
            UsePath.cmdclickUrlIconBackupFileName,
            UrlHistoryIconTool.takeHistoryNum,
        )
    }

    private fun captureBackup(
        terminalFragment: TerminalFragment
    ){
        execBackup(
            terminalFragment,
            UsePath.cmdclickUrlCaptureFileName,
            UsePath.cmdclickUrlCaptureBackupFileName,
            UrlCaptureHistoryTool.takeHistoryNum,
        )
    }

    private fun execBackup(
        terminalFragment: TerminalFragment,
        backupSrcFileName: String,
        backupDestiFileName: String,
        takeSize: Int,
    ){
        val currentAppDirPath =
            terminalFragment.currentAppDirPath
        val appUrlSystemDirPath =
            File(
                currentAppDirPath,
                UsePath.cmdclickUrlSystemDirRelativePath
            )
        val backupSrcFilePath = File(
            appUrlSystemDirPath,
            backupSrcFileName,
        ).absolutePath
        val backupDestFilePath = File(
            appUrlSystemDirPath,
            backupDestiFileName
        ).absolutePath
        CoroutineScope(Dispatchers.Main).launch {
            val backupSrcLineList = ReadText(
                backupSrcFilePath
            ).textToList()
            val backupSrcLineListSize = backupSrcLineList.size
            val backupDestLineList = ReadText(
                backupDestFilePath
            ).textToList().take(takeSize)
            val backupDestLineListSize =
                backupDestLineList.size
            if(
                backupSrcLineListSize < backupDestLineListSize
            ) {
                FileSystems.writeFile(
                    backupSrcFilePath,
                    backupDestLineList.joinToString("\n")
                )
                return@launch
            }
            FileSystems.writeFile(
                backupDestFilePath,
                backupSrcLineList.joinToString("\n")
            )
        }
    }
}