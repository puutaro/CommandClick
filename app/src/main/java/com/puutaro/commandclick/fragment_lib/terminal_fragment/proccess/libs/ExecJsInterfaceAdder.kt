package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs

import android.annotation.SuppressLint
import android.webkit.WebView

object ExecJsInterfaceAdder{

    @SuppressLint("JavascriptInterface")
    fun <T: Any> add(
        webView: WebView,
        classs: T
    ){
        val className =
            convertUseJsInterfaceName(classs::class.java.simpleName)
        webView.addJavascriptInterface(
            classs,
            className
        )
    }

    fun convertUseJsInterfaceName(
        simpleClassName: String,
    ): String {
        return simpleClassName.replaceFirstChar { it.lowercase() }
    }
}