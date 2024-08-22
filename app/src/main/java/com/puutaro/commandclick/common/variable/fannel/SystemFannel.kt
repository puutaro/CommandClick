package com.puutaro.commandclick.common.variable.fannel

object SystemFannel {
    const val cmdTerminal = "cmdTerminal.js"
//    val jsImportManagerFannelName = "jsImportManager.js"
//    val appDirManagerFannelName = "appDirManager.js"
//    val fannelRepoFannelName = "fannelRepo.js"
    val webSearcher = "webSearcher.js"
    val textToSpeech = "textToSpeech.js"
    val cmdBookmaker = "cmdBookmaker.js"

    val allowIntentSystemFannelList = listOf(
        cmdTerminal,
//        fannelRepoFannelName
    )

    val firstPinFannelList = listOf(
        textToSpeech,
        cmdBookmaker,
    )
}