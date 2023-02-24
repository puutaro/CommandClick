package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.View
import android.view.WindowManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.WebAppInterface

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
            terminalFragment.activity?.getWindow()?.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
            settings.setBuiltInZoomControls(true)
            settings.setDisplayZoomControls(false)
            terminalWebView.addJavascriptInterface(
                WebAppInterface(terminalFragment),
                JsInterfaceVariant.jsFileSystem.name
            )

        }
    }
}


private enum class JsInterfaceVariant {
    jsFileSystem
}
