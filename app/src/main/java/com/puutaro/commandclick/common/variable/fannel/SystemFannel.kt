package com.puutaro.commandclick.common.variable.fannel

object SystemFannel {
    const val cmdTerminal = "cmdTerminal.js"
//    val jsImportManagerFannelName = "jsImportManager.js"
//    val appDirManagerFannelName = "appDirManager.js"
//    val fannelRepoFannelName = "fannelRepo.js"
    val home = "system.js"
    val webSearcher = "webSearcher.js"
    val textToSpeech = "textToSpeech.js"
    val cmdBookmaker = "cmdBookmaker.js"
    val copyLink = "copyLink.js"
    val preference = "preference.js"
    val qrLReader = "qrLReader.js"
    val saveGmailConDialog = "saveGmailConDialog.js"
    val savePageUrlDialog = "savePageUrlDialog.js"
    val saveWebConDialog = "saveWebConDialog.js"
    val urlIntender = "urlIntender.js"

    fun convertDisplayNameToFannelName(
        fannelName: String
    ): String {
        return when(fannelName == home){
            true -> "HOME"
            else -> fannelName
        }
    }

    val maskListForFannelManageList = listOf(
        webSearcher,
        copyLink,
        preference,
        qrLReader,
        saveGmailConDialog,
        savePageUrlDialog,
        saveWebConDialog,
        urlIntender
    )

    val allowIntentSystemFannelList = listOf(
        cmdTerminal,
//        fannelRepoFannelName
    )
}