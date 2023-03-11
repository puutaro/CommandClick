package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.UsePath
import java.io.File

class JsFilePathToHistory {
    companion object {
        fun insert(
            recentAppDirPath: String,
            jsFileName: String,
        ) {
            val appUrlSystemPath = "${recentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
            if(
                jsFileName == UsePath.cmdclickStartupJsName
            ) return
            val jsFullPath = "${recentAppDirPath}/${jsFileName}"
            if(
                !File(jsFullPath).isFile
            ) return
            val insertedHistoryContentsList = listOf("${jsFullPath}\t${jsFullPath}") + ReadText(
                appUrlSystemPath,
                cmdclickUrlHistoryFileName
            ).textToList()
            FileSystems.writeFile(
                appUrlSystemPath,
                cmdclickUrlHistoryFileName,
                insertedHistoryContentsList.joinToString("\n")
            )
        }
    }
}