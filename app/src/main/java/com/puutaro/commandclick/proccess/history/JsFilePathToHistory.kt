package com.puutaro.commandclick.proccess.history

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FDialogTempFile
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object JsFilePathToHistory {
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
        val jsFullPathObj = File(jsFullPath)
        if(
            !jsFullPathObj.isFile
        ) return
        val isFDialogFannelUri =
            FDialogTempFile.howFDialogFile(
                jsFullPathObj.name
            )
        if(isFDialogFannelUri) return
        val cmdclickUrlHistoryFilePath = File(
            appUrlSystemPath,
            cmdclickUrlHistoryFileName
        ).absolutePath
        val insertedHistoryContentsList = listOf("${jsFullPath}\t${jsFullPath}") + ReadText(
            cmdclickUrlHistoryFilePath
        ).textToList()
        FileSystems.writeFile(
            cmdclickUrlHistoryFilePath,
            insertedHistoryContentsList.joinToString("\n")
        )
    }
}