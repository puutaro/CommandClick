package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object QrHistoryManager {

    fun registerQrUriToHistory(
//        currentAppDirPath: String,
        title: String,
        selectedQrUri: String,
    ){
        val qrHistoryLimitRowSize = 100
        val qrHistoryParentDirPath =
            "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickQrSystemDirRelativePath}"
        val cmdclickQrHistoryFileName = UsePath.cmdclickQrHistoryFileName
        val qrHistoryList = ReadText(
            File(
                qrHistoryParentDirPath,
                cmdclickQrHistoryFileName
            ).absolutePath
        ).textToList().take(qrHistoryLimitRowSize)
        val registerTitle = title.replace("\n", "").replace("\t", " ")
        val registerQUri = selectedQrUri.replace("\n", "")
        val registerTitleConLine = "$registerTitle\t$registerQUri"
        val registerHistoryList = listOf(registerTitleConLine) + qrHistoryList.filter {
            val hisTitleUriList = it.split("\t")
            val hisTitle = hisTitleUriList.firstOrNull() ?: String()
            registerTitle != hisTitle
        }
        FileSystems.writeFile(
            File(
                qrHistoryParentDirPath,
                cmdclickQrHistoryFileName
            ).absolutePath,
            registerHistoryList.joinToString("\n")
        )
    }
}