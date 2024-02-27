package com.puutaro.commandclick.proccess.intent.lib

import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.util.url.HistoryUrlContents
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object UrlLaunchMacro {
    fun launch(
        terminalViewModel: TerminalViewModel,
        recentAppDirPath: String,
        onUrlLaunchMacro: String,
    ) {
        val launchUrl = HistoryUrlContents.extract(
            recentAppDirPath,
            onUrlLaunchMacro
        )
        if(!launchUrl.isNullOrEmpty()){
            terminalViewModel.launchUrl = launchUrl
            return
        }
        launchUrl(
            onUrlLaunchMacro,
            terminalViewModel
        )
    }

    private fun launchUrl(
        urlStr: String,
        terminalViewModel: TerminalViewModel,
    ){
        if(
            !urlStr.startsWith(
                WebUrlVariables.httpsPrefix
            )
            && !urlStr.startsWith(
                WebUrlVariables.httpPrefix
            )
        ) return
        terminalViewModel.launchUrl = urlStr
    }

}