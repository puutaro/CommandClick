package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.View
import android.view.WindowManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.*

class WebViewSettings {
    companion object {
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
        }
    }
}


private enum class JsInterfaceVariant {
    jsFileSystem,
    jsIntent,
    jsDialog,
    jsArgs,
    cmdIntent,
    jsCurl,
    jsToast,
    jsStop,
    jsUtil,
}
