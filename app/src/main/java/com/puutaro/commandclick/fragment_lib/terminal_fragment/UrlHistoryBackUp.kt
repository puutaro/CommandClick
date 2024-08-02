package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.UrlIconTool
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
    }
    fun bodyBackup(
        terminalFragment: TerminalFragment
    ){
        val leavesLineForTerm = ReadText.leavesLineForTerm
        val currentAppDirPath =
            terminalFragment.currentAppDirPath
        CoroutineScope(Dispatchers.Main).launch {
            val appUrlSystemDirPath =
                "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            val cmdclickUrlHistoryFileName =
                UsePath.cmdclickUrlHistoryFileName
            val urlHistoryConList = ReadText(
                File(
                    appUrlSystemDirPath,
                    cmdclickUrlHistoryFileName
                ).absolutePath
            ).textToList()
            val urlHistoryConListSize = urlHistoryConList.size
            val cmdclickUrlHistoryBackupFileName =
                UsePath.cmdclickUrlHistoryBackupFileName
            val urlHistoryBackupConList = ReadText(
                File(
                    appUrlSystemDirPath,
                    cmdclickUrlHistoryBackupFileName
                ).absolutePath
            ).textToList().take(leavesLineForTerm)
            val urlHistoryBuckupConListSize =
                urlHistoryBackupConList.size
            if(
                urlHistoryConListSize < urlHistoryBuckupConListSize
            ) {
                FileSystems.writeFile(
                    File(
                        appUrlSystemDirPath,
                        cmdclickUrlHistoryFileName,
                    ).absolutePath,
                    urlHistoryBackupConList.joinToString("\n")
                )
                return@launch
            }
            FileSystems.writeFile(
                File(
                    appUrlSystemDirPath,
                    cmdclickUrlHistoryBackupFileName,
                ).absolutePath,
                urlHistoryConList.joinToString("\n")
            )
        }
    }

    fun iconBackup(
        terminalFragment: TerminalFragment
    ){
        val takeHistoryNum = UrlIconTool.takeHistoryNum
        val currentAppDirPath =
            terminalFragment.currentAppDirPath
        CoroutineScope(Dispatchers.Main).launch {
            val appUrlSystemDirPath =
                "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            val cmdclickUrlIconFileName =
                UsePath.cmdclickUrlIconFileName
            val urlIconList = ReadText(
                File(
                    appUrlSystemDirPath,
                    cmdclickUrlIconFileName
                ).absolutePath
            ).textToList()
            val urlIconListSize = urlIconList.size
            val cmdclickUrlIconBackupFileName =
                UsePath.cmdclickUrlIconBackupFileName
            val urlIconBackupList = ReadText(
                File(
                    appUrlSystemDirPath,
                    cmdclickUrlIconBackupFileName
                ).absolutePath
            ).textToList().take(takeHistoryNum)
            val urlIconBuckupListSize =
                urlIconBackupList.size
            if(
                urlIconListSize < urlIconBuckupListSize
            ) {
                FileSystems.writeFile(
                    File(
                        appUrlSystemDirPath,
                        cmdclickUrlIconFileName,
                    ).absolutePath,
                    urlIconBackupList.joinToString("\n")
                )
                return@launch
            }
            FileSystems.writeFile(
                File(
                    appUrlSystemDirPath,
                    cmdclickUrlIconBackupFileName,
                ).absolutePath,
                urlIconList.joinToString("\n")
            )
        }
    }
}