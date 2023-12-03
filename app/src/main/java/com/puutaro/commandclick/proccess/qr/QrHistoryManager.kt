package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText

object QrHistoryManager {

    fun registerQrUriToHistory(
        currentAppDirPath: String,
        title: String,
        selectedQrUri: String,
    ){
        val qrHistoryLimitRowSize = 50
        val qrHistoryParentDirPath =
            "${currentAppDirPath}/${UsePath.cmdclickQrSystemDirRelativePath}"
        val cmdclickQrHistoryFileName = UsePath.cmdclickQrHistoryFileName
        val qrHistoryList = ReadText(
            qrHistoryParentDirPath,
            cmdclickQrHistoryFileName
        ).textToList().take(qrHistoryLimitRowSize)
        val registerHistoryList = listOf("$title\t$selectedQrUri") + qrHistoryList.filter {
            val hisTitleUriList = it.split("\t")
            val hisTitle = hisTitleUriList.firstOrNull() ?: String()
            title != hisTitle
        }
        FileSystems.writeFile(
            qrHistoryParentDirPath,
            cmdclickQrHistoryFileName,
            registerHistoryList.joinToString("\n")
        )
    }
}