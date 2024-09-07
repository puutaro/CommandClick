package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.TextToSpeechIntentSender
import com.puutaro.commandclick.service.TextToSpeechService
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.lang.ref.WeakReference

class JsTextToSpeech(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun speech(
        listFilePath: String,
        extraSettingMapStr: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentAppDirName = File(UsePath.cmdclickDefaultAppDirPath).name
        val fannelRawName = CcPathTool.trimAllExtend(currentFannelName)
        TextToSpeechIntentSender.send(
            context,
            currentAppDirName,
            fannelRawName,
            listFilePath,
            extraSettingMapStr,
        )
    }

    @JavascriptInterface
    fun isRun(): Boolean {
        val textToSpeechClassPath = TextToSpeechService::class.java.name
        return ServiceUtils.isServiceRunning(
            textToSpeechClassPath
        )
    }

    @JavascriptInterface
    fun stopService(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        context?.stopService(
            Intent(terminalFragment.activity, TextToSpeechService::class.java)
        )
    }


    @JavascriptInterface
    fun stop(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context

        val textToSpeech = TextToSpeech(context, null)
        textToSpeech.stop()
        textToSpeech.shutdown()

    }
}
