package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface

class JsUtil {

    @JavascriptInterface
    fun sleep(sleepMiriTime: Int){
        Thread.sleep(sleepMiriTime.toLong())
    }
}