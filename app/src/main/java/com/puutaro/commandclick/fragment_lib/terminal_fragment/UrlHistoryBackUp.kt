package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UrlHistoryBackUp {
    fun backup(
        terminalFragment: TerminalFragment
    ){
        val currentAppDirPath =
            terminalFragment.currentAppDirPath
        CoroutineScope(Dispatchers.IO).launch {
            val appUrlSystemDirPath =
                "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            val cmdclickUrlHistoryFileName =
                UsePath.cmdclickUrlHistoryFileName
            val urlHistoryConList = ReadText(
                appUrlSystemDirPath,
                cmdclickUrlHistoryFileName
            ).textToList()
            val urlHistoryConListSize = urlHistoryConList.size
            val cmdclickUrlHistoryBackupFileName =
                UsePath.cmdclickUrlHistoryBackupFileName
            val urlHistoryBackupConList = ReadText(
                appUrlSystemDirPath,
                cmdclickUrlHistoryBackupFileName
            ).textToList()
            val urlHistoryBuckupConListSize =
                urlHistoryBackupConList.size
            if(
                urlHistoryConListSize == urlHistoryBuckupConListSize
            ) return@launch
            if(
                urlHistoryConListSize < urlHistoryBuckupConListSize
            ) {
                FileSystems.writeFile(
                    appUrlSystemDirPath,
                    cmdclickUrlHistoryFileName,
                    urlHistoryBackupConList.joinToString("\n")
                )
                return@launch
            }
            FileSystems.writeFile(
                appUrlSystemDirPath,
                cmdclickUrlHistoryBackupFileName,
                urlHistoryConList.joinToString("\n")
            )
        }
    }
}