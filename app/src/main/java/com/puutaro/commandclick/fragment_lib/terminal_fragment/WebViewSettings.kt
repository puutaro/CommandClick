package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.View
import android.view.WindowManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.CmdIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsArgs
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUbuntu
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCsv
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCurl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDirSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsHtml
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsListSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsNetTool
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPdf
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPulseAudioReceiver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsRecordToText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsScript
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsSendKey
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsStop
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsTextToSpeech
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.JsInterfaceVariant


object WebViewSettings {
    fun set(
        terminalFragment: TerminalFragment
    ) {
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        val settings = terminalWebView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        terminalWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        terminalFragment.activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        terminalWebView.addJavascriptInterface(
            JsFileSystem(terminalFragment),
            JsInterfaceVariant.jsFileSystem.name
        )
        terminalWebView.addJavascriptInterface(
            JsIntent(terminalFragment),
            JsInterfaceVariant.jsIntent.name
        )

        terminalWebView.addJavascriptInterface(
            JsDialog(terminalFragment),
            JsInterfaceVariant.jsDialog.name
        )

        terminalWebView.addJavascriptInterface(
            JsArgs(terminalFragment),
            JsInterfaceVariant.jsArgs.name
        )
        terminalWebView.addJavascriptInterface(
            CmdIntent(terminalFragment),
            JsInterfaceVariant.cmdIntent.name
        )
        terminalWebView.addJavascriptInterface(
            JsCurl(terminalFragment),
            JsInterfaceVariant.jsCurl.name
        )
        terminalWebView.addJavascriptInterface(
            JsToast(terminalFragment),
            JsInterfaceVariant.jsToast.name
        )
        terminalWebView.addJavascriptInterface(
            JsStop(terminalFragment),
            JsInterfaceVariant.jsStop.name
        )
        terminalWebView.addJavascriptInterface(
            JsUtil(terminalFragment),
            JsInterfaceVariant.jsUtil.name
        )
        terminalWebView.addJavascriptInterface(
            JsUrl(terminalFragment),
            JsInterfaceVariant.jsUrl.name
        )
        terminalWebView.addJavascriptInterface(
            JsScript(terminalFragment),
            JsInterfaceVariant.jsScript.name
        )
        terminalWebView.addJavascriptInterface(
            JsListSelect(terminalFragment),
            JsInterfaceVariant.jsListSelect.name
        )
        terminalWebView.addJavascriptInterface(
            JsEdit(terminalFragment),
            JsInterfaceVariant.jsEdit.name
        )
        terminalWebView.addJavascriptInterface(
            JsFileSelect(terminalFragment),
            JsInterfaceVariant.jsFileSelect.name
        )
        terminalWebView.addJavascriptInterface(
            JsDirSelect(terminalFragment),
            JsInterfaceVariant.jsDirSelect.name
        )
        terminalWebView.addJavascriptInterface(
            JsCsv(terminalFragment),
            JsInterfaceVariant.jsCsv.name
        )
        terminalWebView.addJavascriptInterface(
            JsText(terminalFragment),
            JsInterfaceVariant.jsText.name
        )
        terminalWebView.addJavascriptInterface(
            JsPath(terminalFragment),
            JsInterfaceVariant.jsPath.name
        )
        terminalWebView.addJavascriptInterface(
            JsTextToSpeech(terminalFragment),
            JsInterfaceVariant.jsTextToSpeech.name
        )
        terminalWebView.addJavascriptInterface(
            JsRecordToText(terminalFragment),
            JsInterfaceVariant.jsRecordToText.name
        )
        terminalWebView.addJavascriptInterface(
            JsPdf(terminalFragment),
            JsInterfaceVariant.jsPdf.name
        )
        terminalWebView.addJavascriptInterface(
            JsPulseAudioReceiver(terminalFragment),
            JsInterfaceVariant.jsPulseAudioReceiver.name
        )
        terminalWebView.addJavascriptInterface(
            JsSendKey(terminalFragment),
            JsInterfaceVariant.jsSendKey.name
        )
        terminalWebView.addJavascriptInterface(
            JsNetTool(terminalFragment),
            JsInterfaceVariant.jsNetTool.name
        )
        terminalWebView.addJavascriptInterface(
            JsHtml(terminalFragment),
            JsInterfaceVariant.jsHtml.name
        )
        terminalWebView.addJavascriptInterface(
            JsUbuntu(terminalFragment),
            JsInterfaceVariant.jsUbuntu.name
        )
//        terminalWebView.addJavascriptInterface(
//            JsTrans(terminalFragment),
//            JsInterfaceVariant.jsTrans.name
//        )
    }
}

