package com.puutaro.commandclick.proccess.history.url_history

import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object UrlHistoryRegister {

    private const val takeHistoryNum = 500
    fun insertJsPath(
//        recentAppDirPath: String,
        jsFileName: String,
    ) {
        if(
            jsFileName == SystemFannel.preference
        ) return
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val jsFullPath = "${cmdclickDefaultAppDirPath}/${jsFileName}"
        val jsFullPathObj = File(jsFullPath)
        if(
            !jsFullPathObj.isFile
        ) return
        insert(
//            cmdclickDefaultAppDirPath,
            jsFullPath,
            jsFullPath,
        )
    }

    fun insert(
//        recentAppDirPath: String,
        title: String,
        uri: String,
    ){
        val appUrlSystemPath = "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val cmdclickUrlHistoryFilePath = File(appUrlSystemPath, cmdclickUrlHistoryFileName).absolutePath
        val updatingHistoryCon =
            "${title}\t${uri}\n" + ReadText(
                cmdclickUrlHistoryFilePath
            ).textToList().take(takeHistoryNum).joinToString("\n")
        FileSystems.writeFile(
            cmdclickUrlHistoryFilePath,
            updatingHistoryCon
        )
    }

    fun insertByUnique(
        recentAppDirPath: String,
        title: String,
        uri: String,
    ){
        val appUrlSystemPath = "${recentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        val cmdclickUrlHistoryFileName = UsePath.cmdclickUrlHistoryFileName
        val cmdclickUrlHistoryFilePath = File(appUrlSystemPath, cmdclickUrlHistoryFileName).absolutePath
        val insertLine = "${title}\t${uri}"
        val historyConList = ReadText(
            cmdclickUrlHistoryFilePath
        ).textToList().take(takeHistoryNum)
        val isContainFirstHistoryLine =
            getUrlFromLine(
                historyConList.firstOrNull()
            ) == uri
        if(
            isContainFirstHistoryLine
        ) return
        val updatingHistoryCon =
            "${insertLine}\n" + historyConList.filter {
                getUrlFromLine(it) != uri
            }.joinToString("\n")
        FileSystems.writeFile(
            cmdclickUrlHistoryFilePath,
            updatingHistoryCon
        )
    }


    private fun getUrlFromLine(historyLine: String?): String? {
        return historyLine?.split("\t")?.getOrNull(2)
    }
}