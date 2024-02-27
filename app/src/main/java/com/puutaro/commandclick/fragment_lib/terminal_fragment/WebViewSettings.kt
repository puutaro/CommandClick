package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.View
import android.view.WindowManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.JsInterfaceAdder


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
        settings.mediaPlaybackRequiresUserGesture = false

        JsInterfaceAdder.add(
            terminalFragment,
            terminalWebView
        )
    }
}

