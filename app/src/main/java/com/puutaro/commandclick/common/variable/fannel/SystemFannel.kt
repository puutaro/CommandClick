package com.puutaro.commandclick.common.variable.fannel

object SystemFannel {
    const val tapTerminal = "tapTerminal.js"
    val home = "system.js"
    val webSearcher = "webSearcher.js"
    val textToSpeech = "textToSpeech.js"
    val cmdBookmaker = "bookmaker.js"
    val ggleTranslator = "ggleTranslator.js"
    val copyLink = "copyLink.js"
    val shareImage = "shareImage.js"
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
//        webSearcher,
        copyLink,
        preference,
        qrLReader,
        saveGmailConDialog,
        savePageUrlDialog,
        saveWebConDialog,
        urlIntender,
        shareImage,
    )

    val allowIntentSystemFannelList = listOf(
        tapTerminal,
//        fannelRepoFannelName
    )
}