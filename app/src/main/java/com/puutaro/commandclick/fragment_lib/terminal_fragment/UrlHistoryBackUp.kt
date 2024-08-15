package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object UrlHistoryBackUp {

    fun backup(terminalFragment: TerminalFragment){
        bodyBackup(terminalFragment)
        trimLogoBase64TxtFiles(terminalFragment)
        trimCaptureBase64TxtFiles(terminalFragment)
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

    private fun trimLogoBase64TxtFiles(
        terminalFragment: TerminalFragment
    ){
        val captureHistoryPngDirPath =
            UrlHistoryPath.makeCaptureHistoryDirPath(
                terminalFragment.currentAppDirPath
            )
        execTrimFiles(captureHistoryPngDirPath)
    }

    private fun trimCaptureBase64TxtFiles(
        terminalFragment: TerminalFragment
    ){
        val captureHistoryPngDirPath =
            UrlHistoryPath.makeCaptureHistoryDirPath(
                terminalFragment.currentAppDirPath
            )
        execTrimFilesAndDir(captureHistoryPngDirPath)
    }

    private fun execTrimFiles(
        targetHistoryFileDirPath: String
    ){
        val lastModifiedCapturePngPathList =
            FileSystems.sortedFiles(
                targetHistoryFileDirPath,
                "on"
            )
        val totalNum = lastModifiedCapturePngPathList.size
        val limitNum = 200
        val removeNum = totalNum - limitNum
        if(removeNum <= 0) return
        lastModifiedCapturePngPathList.takeLast(removeNum).forEach {
            FileSystems.removeFiles(
                File(targetHistoryFileDirPath, it).absolutePath
            )
        }
    }

    private fun execTrimFilesAndDir(
        targetHistoryFileDirPath: String
    ){
        val lastModifyExtend = UrlHistoryPath.lastModifyExtend
        val lastModifiedCapturePngPathList =
            FileSystems.sortedFiles(
                targetHistoryFileDirPath,
                "on",
            ).filter {
                it.endsWith(lastModifyExtend)
            }
        val totalNum = lastModifiedCapturePngPathList.size
        val limitNum = 100
        val removeNum = totalNum - limitNum
        if(removeNum <= 0) return
        lastModifiedCapturePngPathList.takeLast(removeNum).forEach {
            lastModifiedFileName ->
            FileSystems.removeFiles(
                File(targetHistoryFileDirPath, lastModifiedFileName).absolutePath
            )
            val dirName = lastModifiedFileName.removeSuffix(lastModifyExtend)
            FileSystems.removeDir(
                File(targetHistoryFileDirPath, dirName).absolutePath
            )
        }
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