package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.UsePath
import java.io.File

class JsFilePathToHistory {
    companion object {
        fun insert(
            recentAppDirPath: String,
            jsFileName: String,
        ) {
            val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
            val jsFullPath = "${recentAppDirPath}/${jsFileName}"
            if(
                !File(jsFullPath).isFile
            ) return
            val insertedHistoryContentsList = listOf("${jsFullPath}\t${jsFullPath}") + ReadText(
                recentAppDirPath,
                cmdclickUrlHistoryFileName
            ).textToList()
            FileSystems.writeFile(
                recentAppDirPath,
                cmdclickUrlHistoryFileName,
                insertedHistoryContentsList.joinToString("\n")
            )
        }
    }
}