package com.puutaro.commandclick.proccess.history

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object UrlHistoryRegister {
    fun insertJsPath(
        recentAppDirPath: String,
        jsFileName: String,
    ) {
//        val appUrlSystemPath = "${recentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
//        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        if(
            jsFileName == UsePath.cmdclickStartupJsName
        ) return
        val jsFullPath = "${recentAppDirPath}/${jsFileName}"
        val jsFullPathObj = File(jsFullPath)
        if(
            !jsFullPathObj.isFile
        ) return
        insert(
            recentAppDirPath,
            jsFullPath,
            jsFullPath,
        )
    }

    fun insert(
        recentAppDirPath: String,
        title: String,
        uri: String,
    ){
        val appUrlSystemPath = "${recentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val cmdclickUrlHistoryFilePath = File(appUrlSystemPath, cmdclickUrlHistoryFileName).absolutePath
        val takeHistoryNum = 500
        val updatingHistoryCon =
            "${title}\t${uri}\n" + ReadText(
                cmdclickUrlHistoryFilePath
            ).textToList().take(takeHistoryNum).joinToString("\n")
        FileSystems.writeFile(
            cmdclickUrlHistoryFilePath,
            updatingHistoryCon
        )
    }
}