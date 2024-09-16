package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PocketWebViewLancher {

    fun launch(
        terminalFragment: TerminalFragment,
        url: String?,
    ){
        if (
            url.isNullOrEmpty()
        ) return
        terminalFragment.onRegisterPocketWebViewUrl?.cancel()
        terminalFragment.onRegisterPocketWebViewUrl = CoroutineScope(Dispatchers.Main).launch pocketLaunch@ {
            val webSearcherName = SystemFannel.webSearcher
            val systemExecRepTextList = listOf(url)
            val isStop = withContext(Dispatchers.IO){
                for(i in 1..5){
                    if(
                        terminalFragment.activity == null
                        || terminalFragment.pocketWebViewManager == null
                    ) continue
                    return@withContext false
                }
                true
            }
            if(isStop) return@pocketLaunch
            withContext(Dispatchers.Main){
                ExecJsLoad.execExternalJs(
                    terminalFragment,
                    webSearcherName,
                    systemExecRepTextList
                )
            }
        }
    }

//    fun preLoadUrl(
//        terminalFragment: TerminalFragment,
//        url: String?,
//    ){
//        if(
//            url.isNullOrEmpty()
//            || terminalFragment.pocketWebViewManager == null
//        ) return
//        terminalFragment.pocketWebViewManager?.preLoadUrlHandler(
//            terminalFragment,
//            url,
//        )
//    }

    fun loadUrl(
        terminalFragment: TerminalFragment,
        url: String?,
    ){
        if(
            url.isNullOrEmpty()
            || terminalFragment.pocketWebViewManager == null
        ) return
        terminalFragment.pocketWebViewManager?.loadUrlHandler(
            terminalFragment,
            url,
        )
    }
}