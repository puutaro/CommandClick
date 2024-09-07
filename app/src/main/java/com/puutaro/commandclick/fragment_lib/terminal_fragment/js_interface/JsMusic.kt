package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ServiceUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.MediaPlayerIntentSender
import com.puutaro.commandclick.service.MusicPlayerService
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File
import java.lang.ref.WeakReference

class JsMusic(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun play(
        listFilePath: String,
        extraSettingMapStr: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )

        val currentAppDirName = File(UsePath.cmdclickDefaultAppDirPath).name
        val fannelRawName = CcPathTool.trimAllExtend(currentFannelName)
        MediaPlayerIntentSender.send(
            context,
            currentAppDirName,
            fannelRawName,
            listFilePath,
            extraSettingMapStr,
        )
    }

    @JavascriptInterface
    fun isRun(): Boolean {
        val musicPlayerClassPath = MusicPlayerService::class.java.name
        return ServiceUtils.isServiceRunning(
            musicPlayerClassPath
        )
    }

    @JavascriptInterface
    fun stop(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context

        context?.stopService(
            Intent(terminalFragment.activity, MusicPlayerService::class.java)
        )
    }
}