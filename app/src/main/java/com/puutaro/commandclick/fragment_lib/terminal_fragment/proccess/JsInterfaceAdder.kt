package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.webkit.WebView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.CmdIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsArgs
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsBroadcast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCsv
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCurl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDirSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDirect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsEditor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFannelInstaller
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsHtml
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsIconSelectBox
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsLinux
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToolbar
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsListSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMap
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsNetTool
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPdf
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPermission
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPulseAudioReceiver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsQr
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsQrEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsRecordToText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsReplaceVariables
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsScript
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsSendKey
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsStop
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsTextToSpeech
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUbuntu
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsValEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.JsInterfaceVariant

object JsInterfaceAdder {
    fun add(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        webView.addJavascriptInterface(
            JsFileSystem(terminalFragment),
            JsInterfaceVariant.jsFileSystem.name
        )
        webView.addJavascriptInterface(
            JsIntent(terminalFragment),
            JsInterfaceVariant.jsIntent.name
        )

        webView.addJavascriptInterface(
            JsDialog(terminalFragment),
            JsInterfaceVariant.jsDialog.name
        )

        webView.addJavascriptInterface(
            JsArgs(terminalFragment),
            JsInterfaceVariant.jsArgs.name
        )
        webView.addJavascriptInterface(
            CmdIntent(terminalFragment),
            JsInterfaceVariant.cmdIntent.name
        )
        webView.addJavascriptInterface(
            JsCurl(terminalFragment),
            JsInterfaceVariant.jsCurl.name
        )
        webView.addJavascriptInterface(
            JsToast(terminalFragment),
            JsInterfaceVariant.jsToast.name
        )
        webView.addJavascriptInterface(
            JsStop(terminalFragment),
            JsInterfaceVariant.jsStop.name
        )
        webView.addJavascriptInterface(
            JsUtil(terminalFragment),
            JsInterfaceVariant.jsUtil.name
        )
        webView.addJavascriptInterface(
            JsUrl(terminalFragment),
            JsInterfaceVariant.jsUrl.name
        )
        webView.addJavascriptInterface(
            JsScript(terminalFragment),
            JsInterfaceVariant.jsScript.name
        )
        webView.addJavascriptInterface(
            JsListSelect(terminalFragment),
            JsInterfaceVariant.jsListSelect.name
        )
        webView.addJavascriptInterface(
            JsEdit(terminalFragment),
            JsInterfaceVariant.jsEdit.name
        )
        webView.addJavascriptInterface(
            JsValEdit(terminalFragment),
            JsInterfaceVariant.jsValEdit.name
        )
        webView.addJavascriptInterface(
            JsFileSelect(terminalFragment),
            JsInterfaceVariant.jsFileSelect.name
        )
        webView.addJavascriptInterface(
            JsDirSelect(terminalFragment),
            JsInterfaceVariant.jsDirSelect.name
        )
        webView.addJavascriptInterface(
            JsCsv(terminalFragment),
            JsInterfaceVariant.jsCsv.name
        )
        webView.addJavascriptInterface(
            JsText(terminalFragment),
            JsInterfaceVariant.jsText.name
        )
        webView.addJavascriptInterface(
            JsPath(terminalFragment),
            JsInterfaceVariant.jsPath.name
        )
        webView.addJavascriptInterface(
            JsTextToSpeech(terminalFragment),
            JsInterfaceVariant.jsTextToSpeech.name
        )
        webView.addJavascriptInterface(
            JsRecordToText(terminalFragment),
            JsInterfaceVariant.jsRecordToText.name
        )
        webView.addJavascriptInterface(
            JsPdf(terminalFragment),
            JsInterfaceVariant.jsPdf.name
        )
        webView.addJavascriptInterface(
            JsPulseAudioReceiver(terminalFragment),
            JsInterfaceVariant.jsPulseAudioReceiver.name
        )
        webView.addJavascriptInterface(
            JsSendKey(terminalFragment),
            JsInterfaceVariant.jsSendKey.name
        )
        webView.addJavascriptInterface(
            JsNetTool(terminalFragment),
            JsInterfaceVariant.jsNetTool.name
        )
        webView.addJavascriptInterface(
            JsHtml(terminalFragment),
            JsInterfaceVariant.jsHtml.name
        )
        webView.addJavascriptInterface(
            JsUbuntu(terminalFragment),
            JsInterfaceVariant.jsUbuntu.name
        )
        webView.addJavascriptInterface(
            JsLinux(terminalFragment),
            JsInterfaceVariant.jsLinux.name
        )
        webView.addJavascriptInterface(
            JsReplaceVariables(terminalFragment),
            JsInterfaceVariant.jsReplaceVariables.name
        )
        webView.addJavascriptInterface(
            JsPermission(terminalFragment),
            JsInterfaceVariant.jsPermission.name
        )
        webView.addJavascriptInterface(
            JsQr(terminalFragment),
            JsInterfaceVariant.jsQr.name
        )
        webView.addJavascriptInterface(
            JsDirect(terminalFragment),
            JsInterfaceVariant.jsDirect.name
        )
        webView.addJavascriptInterface(
            JsEditor(terminalFragment),
            JsInterfaceVariant.jsEditor.name
        )
        webView.addJavascriptInterface(
            JsQrEdit(terminalFragment),
            JsInterfaceVariant.jsQrEdit.name
        )
        webView.addJavascriptInterface(
            JsBroadcast(terminalFragment),
            JsInterfaceVariant.jsBroadcast.name
        )
        webView.addJavascriptInterface(
            JsFannelInstaller(terminalFragment),
            JsInterfaceVariant.jsFannelInstaller.name
        )
        webView.addJavascriptInterface(
            JsIconSelectBox(terminalFragment),
            JsInterfaceVariant.jsIconSelectBox.name,
        )
        webView.addJavascriptInterface(
            JsMap(terminalFragment),
            JsInterfaceVariant.jsMap.name
        )
        webView.addJavascriptInterface(
            JsFDialog(terminalFragment),
            JsInterfaceVariant.jsFDialog.name
        )
        webView.addJavascriptInterface(
            JsToolbar(terminalFragment),
            JsInterfaceVariant.jsToolbar.name
        )
    }
}