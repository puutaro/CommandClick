package com.puutaro.commandclick.proccess.intent.lib

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.util.url.HistoryUrlContents
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object UrlLaunchMacro {
    fun launch(
        terminalViewModel: TerminalViewModel,
//        recentAppDirPath: String,
        onUrlLaunchMacro: String,
        curUrl: String?,
    ) {
        val isCurUrlHttp = EnableUrlPrefix.isHttpPrefix(curUrl)
        if(isCurUrlHttp) return
        val launchUrl = HistoryUrlContents.extract(
//            recentAppDirPath,
            onUrlLaunchMacro
        )
        if(launchUrl == curUrl) return
        if(
            !launchUrl.isNullOrEmpty()
        ){
            terminalViewModel.launchUrl = launchUrl
            return
        }
        launchUrl(
            onUrlLaunchMacro,
            terminalViewModel
        )
    }

    fun launchForIndex(
        context: Context?,
    ){
        val launchUrlSrc = HistoryUrlContents.extract(
            SettingVariableSelects.OnUrlLaunchMacroSelects.RECENT.name,
        )
        val launchUrl = when (
            launchUrlSrc.isNullOrEmpty()
        ){
            true -> {
                WebUrlVariables.commandClickUsageUrl
            }
            else -> {
                if(
                    EnableUrlPrefix.isHttpPrefix(launchUrlSrc)
                ) launchUrlSrc
                else WebUrlVariables.commandClickUsageUrl
            }
        }
        BroadCastIntent.sendUrlCon(
            context,
            launchUrl
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