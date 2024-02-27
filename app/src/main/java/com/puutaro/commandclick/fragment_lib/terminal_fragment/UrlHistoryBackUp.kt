package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object UrlHistoryBackUp {
    fun backup(
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
}